package kr.co.koscom.omp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_list.*
import kotlinx.android.synthetic.main.activity_order_list.btnSearch
import kotlinx.android.synthetic.main.activity_order_list.count
import kotlinx.android.synthetic.main.activity_order_list.nothing
import kotlinx.android.synthetic.main.activity_order_list.progress_bar_login
import kotlinx.android.synthetic.main.activity_order_list.search
import kotlinx.android.synthetic.main.activity_order_list.tabLayout
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.PaginationListener
import kr.co.koscom.omp.view.ViewUtils
import java.text.DecimalFormat
import java.util.*

/**
 * 주문현황 계약 및 체결, 전종목 거래현황
 */

class OrderListActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private var gubn = 1

    private var stock: Stock.ResultMap? = null

    private val PAGE_SIZE = 30
    private var loading = false
    private var lastPage = false
    private var isCreate = true
    private var isCreateTab = true

    val listData = ArrayList<Order.OrderItem>()

    private val contractList = ArrayList<OrderContract.OrderContractItem>()

    private val numberFormat = DecimalFormat("#,###")

    private var fragment: WebFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        REQUIRE_REFRESH = true

        orderListActivity = this

        setContentView(R.layout.activity_order_list)

        toolbar.initTitle("비상장주식거래")
        toolbar.initData(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        gubn = intent.getIntExtra("gubn", 1)

        viewModelFactory = Injection.provideViewModelFactory(this)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        /*searchWord.setOnClickListener {

            startActivityForResult(Intent(this, Search2Activity::class.java),
                BuyWriteActivity.STOCK_SEARCH
            )
        }*/

        search.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                lastPage = false

                listData.clear()
                contractList.clear()
            }

        })
        search.setOnClickListener {
            ActivityUtil.startSearchActivityResult(this, BuyWriteActivity.STOCK_SEARCH)
//            startActivityForResult(Intent(this@OrderListActivity, SearchActivity::class.java), BuyWriteActivity.STOCK_SEARCH)
        }

        btnSearch.setOnClickListener {
            if(tabLayout.selectedTabPosition == 0){

                filterAllUnderBar.visibility = View.VISIBLE
                filterSellUnderBar.visibility = View.INVISIBLE
                filterBuyUnderBar.visibility = View.INVISIBLE

                if(!isCreate) search.setText("")
                isCreate = false

                listData.clear()
                search(DealType.ALL.type)
            }
            else if(tabLayout.selectedTabPosition == 1){
                contractList.clear()
                searchContract(DealType.ALL.type)
            }
        }

        filterAll.setOnClickListener {
            onTapSubDealType(DealType.ALL)

            if(tabLayout.selectedTabPosition == 0){
                listData.clear()
                search(DealType.ALL.type)
            }
            else if(tabLayout.selectedTabPosition == 1){
                contractList.clear()
                searchContract(DealType.ALL.type)
            }
        }

        filterSell.setOnClickListener {
            onTapSubDealType(DealType.SELL)

            if(tabLayout.selectedTabPosition == 0){
                listData.clear()
                search(DealType.SELL.type)
            }
            else if(tabLayout.selectedTabPosition == 1){
                contractList.clear()
                searchContract(DealType.SELL.type)
            }
        }

        filterBuy.setOnClickListener {
            onTapSubDealType(DealType.BUYING)

            if(tabLayout.selectedTabPosition == 0){
                listData.clear()
                search(DealType.BUYING.type)
            }
            else if(tabLayout.selectedTabPosition == 1){
                contractList.clear()
                searchContract(DealType.BUYING.type)
            }
        }

        btnOrder.setOnClickListener {
            ActivityUtil.startOrderWriteActivity(this)
//            startActivity(Intent(this@OrderListActivity, OrderWriteActivity::class.java))
        }

//        for (i in 0 until tabLayout.tabCount) {
//
//            val tab = tabLayout.getTabAt(i)
//            if (tab != null) {
//
//                val tabTextView = TextView(this)
//                tab.customView = tabTextView
//
//                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//
//                tabTextView.text = tab.text
//                tabTextView.setTextColor(Color.parseColor("#ffffff"))
//                tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
//
//                // First tab is the selected tab, so if i==0 then set BOLD typeface
//                if (i == 0) {
//                    tabTextView.setTypeface(ResourcesCompat.getFont(this, R.font.spoqa_han_sans), Typeface.BOLD)
//                    tabTextView.setTextColor(Color.parseColor("#ffffff"))
//                }else{
//                    tabTextView.setTypeface(ResourcesCompat.getFont(this, R.font.spoqa_han_sans), Typeface.NORMAL)
//                    tabTextView.setTextColor(Color.parseColor("#ffffff"))
//                }
//
//            }
//
//        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.i("tabLayout", "onTabSelected:" + isCreateTab)
                if(!isCreateTab) {
                    val text = tab?.customView as TextView?

                    text?.setTypeface(ResourcesCompat.getFont(this@OrderListActivity, R.font.spoqa_han_sans), Typeface.BOLD)
                    text?.setTextColor(Color.parseColor("#ffffff"))

                    lastPage = false
                    search.setText("")
                    listData.clear()
                    contractList.clear()
                    listOrder.adapter?.notifyDataSetChanged()

                    if(tab!!.position == 0){
                        subFragment.visibility = View.INVISIBLE
                        onTapSubDealType(DealType.ALL)

                        search(DealType.ALL.type)

                    }
                    else if(tab.position == 1){
                        subFragment.visibility = View.INVISIBLE
                        onTapSubDealType(DealType.ALL)

                        searchContract(DealType.ALL.type)
                    }
                    else if(tab.position == 2){

                        if(fragment == null){
                            val transaction = supportFragmentManager.beginTransaction()
                            fragment = WebFragment()
                            var bundle = Bundle()
                            bundle.putString("url", BuildConfig.SERVER_URL + "/mobile/invst/nltdAllStockTrdSt")
                            fragment!!.arguments = bundle
                            transaction.replace(R.id.subFragment, fragment!!)
                            transaction.commitAllowingStateLoss()
                        }

                        subFragment.visibility = View.VISIBLE
                    }
                }
                isCreateTab = false
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                val text = tab?.customView as TextView?
//
//                text?.setTypeface(ResourcesCompat.getFont(this@OrderListActivity, R.font.spoqa_han_sans), Typeface.NORMAL)
//                text?.setTextColor(Color.parseColor("#ffffff"))
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.i("tabLayout", "onTabSelected")
//                val text = tab?.customView as TextView?
//
//                text?.setTypeface(ResourcesCompat.getFont(this@OrderListActivity, R.font.spoqa_han_sans), Typeface.BOLD)
//                text?.setTextColor(Color.parseColor("#ffffff"))

                lastPage = false

                listData.clear()
                contractList.clear()
                listOrder.adapter?.notifyDataSetChanged()

                if(tab!!.position == 0){
                    subFragment.visibility = View.INVISIBLE
                    onTapSubDealType(DealType.ALL)

                    search(DealType.ALL.type)

                }
                else if(tab.position == 1){
                    subFragment.visibility = View.INVISIBLE
                    onTapSubDealType(DealType.ALL)

                    searchContract(DealType.ALL.type)
                }
                else if(tab.position == 2){

                    if(fragment == null){
                        val transaction = supportFragmentManager.beginTransaction()
                        fragment = WebFragment()
                        var bundle = Bundle()
                        bundle.putString("url", BuildConfig.SERVER_URL + "/mobile/invst/nltdAllStockTrdSt")
                        fragment!!.arguments = bundle
                        transaction.replace(R.id.subFragment, fragment!!)
                        transaction.commitAllowingStateLoss()
                    }

                    subFragment.visibility = View.VISIBLE
                }
            }

        })

        listOrder!!.layoutManager = LinearLayoutManager(this)
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        horizontalDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider2)!!)

        listOrder!!.addItemDecoration(horizontalDivider)

        //listOrder!!.adapter = OrderAdapter(listData)
        listOrder!!.addOnScrollListener(object : PaginationListener(listOrder!!.layoutManager!! as LinearLayoutManager, PAGE_SIZE) {
            override fun loadMoreItems() {
                loading = true
                if(tabLayout.selectedTabPosition == 0){
                    onTapSubDealType(DealType.ALL)
                    search(DealType.ALL.type)
                }
                else if(tabLayout.selectedTabPosition == 1){
                    onTapSubDealType(DealType.ALL)
                    searchContract(DealType.ALL.type)
                }
            }

            override fun isLastPage(): Boolean {
                return lastPage;
            }

            override fun isLoading(): Boolean {
                return loading;
            }

            override fun viewItem(itemIndex: Int) {
                Log.d(AlarmFragment::class.simpleName, "viewItem($itemIndex)")
            }
        })
    }

    /**
     * 전체, 매도, 매수 클릭시
     */
    private fun onTapSubDealType(dealType: DealType){
        when(dealType){
            DealType.SELL ->{
                //매도
                filterAllUnderBar.visibility = View.INVISIBLE
                filterSellUnderBar.visibility = View.VISIBLE
                filterBuyUnderBar.visibility = View.INVISIBLE
            }
            DealType.BUYING ->{
                //매수
                filterAllUnderBar.visibility = View.INVISIBLE
                filterSellUnderBar.visibility = View.INVISIBLE
                filterBuyUnderBar.visibility = View.VISIBLE
            }
            DealType.ALL ->{
                //전체
                filterAllUnderBar.visibility = View.VISIBLE
                filterSellUnderBar.visibility = View.INVISIBLE
                filterBuyUnderBar.visibility = View.INVISIBLE
            }
        }

    }

    override fun onResume() {
        super.onResume()
        
        if(REQUIRE_REFRESH){
            REQUIRE_REFRESH = false

            lastPage = false

            listData.clear()
            contractList.clear()
            listOrder.adapter?.notifyDataSetChanged()

            if(gubn == 1){
                tabLayout.getTabAt(0)?.select()
                onTapSubDealType(DealType.ALL)
                search(DealType.ALL.type)
            }
            else if(gubn == 2){
                tabLayout.getTabAt(1)?.select()
                onTapSubDealType(DealType.ALL)
                searchContract(DealType.ALL.type)
            }
            else if(gubn == 3){
                tabLayout.getTabAt(2)?.select()

                if(fragment == null){
                    val transaction = supportFragmentManager.beginTransaction()
                    fragment = WebFragment()
                    var bundle = Bundle()
                    bundle.putString("url", BuildConfig.SERVER_URL + "/mobile/invst/nltdAllStockTrdSt")
                    fragment!!.arguments = bundle
                    transaction.replace(R.id.subFragment, fragment!!)
                    transaction.commitAllowingStateLoss()
                }

                subFragment.visibility = View.VISIBLE
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    private fun search(dealTpCode : String?){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(orderViewModel.tradeList(listData.size, PAGE_SIZE, search.text.toString(),dealTpCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    listData.addAll(it.datas?.resultList!!)
                    if(listOrder!!.adapter !is OrderAdapter){
                        listOrder!!.adapter = OrderAdapter(listData)
                    }
                    listOrder!!.adapter?.notifyDataSetChanged()

                    count.text = numberFormat.format(it.datas?.resultCount)

                    if(it.datas?.resultList!!.size < PAGE_SIZE){
                        lastPage = true
                    }

                    loading = false

                    if(listData.size == 0){
                        nothing.visibility = View.VISIBLE
                    }
                    else{
                        nothing.visibility = View.GONE
                    }

                }else{

                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }
    private fun searchContract(dealTpCode : String?){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(orderViewModel.contractList(contractList.size, PAGE_SIZE, search.text.toString(), dealTpCode = dealTpCode?: "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    contractList.addAll(it.datas!!.resultList!!)
                    if(listOrder!!.adapter !is ContractAdapter){
                        listOrder!!.adapter = ContractAdapter(contractList)
                    }
                    listOrder!!.adapter?.notifyDataSetChanged()

                    count.text = numberFormat.format(it.datas?.resultCount)

                    if(it.datas!!.resultList!!.size < PAGE_SIZE){
                        lastPage = true
                    }

                    loading = false

                    if(contractList.size == 0){
                        nothing.visibility = View.VISIBLE
                    }
                    else{
                        nothing.visibility = View.GONE
                    }

                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    internal inner class OrderAdapter(val list: ArrayList<Order.OrderItem>) :
        RecyclerView.Adapter<OrderAdapter.ViewHolder>() {


        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var stockName = itemView.findViewById<TextView>(R.id.stockName)
            var stockName2 = itemView.findViewById<TextView>(R.id.stockName2)
            var count = itemView.findViewById<TextView>(R.id.count)
            var price = itemView.findViewById<TextView>(R.id.price)
            var status1 = itemView.findViewById<LinearLayoutCompat>(R.id.status1)
            var status2 = itemView.findViewById<LinearLayoutCompat>(R.id.status2)
            var status3 = itemView.findViewById<LinearLayoutCompat>(R.id.status3)
            var status4 = itemView.findViewById<LinearLayoutCompat>(R.id.status4)
            var contract_status1 = itemView.findViewById<LinearLayoutCompat>(R.id.contract_status1)
            var contract_status2 = itemView.findViewById<LinearLayoutCompat>(R.id.contract_status2)
            var btnNego = itemView.findViewById<RelativeLayout>(R.id.btnNego)
            var ivSecret = itemView.findViewById<ImageView>(R.id.ivSecret)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_order_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
            val data = list[position]

            holder.stockName.text = data.CORP_HANGL_NM
            holder.stockName2.text = data.STOCK_TP_CODE_NM

            //holder.status.text = if(data.POST_ORD_STAT_CODE == "0"){"협상대기"}else if(data.POST_ORD_STAT_CODE == "1"){"협상중"}else if(data.POST_ORD_STAT_CODE == "2"){"협상중(협상불가)"}else if(data.POST_ORD_STAT_CODE == "4"){"삭제"}else{""}

            holder.status1.visibility = View.INVISIBLE
            holder.status2.visibility = View.INVISIBLE
            holder.status3.visibility = View.INVISIBLE
            holder.status4.visibility = View.INVISIBLE
            holder.contract_status1.visibility = View.INVISIBLE
            holder.contract_status2.visibility = View.INVISIBLE

            if(data.PUBLIC_YN == "Y"){
                holder.ivSecret.toGone()
                holder.count.text = numberFormat.format(data.DEAL_QTY)
                holder.price.text = numberFormat.format(data.DEAL_UPRC)
            }else{
                holder.ivSecret.toVisible()
                holder.count.text = R.string.star6.toResString()
                holder.price.text = R.string.star6.toResString()
            }

            if(data.POST_ORD_STAT_CODE == "0"){
                if(data.DEAL_TP == "10"){
                    holder.status1.visibility = View.VISIBLE
                }
                else{
                    holder.status2.visibility = View.VISIBLE
                }
            }
            else if(data.POST_ORD_STAT_CODE == "1"){
                if(data.RMQTY ?: 0 > 0){
                    holder.status4.visibility = View.VISIBLE
                }
                else{
                    holder.status3.visibility = View.VISIBLE
                }
            }

            holder.mRow.setOnClickListener {


                ActivityUtil.startOrderDetailActivity(this@OrderListActivity, data)

//                var intent = Intent(this@OrderListActivity, OrderDetailActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("entpNo", data.ENTP_NO)
//                intent.putExtra("stkCode", data.STK_CODE)
//                intent.putExtra("stkName", data.STK_NM)
//                intent.putExtra("orderNo", data.ORDER_NO)
//                startActivity(intent)

            }

            holder.btnNego.setOnClickListener {
                ActivityUtil.startOrderPopupActivity(this@OrderListActivity, data)

//                var intent = Intent(this@OrderListActivity, OrderPopupActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("orderItem", data)
//                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    internal inner class ContractAdapter(val list: ArrayList<OrderContract.OrderContractItem>) :
        RecyclerView.Adapter<ContractAdapter.ViewHolder>() {


        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var stockName = itemView.findViewById<TextView>(R.id.stockName)
            var stockName2 = itemView.findViewById<TextView>(R.id.stockName2)
            var count = itemView.findViewById<TextView>(R.id.count)
            var price = itemView.findViewById<TextView>(R.id.price)
            var status1 = itemView.findViewById<LinearLayoutCompat>(R.id.status1)
            var status2 = itemView.findViewById<LinearLayoutCompat>(R.id.status2)
            var status3 = itemView.findViewById<LinearLayoutCompat>(R.id.status3)
            var status4 = itemView.findViewById<LinearLayoutCompat>(R.id.status4)
            var contract_status1 = itemView.findViewById<LinearLayoutCompat>(R.id.contract_status1)
            var contract_status2 = itemView.findViewById<LinearLayoutCompat>(R.id.contract_status2)
            var btnNego = itemView.findViewById<RelativeLayout>(R.id.btnNego)
            var ivSecret = itemView.findViewById<ImageView>(R.id.ivSecret)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_order_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ContractAdapter.ViewHolder, position: Int) {
            val data = list[position]

            holder.stockName.text = data.CORP_HANGL_NM
            holder.stockName2.text = data.STOCK_TP_CODE_NM

            holder.status1.visibility = View.INVISIBLE
            holder.status2.visibility = View.INVISIBLE
            holder.status3.visibility = View.INVISIBLE
            holder.status4.visibility = View.INVISIBLE
            holder.contract_status1.visibility = View.INVISIBLE
            holder.contract_status2.visibility = View.INVISIBLE

            if(data.PUBLIC_YN == "Y"){
                holder.ivSecret.toGone()
                holder.count.text = numberFormat.format(data.DEAL_QTY ?: 0)
                holder.price.text = numberFormat.format(data.DEAL_UPRC ?: 0)
            }else{
                holder.ivSecret.toVisible()
                holder.count.text = R.string.star6.toResString()
                holder.price.text = R.string.star6.toResString()
            }

            if(data.NEGO_SETT_STAT_CODE == "204" || data.NEGO_SETT_STAT_CODE == "206"){
                holder.contract_status1.visibility = View.VISIBLE
            }
            else{
                holder.contract_status2.visibility = View.VISIBLE
            }

            holder.mRow.setOnClickListener {
                ActivityUtil.startContractPopupActivity(this@OrderListActivity, data)

//                var intent = Intent(this@OrderListActivity, ContractPopupActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("contractItem", data)
//                startActivity(intent)

            }
            holder.btnNego.setOnClickListener {
                ActivityUtil.startContractPopupActivity(this@OrderListActivity, data)
//                var intent = Intent(this@OrderListActivity, ContractPopupActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("contractItem", data)
//                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == BuyWriteActivity.STOCK_SEARCH){
                if(data != null){
                    stock = data.getSerializableExtra("stock") as Stock.ResultMap
                    search.text = stock?.STK_NM
                    isCreate = true
                    btnSearch.callOnClick()
                }
            }
        }
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

    override fun onStop() {
        super.onStop()

        disposable.clear()
        toolbar.dispose()
    }

    companion object {

        var REQUIRE_REFRESH = true

        var orderListActivity: OrderListActivity? = null

    }
}
