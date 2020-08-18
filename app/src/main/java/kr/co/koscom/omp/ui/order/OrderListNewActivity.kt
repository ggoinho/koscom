package kr.co.koscom.omp.ui.order

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_order_list.*
import kr.co.koscom.omp.BuyWriteActivity
import kr.co.koscom.omp.DeepLinker
import kr.co.koscom.omp.NavigationFragment
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.OrderListAdapter
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.custom.CustomSearchView
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.enums.NegoTiableMyTab
import kr.co.koscom.omp.enums.OrderListTab
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.ui.order.contract.OrderListNewContract
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.ViewUtils

class OrderListNewActivity : AppCompatActivity(), OrderListNewContract.View {
    internal val tag = this.javaClass.simpleName

    private lateinit var viewModelFactory: ViewModelFactory

    private val adapterOrder: OrderListAdapter by lazy{
        OrderListAdapter()
    }

    private val presenter: OrderListNewPresenter by lazy {
        OrderListNewPresenter(this)
    }

    private var currentTab: OrderListTab = OrderListTab.ORDER_STATUS
    private var currentDealType: DealType = DealType.ALL
    private var isLastPage: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)

        init()
        initListener()

        presenter.initViewModel(viewModelFactory)

        currentTab = OrderListTab.getType(intent.getIntExtra(Keys.INTENT_GUBN, OrderListTab.ORDER_STATUS.ordinal))
        tabLayout.getTabAt(currentTab.ordinal)?.select()

    }

    private fun init(){


        toolbar.initTitle(R.string.orderlist_title.toResString())
        toolbar.initData(this)

        searchView.initListener()
        searchView.setInputAvailable(false)
        searchView.setHint(R.string.orderlist_search_input.toResString())
        searchView.setonEditTextListener(object: CustomSearchView.OnEditTextListener{
            override fun onImeActionNext(v: CustomSearchView) {

            }

            override fun onActionClear(v: CustomSearchView) {
                refresh()
                ActivityUtil.startSearchActivityResult(this@OrderListNewActivity, BuyWriteActivity.STOCK_SEARCH)
            }

            override fun onValueChanged(v: CustomSearchView) {

            }

            override fun onActionSearch(v: CustomSearchView) {
                refresh()
            }

            override fun onOptionClick(v: CustomSearchView, data: Any?) {
                ActivityUtil.startSearchActivityResult(this@OrderListNewActivity, BuyWriteActivity.STOCK_SEARCH)
            }
        })


        rvOrder.apply {
            layoutManager = LinearLayoutManager(this@OrderListNewActivity)
            itemAnimator = DefaultItemAnimator()

            adapter = adapterOrder
        }

        rvOrder.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0


                if(!isLastPage && (totalItemCount-visibleItemCount) <= (firstVisibleItem + 1)){
                    getCurrentTabList(currentDealType)
                }
            }
        })
    }

    private fun initListener(){

        adapterOrder.setOnOrderListClickListener(object : OrderListAdapter.OnOrderListClickListener{
            override fun onRquestCancel(item: Any) {
                if(item is OrderNego.OrderNegoItem){
                    ViewUtils.alertCustomDialog(this@OrderListNewActivity, R.string.main_yocheong_cancel.toResString(), R.string.no.toResString(), R.string.yes.toResString(),{

                    },{
                        presenter.yocheongCancel(item)
                    })
                }
            }
        })


        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                refresh()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text = tab?.customView as TextView?
                text?.toNormal(this@OrderListNewActivity)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

                val text = tab?.customView as TextView?
                text?.toBold(this@OrderListNewActivity)

                currentTab = if(tab?.position == OrderListTab.CONCLUSION_STATUS.ordinal){
                    tvNegotiable.toInvisible()
                    tvMy.toInvisible()

                    OrderListTab.CONCLUSION_STATUS

                } else {
                    tvNegotiable.toVisible()
                    tvMy.toVisible()

                    OrderListTab.ORDER_STATUS
                }
                adapterOrder.tabType = currentTab

                refresh()
            }
        })


        tvTabAll.setOnClickListener {
            onClickOrderTab(DealType.ALL, true)
        }

        tvTabBuy.setOnClickListener {
            onClickOrderTab(DealType.BUYING, true)
        }

        tvTabSell.setOnClickListener {
            onClickOrderTab(DealType.SELL, true)
        }

        tvNegotiable.setOnClickListener {
            if(tvNegotiable.isSelected){
                checkNegoTiableMy(NegoTiableMyTab.NONE, true)
            }else{
                checkNegoTiableMy(NegoTiableMyTab.NEGOTIABLE, true)
            }
        }

        tvMy.setOnClickListener {
            if(tvMy.isSelected){
                checkNegoTiableMy(NegoTiableMyTab.NONE, true)
            }else{
                checkNegoTiableMy(NegoTiableMyTab.MY, true)
            }
        }

        tvOrderRegist.setOnClickListener {
            ActivityUtil.startOrderWriteActivity(this)
        }

    }

    /**
     * 전체, 매도, 매수 탭 클릭
     */
    private fun onClickOrderTab(type: DealType, isSynchronized: Boolean = false){

        currentDealType = type
        when(type){
            DealType.ALL ->{
                //전체
                tvTabAll.isSelected = true
                tvTabAll.toBold(this)
                vTabDivide1.isSelected = true
                tvTabSell.isSelected = false
                tvTabSell.toNormal(this)
                vTabDivide2.isSelected = false
                tvTabBuy.isSelected = false
                tvTabBuy.toNormal(this)

            }
            DealType.SELL ->{
                //매도
                tvTabAll.isSelected = false
                tvTabAll.toNormal(this)
                vTabDivide1.isSelected = true
                tvTabSell.isSelected = true
                tvTabSell.toBold(this)
                vTabDivide2.isSelected = true
                tvTabBuy.isSelected = false
                tvTabBuy.toNormal(this)

            }
            DealType.BUYING ->{
                //매수
                tvTabAll.isSelected = false
                tvTabAll.toNormal(this)
                vTabDivide1.isSelected = false
                tvTabSell.isSelected = false
                tvTabSell.toNormal(this)
                vTabDivide2.isSelected = true
                tvTabBuy.isSelected = true
                tvTabBuy.toBold(this)

            }
        }

        initCurrentTabList(type)

    }

    /**
     * 협상가능, MY 체크
     */
    private fun checkNegoTiableMy(type: NegoTiableMyTab, isSynchronized: Boolean = false){


        when(type){
            NegoTiableMyTab.NONE->{
                tvNegotiable.isSelected = false
                tvMy.isSelected = false
            }
            NegoTiableMyTab.NEGOTIABLE->{
                tvNegotiable.isSelected = true
                tvMy.isSelected = false
            }
            NegoTiableMyTab.MY->{
                tvNegotiable.isSelected = false
                tvMy.isSelected = true
            }
            else->{
                tvNegotiable.isSelected = false
                tvMy.isSelected = false
            }
        }

        initCurrentTabList(currentDealType)
    }

    /**
     * 현재 탭에 따른 리스트 초기화
     */
    fun initCurrentTabList(tab: DealType){
        isLastPage = false
        adapterOrder.clearList()
        getCurrentTabList(tab)
    }

    /**
     * 현재 탭에 따른 리스트 호출
     */
    fun getCurrentTabList(tab: DealType){
        when(tab){
            DealType.ALL ->{
                //전체
                if(currentTab == OrderListTab.CONCLUSION_STATUS){
                    presenter.getContractList(adapterOrder.getContractList(), currentDealType.type, searchView.getSearchTextString())
                }else{
                    presenter.getTradeList(adapterOrder.getOrderList(), currentDealType.type, searchView.getSearchTextString(), tvNegotiable.isSelected, tvMy.isSelected)
                }

            }

            DealType.BUYING ->{
                //매수
                if(currentTab == OrderListTab.CONCLUSION_STATUS){
                    presenter.getContractList(adapterOrder.getContractList(), currentDealType.type, searchView.getSearchTextString())
                }else{
                    presenter.getTradeList(adapterOrder.getOrderList(), currentDealType.type, searchView.getSearchTextString(), tvNegotiable.isSelected, tvMy.isSelected)
                }

            }
            DealType.SELL ->{
                //매도
                if(currentTab == OrderListTab.CONCLUSION_STATUS){
                    presenter.getContractList(adapterOrder.getContractList(), currentDealType.type, searchView.getSearchTextString())
                }else{
                    presenter.getTradeList(adapterOrder.getOrderList(), currentDealType.type, searchView.getSearchTextString(), tvNegotiable.isSelected, tvMy.isSelected)
                }

            }
        }

    }


    private fun setTotalCount(totalCount: Int){
        tvOrderTotalCount.toTextfromHtml(R.string.main_order_total_count.toResString(), totalCount)
    }

    /**
     * 주문현황 리스트 셋
     */
    override fun setTradeList(order: Order) {

        order.datas?.let {
            adapterOrder.addOrderList(it.resultList ?: arrayListOf())
            setTotalCount(it.resultCount ?: 0)

            if(it.resultCount?: 0 <= 0){
                layoutEmpty.toVisible()
                rvOrder.toGone()
            }else{
                layoutEmpty.toGone()
                rvOrder.toVisible()
            }

            isLastPage = it.resultList?.size ?:0 < presenter.PAGE_SIZE
        }
        loadingClose()
    }

    /**
     * 체결현황 리스트 셋
     */
    override fun setContractList(order: OrderContract) {

        order.datas?.let {
            adapterOrder.addContractList(it.resultList ?: arrayListOf())
            setTotalCount(it.resultCount ?: 0)

            if(it.resultCount?: 0 <= 0){
                layoutEmpty.toVisible()
                rvOrder.toGone()
            }else{
                layoutEmpty.toGone()
                rvOrder.toVisible()
            }

            isLastPage = it.resultList?.size ?:0 < presenter.PAGE_SIZE
        }
        loadingClose()
    }



    private fun loadingClose(){
        adapterOrder.notifyDataSetChanged()
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
        searchView.setText("")
        currentDealType = DealType.ALL
        onClickOrderTab(currentDealType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            BuyWriteActivity.STOCK_SEARCH ->{
                if(resultCode != Activity.RESULT_OK) return

                data?.let {
                    val stock = it.getSerializableExtra(Keys.INTENT_STOCK)

                    if(stock is Stock.ResultMap){
                        searchView.setText(stock.STK_NM?: "")
                    }

                    initCurrentTabList(currentDealType)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        if(REQUIRE_REFRESH){
            refresh()
        }
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

    companion object {
        var REQUIRE_REFRESH = true
    }

}
