package kr.co.koscom.omp.ui.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.sendbird.syncmanager.utils.PreferenceUtils
import isMe
import kotlinx.android.synthetic.main.activity_order_detail_new.*
import kr.co.koscom.omp.BuyWriteActivity
import kr.co.koscom.omp.NavigationFragment
import kr.co.koscom.omp.R
import kr.co.koscom.omp.WebFragment
import kr.co.koscom.omp.adapter.OrderDetailPagerAdapter
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.NegotiationData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.enums.NegotiationEnterType
import kr.co.koscom.omp.enums.OrderDetailTab
import kr.co.koscom.omp.enums.TransactionTargetType
import kr.co.koscom.omp.extension.isY
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.ui.order.contract.OrderDetailNewContract
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.ViewUtils
import toStockTypeCodeName

class OrderDetailNewActivity : AppCompatActivity(), OrderDetailNewContract.View {
    internal val tag = this.javaClass.simpleName

    private lateinit var viewModelFactory: ViewModelFactory

    private val adapterNegoDetail: OrderDetailPagerAdapter by lazy{
        OrderDetailPagerAdapter(supportFragmentManager, lifecycle, object: OrderDetailPagerAdapter.OnNegoPagerClickListener{
            override fun onContractRequestClick(item: Any) {
                if(item is Order.OrderItem){
                    if(!item.PUBLIC_YN.isY()){
                        //비공개
                        if(item.PBLS_CLNT_NO.isMe()){
                            ViewUtils.alertDialog(this@OrderDetailNewActivity, R.string.orderpopup_popup_contents1.toResString()){}
                            return
                        }

                        ActivityUtil.startNegotiationPopupActivity(this@OrderDetailNewActivity, NegotiationData().apply {
                            orderNo = item.ORDER_NO ?:""
                            enterType = NegotiationEnterType.DETAIL
                            transactionTargetType = if(item.SECRET_CERTI_YN == "Y") TransactionTargetType.SPECIFIC else TransactionTargetType.UNSPECIFIC
                            registNickName = item.NICKNAME ?: ""
                            stockTpCodeNm = item.STOCK_TP_CODE_NM?: ""
                        })

                    }else{
                        //공개
                        presenter.requestNego(item.ORDER_NO?: "")
                    }
                }
            }
        })
    }

    private val presenter: OrderDetailNewPresenter by lazy {
        OrderDetailNewPresenter(this)
    }

    private lateinit var webFragment: WebFragment

    private lateinit var orderData: Order.OrderItem
    private var entpNo = ""
    private var stkCode = ""
    private var orderNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail_new)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            webFragment = WebFragment()
            transaction.replace(R.id.layoutWeb, webFragment)
            transaction.commitAllowingStateLoss()
        }


        viewModelFactory = Injection.provideViewModelFactory(this)

        init()
        initListener()

        presenter.initViewModel(viewModelFactory)
        presenter.getTradeList(stkCode)

    }

    private fun init(){

        toolbar.initTitle(R.string.orderlist_title.toResString())
        toolbar.initData(this)

        orderData = intent.getSerializableExtra(Keys.INTENT_ORDER_ITEM) as Order.OrderItem
        entpNo = orderData.ENTP_NO
        stkCode = orderData.STK_CODE

        if(orderData == null){
            ViewUtils.alertDialog(this, R.string.no_data_contents.toResString()){
                finish()
                overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
            }
        }

        vpNego.apply {
            offscreenPageLimit = 2
            adapter = adapterNegoDetail
        }

    }

    private fun initListener(){

        ivSearch.setOnClickListener {
            ActivityUtil.startSearch2ActivityResult(this, BuyWriteActivity.STOCK_SEARCH)
        }

        ivStar.setOnClickListener {
            presenter.favoriteCorp(orderData.ENTP_NO, !ivStar.isSelected)
        }

        layoutLeftArrow.setOnClickListener {
            val page = vpNego.currentItem-1
            if(page>=0){
                vpNego.currentItem = page
            }else{
                vpNego.currentItem = (vpNego.adapter?.itemCount ?: 0)-1
            }
        }

        layoutRightArrow.setOnClickListener {
            val page = vpNego.currentItem+1
            if(page <= (vpNego.adapter?.itemCount ?: 0) -1){
                vpNego.currentItem = page
            }else{
                vpNego.currentItem = 0
            }
        }


        vpNego.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setHashTagData(position)
                setFavoriteData(!adapterNegoDetail.getOrderList()[position].FAV_CORP_NO.isNullOrEmpty())
                setTopTitle(adapterNegoDetail.getOrderList()[position])
            }
        })


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                loadWeb(OrderDetailTab.getType(tab?.position ?: 0))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadWeb(OrderDetailTab.getType(tab?.position ?: 0))
            }
        })


        layoutBottomArrow.setOnClickListener {
            onClickBottomArrow(!layoutBottomArrow.isSelected)
        }
    }


    private fun loadWeb(tabType: OrderDetailTab){
        val listData = adapterNegoDetail.getOrderList()
        when(tabType){
            OrderDetailTab.ORDER_DETAIL -> webFragment.loadUrl(Constants.URL_ORDER_DETAIL + "?orderNo=${listData[vpNego.currentItem].ORDER_NO}&rqtClientNo=${listData[vpNego.currentItem].PBLS_CLNT_NO}&orderStkCode=${listData[vpNego.currentItem].STK_CODE}")
            OrderDetailTab.STOCK_CONCLUSION -> webFragment.loadUrl(Constants.URL_STOCK_SUC_INFO + "?orderNo=${listData[vpNego.currentItem].ORDER_NO}&rqtClientNo=${listData[vpNego.currentItem].PBLS_CLNT_NO}&orderStkCode=${listData[vpNego.currentItem].STK_CODE}")
            OrderDetailTab.ORDER_CONCLUSION -> webFragment.loadUrl(Constants.URL_ORDER_SUC_INFO + "?orderNo=${listData[vpNego.currentItem].ORDER_NO}&rqtClientNo=${listData[vpNego.currentItem].PBLS_CLNT_NO}&orderStkCode=${listData[vpNego.currentItem].STK_CODE}")
            OrderDetailTab.EXPERT_INFO -> webFragment.loadUrl(Constants.URL_EXPERT_INFO + "?orderNo=${listData[vpNego.currentItem].ORDER_NO}&rqtClientNo=${listData[vpNego.currentItem].PBLS_CLNT_NO}&orderStkCode=${listData[vpNego.currentItem].STK_CODE}")
            OrderDetailTab.DISCUSSION_BOARD -> webFragment.loadUrl(Constants.URL_ORDERINFO_DISCUSSION_BOARD + "?orderNo=${listData[vpNego.currentItem].ORDER_NO}&rqtClientNo=${listData[vpNego.currentItem].PBLS_CLNT_NO}&orderStkCode=${listData[vpNego.currentItem].STK_CODE}&LOGIN_ID=${PreferenceUtils.getUserId()}")
        }

    }

    /**
     * Arrow 접기/펼치기
     */
    private fun onClickBottomArrow(isSelected: Boolean){
        layoutBottomArrow.isSelected = isSelected
        if(layoutBottomArrow.isSelected){
            layoutMotion.transitionToEnd()
        }else{
            layoutMotion.transitionToStart()
        }
    }


    /**
     * 해시태그 레이아웃 셋팅
     */
    private fun setHashTagData(position: Int){
//        onClickBottomArrow(false)
        layoutCustomHashTag.setTextStyle(R.style.F13R_purple_7e84a7)
        layoutCustomHashTag.setTextBackground(R.drawable.rectangle_round_white_abafcb)
        layoutCustomHashTag.setHashTagList(adapterNegoDetail.getOrderList()[position].HASH?: arrayListOf())
        layoutCustomHashTag.notifyDataSetChanged()
    }

    /**
     * 상단 타이틀 셋팅
     */
    fun setTopTitle(data: Order.OrderItem){
        tvStockName.text = data.STK_NM
        tvStockGubun.text = data.STOCK_TP_CODE_NM.toStockTypeCodeName(data.SEC_KIND_DTL_TP_CODE)
    }

    /**
     * 즐겨찾기 셋팅
     */
    override fun setFavoriteData(isFavorite: Boolean){
        ivStar.isSelected = isFavorite
    }

    /**
     * 주문현황 리스트 셋
     */
    override fun setTradeList(order: Order) {

        order.datas?.let {
            adapterNegoDetail.addAll(it.resultList ?: arrayListOf())
            loadingClose()

            //Favorite 및 현재 Order번호로 페이저 이동
            if(!orderNo.isNullOrEmpty()){
                it.resultList?.let {resultList->
                    for((index, order) in resultList.withIndex()){
                        ivStar.isSelected = !resultList[index].FAV_CORP_NO.isNullOrEmpty()

                        if(order.ORDER_NO == orderNo){
                            vpNego.currentItem = index
                            break
                        }
                    }
                }
            }else{
                if(adapterNegoDetail.getOrderList().size >0){
                    setHashTagData(0)
                    setFavoriteData(!adapterNegoDetail.getOrderList()[0].FAV_CORP_NO.isNullOrEmpty())
                    setTopTitle(adapterNegoDetail.getOrderList()[0])
                }
            }


            if((it.resultList?.size ?: 0) > 0){
                tabLayout.getTabAt(0)?.select()
            }else{
                ViewUtils.alertCustomDialog(this, R.string.orderdetail_no_data_query.toResString(), R.string.cancel.toResString(), R.string.confirm.toResString(),{
                    ivSearch.callOnClick()
                },{
                    ivSearch.callOnClick()
                })
            }
        }

    }


    private fun loadingClose(){
        adapterNegoDetail.notifyDataSetChanged()
    }


    /**
     * Loading Progress
     */
    override fun showProgress(isShow: Boolean){
        if(isShow){
            progress_bar_login.toVisible()
        }else{
            progress_bar_login.toInvisible()
        }
    }

    /**
     * Alert 다이얼로그
     */
    override fun alertDialog(msg: String){
        ViewUtils.alertDialog(this, msg){

        }

    }

    /**
     * Error Alert
     */
    override fun showErrorMsg(code: String?, msg: String?) {
        ViewUtils.showErrorMsg(this, code, msg)
    }

    override fun refresh(){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            BuyWriteActivity.STOCK_SEARCH->{
                if(resultCode != Activity.RESULT_OK) return

                data?.let {
                    val stock = it.getSerializableExtra(Keys.INTENT_STOCK)

                    if(stock is Stock.ResultMap){
                        entpNo = stock.ENTP_NO ?: ""
                        stkCode = stock.STK_CODE ?: ""
                        orderNo = ""

//                        tvStockName.text = stock.STK_NM
//                        tvStockGubun.text = stock.SEC_KIND_TP_CODE.toStockTypeCodeName(stock.SEC_KIND_DTL_TP_CODE)

                        presenter.getTradeList(stkCode)
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        presenter.detachView()
        toolbar.dispose()

        super.onDestroy()
    }


    override fun onBackPressed() {
        if(drawer_layout!!.isDrawerOpen(GravityCompat.END)){
            drawer_layout?.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }

}
