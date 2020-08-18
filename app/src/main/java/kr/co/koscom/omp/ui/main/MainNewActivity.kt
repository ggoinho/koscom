package kr.co.koscom.omp.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main_new.*
import kr.co.koscom.omp.DeepLinker
import kr.co.koscom.omp.NavigationFragment
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.MainListAdapter
import kr.co.koscom.omp.adapter.holder.MainHeaderHolder
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.enums.MainOrderTab
import kr.co.koscom.omp.enums.MyPageTabType
import kr.co.koscom.omp.enums.NegoTiableMyTab
import kr.co.koscom.omp.enums.OrderListTab
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.ui.main.contract.MainNewContract
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.util.LogUtil
import kr.co.koscom.omp.view.ViewUtils

class MainNewActivity : AppCompatActivity(), MainNewContract.View {
    internal val tag = this.javaClass.simpleName

    private val deepLinker = DeepLinker
    private lateinit var viewModelFactory: ViewModelFactory

    private val adapterMain: MainListAdapter by lazy{
        MainListAdapter(supportFragmentManager, lifecycle)
    }

    private val presenter: MainNewPresenter by lazy {
        MainNewPresenter(this)
    }

    private var currentTab: MainOrderTab = MainOrderTab.ORDER_STATUS
    private var isLastPage: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)

        mainActivity = this

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

        processDeepLink()

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter(
            Constants.pushMessageReceived)
        )
    }

    private fun init(){

        rvMain.apply {
            layoutManager = LinearLayoutManager(this@MainNewActivity)
            itemAnimator = DefaultItemAnimator()

            adapter = adapterMain
        }

        rvMain.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0

                if(firstVisibleItem < 1){
                    if(layoutSearchFilter.isVisible()){
                        layoutSearchFilter.toGone()
                    }
                }else{
                    if(layoutSearchFilter.isGone()){
                        layoutSearchFilter.toVisible()
                    }
                }

                if(!isLastPage && (totalItemCount-visibleItemCount) <= (firstVisibleItem + 1) && currentTab == MainOrderTab.ORDER_STATUS ){

                    getCurrentTabList(currentTab, false)
                }
            }

        })

    }

    private fun initListener(){

        adapterMain.setOnHeaderFilterClickListener(object : MainListAdapter.OnHeaderFilterClickListener{
            override fun onSelectTabEvent(type: MainOrderTab, isSynchronized: Boolean) {
                onClickOrderTab(type)
            }

            override fun checkNegoTiableMyEvent(type: NegoTiableMyTab, isSynchronized: Boolean) {
                checkNegoTiableMy(type)
            }

            override fun onClickDetailView(item: Any, position: Int) {
                moveDetailViewClick()

            }
        })

        adapterMain.setOnNegoPagerClickListener(object: MainHeaderHolder.OnNegoPagerClickListener{
            override fun onRejectClick(item: Any, position: Int) {
                if(item is Main.Nego){
                    presenter.denyNego(item)
                }
            }

            override fun onAcceptClick(item: Any, position: Int) {
                if(item is Main.Nego){
                    presenter.denyNego(item)
                }
            }
        })

        adapterMain.setOnContractPagerClickListener(object : MainHeaderHolder.OnContractPagerClickListener{
            override fun onContractClick(item: Any, position: Int) {

            }

            override fun onConfirmClick(item: Any, position: Int) {
                if(item is Main.NegoContract){
                    ActivityUtil.startContractDetailActivity(this@MainNewActivity, item.STOCK_TP_CODE_NM,  item.CHANNEL_URL,  item.ORDER_NO)
                }
            }
        })

        adapterMain.setOnMainListClickListener(object : MainListAdapter.OnMainListClickListener{
            override fun onRquestCancel(item: Any) {
                if(item is OrderNego.OrderNegoItem){
                    ViewUtils.alertCustomDialog(this@MainNewActivity, R.string.main_yocheong_cancel.toResString(), R.string.no.toResString(), R.string.yes.toResString(),{

                    },{
                        presenter.yocheongCancel(item)
                    })
                }
            }
        })


        ivLogo.setOnClickListener {
            refresh()
        }

        layoutMenu.setOnClickListener {
            ActivityUtil.startDrawerLayoutActivityResult(this, Constants.RESULT_CODE_DRAWER_LAYOUT)
//            drawer_layout.openDrawer(GravityCompat.END)
        }

        layoutAlarm.setOnClickListener {
            ActivityUtil.startMyPageActivity(this, MyPageTabType.ALARM.ordinal)
        }

        tvTabOrderStatus.setOnClickListener {
            onClickOrderTab(MainOrderTab.ORDER_STATUS, true)

        }

        tvTabMyNegoProgress.setOnClickListener {
            onClickOrderTab(MainOrderTab.NEGO_PROGRESS, true)
        }

        tvConclusionStatus.setOnClickListener {
            onClickOrderTab(MainOrderTab.CONCLUSION_STATUS, true)
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

        tvOrderDetailView.setOnClickListener {
            moveDetailViewClick()
        }

    }

    private fun onClickOrderTab(type: MainOrderTab, isSynchronized: Boolean = false){

        currentTab = type
        when(type){
            MainOrderTab.ORDER_STATUS ->{
                //주문현황
                tvTabOrderStatus.isSelected = true
                tvTabOrderStatus.toBold(this)
                vTabDivide1.isSelected = true
                tvTabMyNegoProgress.isSelected = false
                tvTabMyNegoProgress.toNormal(this)
                vTabDivide2.isSelected = false
                tvConclusionStatus.isSelected = false
                tvConclusionStatus.toNormal(this)

                tvNegotiable.toVisible()
                tvMy.toVisible()
                tvOrderTotalCount.toVisible()
            }
            MainOrderTab.NEGO_PROGRESS ->{
                //나의협상 진행상황
                tvTabOrderStatus.isSelected = false
                tvTabOrderStatus.toNormal(this)
                vTabDivide1.isSelected = true
                tvTabMyNegoProgress.isSelected = true
                tvTabMyNegoProgress.toBold(this)
                vTabDivide2.isSelected = true
                tvConclusionStatus.isSelected = false
                tvConclusionStatus.toNormal(this)

                tvNegotiable.toGone()
                tvMy.toGone()
                tvOrderTotalCount.toGone()
            }
            MainOrderTab.CONCLUSION_STATUS ->{
                //체결현황
                tvTabOrderStatus.isSelected = false
                tvTabOrderStatus.toNormal(this)
                vTabDivide1.isSelected = false
                tvTabMyNegoProgress.isSelected = false
                tvTabMyNegoProgress.toNormal(this)
                vTabDivide2.isSelected = true
                tvConclusionStatus.isSelected = true
                tvConclusionStatus.toBold(this)

                tvNegotiable.toGone()
                tvMy.toGone()
                tvOrderTotalCount.toGone()
            }
        }

        adapterMain.setFilterTab(currentTab)
        getCurrentTabList(type, true)

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

        adapterMain.setFilterCheckNegoMy(type)
//        adapterMain.notifyDataSetChanged()

        getCurrentTabList(currentTab, true)
    }

    /**
     * 현재 탭에 따른 리스트 호출
     */
    fun getCurrentTabList(tab: MainOrderTab?, isInit: Boolean = false){
        when(tab){
            MainOrderTab.ORDER_STATUS ->{
                //주문현황
                if(isInit){
                    adapterMain.clearList()
//                    adapterMain.notifyDataSetChanged()
                    presenter.getTradeList(arrayListOf(), tvNegotiable.isSelected, tvMy.isSelected)
                }else{
                    presenter.getTradeList(adapterMain.getTradeList(), tvNegotiable.isSelected, tvMy.isSelected)
                }
            }

            MainOrderTab.NEGO_PROGRESS ->{
                //나의협상 진행상황
                adapterMain.clearList()
//                adapterMain.notifyDataSetChanged()
                presenter.getNegoList(arrayListOf())
            }
            MainOrderTab.CONCLUSION_STATUS ->{
                //체결현황
                adapterMain.clearList()
//                adapterMain.notifyDataSetChanged()
                presenter.getContractList(arrayListOf())
            }
        }

    }

    /**
     * Main Data Set
     */
    override fun setMainData(mainTotal: MainTotalData) {


        val typeNegotiableMy = when{
            tvNegotiable.isSelected-> NegoTiableMyTab.NEGOTIABLE
            tvMy.isSelected-> NegoTiableMyTab.MY
            else-> NegoTiableMyTab.NONE
        }

        adapterMain.setFilterTab(currentTab)
        adapterMain.setFilterCheckNegoMy(typeNegotiableMy)
        adapterMain.setMainTotalData(mainTotal)
        adapterMain.setMainTopHeader(mainTotal)

        onClickOrderTab(currentTab, true)
    }

    /**
     * 주문현황 리스트 셋
     */
    override fun setTradeList(order: Order) {

        order.datas?.let {
            adapterMain.addTradeList(it.resultList ?: arrayListOf())
            adapterMain.setTotalCount(it.resultCount ?: 0)
            tvOrderTotalCount.toTextfromHtml(R.string.main_order_total_count.toResString(), it.resultCount?:0)

            isLastPage = it.resultList?.size ?:0 < presenter.PAGE_SIZE
        }
        loadingClose()
    }

    /**
     * 나의협상 진행상황 리스트 셋
     */
    override fun setNegoList(order: OrderNego) {

        order.datas?.let {
            adapterMain.addNegoList(it.resultList ?: arrayListOf())
            adapterMain.setTotalCount(0)

//            isLastPage = it.resultList?.size ?:0 < presenter.PAGE_SIZE
        }
        loadingClose()
    }

    /**
     * 체결현황 리스트 셋
     */
    override fun setContractList(order: OrderContract) {

        order.datas?.let {
            adapterMain.addContractList(it.resultList ?: arrayListOf())
            adapterMain.setTotalCount(0)

//            isLastPage = it.resultList?.size ?:0 < presenter.PAGE_SIZE
        }
        loadingClose()
    }

    /**
     * 상세보기 클릭
     */
    private fun moveDetailViewClick(){
        when(currentTab){
            MainOrderTab.ORDER_STATUS -> {
                //주문현황
                ActivityUtil.startOrderListActivity(this, OrderListTab.ORDER_STATUS.ordinal)
            }
            MainOrderTab.NEGO_PROGRESS -> {
                //나의협상 진행상황
                ActivityUtil.startMyPageActivity(this, MyPageTabType.MY_NEGO.ordinal)
            }
            MainOrderTab.CONCLUSION_STATUS -> {
                //체결현황
                ActivityUtil.startOrderListActivity(this, OrderListTab.CONCLUSION_STATUS.ordinal)
            }
        }
    }


    private fun loadingClose(){
        adapterMain.notifyDataSetChanged()

        Handler().postDelayed({
            adapterMain.setIsRefresh(false)
        },50)

    }

    /**
     * Alarm 카운트 셋팅
     */
    override fun setAlarmCount(alarm: Alarm) {
        alarm.datas?.let {data->
            if(0 < data.result?: 0){
                tvAlarm.toVisible()
                tvAlarm.text = data.result.toString()
            }else{
                tvAlarm.toInvisible()
                tvAlarm.text = ""
            }
        }?: run{
            tvAlarm.toInvisible()
        }
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
        adapterMain.clearList()
        adapterMain.setIsRefresh(true)

        presenter.searchAlarm()
        presenter.getMainData()
    }


    private fun processDeepLink(){

        val screen = deepLinker.subscribeScreen()
        val url = deepLinker.subscribeUrl()
        val tab = deepLinker.subscribeTab()
        val message = deepLinker.subscribeMessage()

        if(!screen.isNullOrEmpty()){
            ActivityUtil.startClassForName(this, screen, tab, message)
        }else{
            ActivityUtil.startInvestmentActivity(this, true)
        }
    }


    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            LogUtil.d(tag, Constants.pushMessageReceived)
            presenter.searchAlarm()
        }
    }

    override fun onResume() {
        super.onResume()

        refresh()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)

        mainActivity = null

        presenter.logOut()

        presenter.detachView()

        super.onDestroy()
    }


    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.END)){
            drawer_layout.closeDrawer(GravityCompat.END)
        }
        else{
            ViewUtils.alertLogoutDialog(this, R.string.main_logout_contents.toResString()){
                ActivityUtil.startCleanLoginActivity(this)
            }
        }
    }

    companion object {
        var mainActivity: MainNewActivity? = null

    }
}
