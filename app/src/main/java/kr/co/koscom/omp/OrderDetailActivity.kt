package kr.co.koscom.omp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.mikephil.charting.data.Entry
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.OrderViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_detail.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.view.MyLineChart
import kr.co.koscom.omp.view.ViewUtils
import java.text.DecimalFormat

/**
 * 주문상세
 */

class OrderDetailActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private var companyPagerAdapter: CompanyPagerAdapter? = null
    private var padding = 0

    private var webFragment: WebFragment? = null

    private val listData = ArrayList<Order.OrderItem>()

    private val numberFormat = DecimalFormat("#,###")

    private var entpNo: String? = null
    private var stkCode: String? = null
    private var stkName: String? = null
    private var orderNo: String? = null

    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var touchY2: Float = 0f

    private var isFavorite: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_detail)

        entpNo = intent.getStringExtra("entpNo")
        stkCode = intent.getStringExtra("stkCode")
        stkName = intent.getStringExtra("stkName")
        orderNo = intent.getStringExtra("orderNo")

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        padding = (PADDING_GAP_DIPS * resources.displayMetrics.density).toInt()

        toolbar.initTitle("비상장주식거래")
        toolbar.initData(this)

        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        btnSearch.setOnClickListener {
            var intent = Intent(this, Search2Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(intent, BuyWriteActivity.STOCK_SEARCH)
        }

        btnStar.setOnClickListener {
            favoriteCorp(entpNo!!)
        }

        btnPrev.setOnClickListener {
            var page = vpCompany.currentItem - 1
            if(page >= 0){
                vpCompany.setCurrentItem(page)
            }
            else{
                vpCompany.setCurrentItem(vpCompany.adapter!!.count-1)
            }
        }
        btnNext.setOnClickListener {
            var page = vpCompany.currentItem + 1
            if(page <= vpCompany.adapter!!.count-1){
                vpCompany.setCurrentItem(page)
            }
            else{
                vpCompany.setCurrentItem(0)
            }
        }

        border.setOnTouchListener { view, event ->
            Log.d("OrderDetailActivity", "onTouch")

            if(touchX == 0f){
                touchX = event.rawX
            }
            if(touchY == 0f){
                touchY = event.rawY
            }

            var eventaction = event.getAction()
            when (eventaction) {

                MotionEvent.ACTION_DOWN -> {
                    touchY2 = 0f
                }

                MotionEvent.ACTION_MOVE -> {
                    Log.d("OrderDetailActivity", "tabLayout onTouch move")

                    if(touchY2 != 0f){
                        Log.d("OrderDetailActivity", "(${event.rawY}, $touchY2)")

                        var layoutParams = content.layoutParams as ConstraintLayout.LayoutParams
                        layoutParams.height = layoutParams.height + (event.rawY - touchY2).toInt()
                        if(layoutParams.height > height87.height){
                            layoutParams.height = height87.height
                        }
                        if(layoutParams.height < height53.height){
                            layoutParams.height = height53.height
                        }
                        content.layoutParams = layoutParams

                        touchY2 = event.rawY
                    }
                    else{
                        touchY2 = event.rawY
                    }

                }

                MotionEvent.ACTION_UP -> {
                    Log.d("OrderDetailActivity", "onTouch up")

                    if(event.rawY != touchY){
                        if(event.rawY < touchY){
                            borderArrow.setImageResource(R.drawable.ico_list_down)
                        }
                        else if(event.rawY > touchY){
                            borderArrow.setImageResource(R.drawable.ico_list_up)
                        }
                    }

                    touchX = 0f
                    touchY = 0f
                }
            }

            return@setOnTouchListener true
        }

        for (index in (1 .. (tabLayout.getChildAt(0) as LinearLayout).childCount)){
            (tabLayout.getChildAt(0) as LinearLayout).getChildAt(index-1).setOnTouchListener { view, event ->
                Log.d("OrderDetailActivity", "tabLayout onTouch")

                if(touchX == 0f){
                    touchX = event.rawX
                }
                if(touchY == 0f){
                    touchY = event.rawY
                }

                var eventaction = event.getAction()
                when (eventaction) {

                    MotionEvent.ACTION_DOWN -> {
                        touchY2 = 0f
                    }

                    MotionEvent.ACTION_MOVE -> {
                        Log.d("OrderDetailActivity", "tabLayout onTouch move")

                        if(touchY2 != 0f){
                            Log.d("OrderDetailActivity", "(${event.rawY}, $touchY2)")

                            var layoutParams = content.layoutParams as ConstraintLayout.LayoutParams
                            layoutParams.height = layoutParams.height + (event.rawY - touchY2).toInt()
                            if(layoutParams.height > height87.height){
                                layoutParams.height = height87.height
                            }
                            if(layoutParams.height < height53.height){
                                layoutParams.height = height53.height
                            }
                            content.layoutParams = layoutParams

                            touchY2 = event.rawY
                        }
                        else{
                            touchY2 = event.rawY
                        }

                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d("OrderDetailActivity", "tabLayout onTouch up")

                        if(Math.abs(event.rawY - touchY) > 50){
                            if(event.rawY < touchY){
                                Log.d("OrderDetailActivity", "tabLayout touch above")
                                borderArrow.setImageResource(R.drawable.ico_list_down)
                            }
                            else if(event.rawY > touchY){
                                Log.d("OrderDetailActivity", "tabLayout touch below")
                                borderArrow.setImageResource(R.drawable.ico_list_up)
                            }
                        }
                        else{
                            Log.d("OrderDetailActivity", "tabLayout onclick")

                            tabLayout.getTabAt(index-1)?.select()

                            return@setOnTouchListener true
                        }

                        touchX = 0f
                        touchY = 0f
                        touchY2 = 0f
                    }
                }

                return@setOnTouchListener true
            }
        }


        content.post {
            companyPagerAdapter = CompanyPagerAdapter()
            vpCompany!!.adapter = companyPagerAdapter
            vpCompany!!.setPadding(padding, 0, padding, 0)
            vpCompany!!.clipToPadding = false
            vpCompany!!.pageMargin = padding/20
            vpCompany!!.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int,positionOffset: Float,positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {

                    loadWeb(tabLayout.selectedTabPosition)

                    stockName.text = listData.get(position).STK_NM
                }

            })
        }

        stockName.text = stkName

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            webFragment = WebFragment()
            transaction.replace(R.id.webFragment, webFragment!!)
            transaction.commitAllowingStateLoss()
        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d(OrderDetailActivity::class.simpleName, "onTabReselected(${tab!!.position})")

                loadWeb(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(OrderDetailActivity::class.simpleName, "onTabSelected(${tab!!.position})")
                loadWeb(tab!!.position)

                Log.d(OrderDetailActivity::class.simpleName, "tablayout scrollx : " + tab.position)
                tabScrollView.smoothScrollTo(((tabLayout.width/tabLayout.tabCount)/tabLayout.tabCount) * tab.position, 0)
            }

        })

        search()
    }

    private fun isWideScreen(): Boolean{
        var layoutParams = content.layoutParams as ConstraintLayout.LayoutParams
        Log.d("OrderDetailActivity", "content height : " + layoutParams.height)
        Log.d("OrderDetailActivity", "height87.height : " + height87.height)
        Log.d("OrderDetailActivity", "height53.height : " + height53.height)
        if(layoutParams.height == height87.height){
            Log.d(OrderDetailActivity::class.simpleName, "is Wide")
            return true
        }
        else{
            Log.d(OrderDetailActivity::class.simpleName, "is not wide")
            return false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == BuyWriteActivity.STOCK_SEARCH){
                val data = data!!.getSerializableExtra("stock") as Stock.ResultMap

                entpNo = data.ENTP_NO
                stkCode = data.STK_CODE
                stkName = data.STK_NM
                orderNo = null

                stockName.text = stkName

                search()
            }
        }
    }

    private fun search(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(orderViewModel.tradeList(0, 9999, stkCode,"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    listData.clear()
                    listData.addAll(it.datas?.resultList!!)
                    vpCompany!!.adapter?.notifyDataSetChanged()
                    Log.d("OrderDetailActivity", "vpCompany!!.adapter?.notifyDataSetChanged()")

                    if(!orderNo.isNullOrEmpty()){
                        for((index, order) in it.datas?.resultList!!.withIndex()){
                            if (!it.datas?.resultList!![index].FAV_CORP_NO.isNullOrEmpty()){
                                isFavorite = true
                            }

                            if(order.ORDER_NO == orderNo){
                                vpCompany.currentItem = index
                                break
                            }
                        }
                    }

                    if(it.datas!!.resultList!!.size > 0){
                        tabLayout.getTabAt(0)?.select()
                    }
                    else{
                        val aAlertDialogBuilder = AlertDialog.Builder(this)
                        aAlertDialogBuilder.setMessage("조회된 데이터가 없습니다.")
                        aAlertDialogBuilder.setPositiveButton("확인") { dialog, which ->
                            dialog.dismiss()

                            btnSearch.callOnClick()
                        }
                        aAlertDialogBuilder.setOnCancelListener {
                            it.dismiss()

                            btnSearch.callOnClick()
                        }

                        val aAlertDialog = aAlertDialogBuilder.create()
                        aAlertDialog.show()
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

    private fun requestNego(orderNo: String){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(chatViewModel.requestNego(PreferenceUtils.getUserId(), orderNo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    ViewUtils.alertDialog(this, "성공적으로 협상요청했습니다."){

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

    private fun favoriteCorp(entpNo: String){
        progress_bar_login.visibility = View.VISIBLE

        val type : String = if (isFavorite!!){
            "DEL"
        } else {
            "ADD"
        }

        disposable.add(orderViewModel.favoriteCorp(PreferenceUtils.getUserId(), entpNo, type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    if (isFavorite!!){
                        btnStar.setImageResource(R.drawable.ico_start_w)
                        //ViewUtils.alertDialog(this, "성공적으로 취소했습니다."){

                        //}
                    } else { 
                        btnStar.setImageResource(R.drawable.ico_start_y)
                        ViewUtils.alertDialog(this, "성공적으로 등록했습니다."){

                        }
                    }

                    isFavorite = !isFavorite!!



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

    private fun loadWeb(tabPosition: Int){
        when(tabPosition){
            0 -> webFragment!!.loadUrl(BuildConfig.SERVER_URL + "/mobile/invst/orderDetails?orderNo=${listData.get(vpCompany.currentItem).ORDER_NO}&rqtClientNo=${listData.get(vpCompany.currentItem).PBLS_CLNT_NO}&orderStkCode=${listData.get(vpCompany.currentItem).STK_CODE}")
            1 -> webFragment!!.loadUrl(BuildConfig.SERVER_URL + "/mobile/invst/stockSucInfo?orderNo=${listData.get(vpCompany.currentItem).ORDER_NO}&rqtClientNo=${listData.get(vpCompany.currentItem).PBLS_CLNT_NO}&orderStkCode=${listData.get(vpCompany.currentItem).STK_CODE}")
            2 -> webFragment!!.loadUrl(BuildConfig.SERVER_URL + "/mobile/invst/orderSucInfo?orderNo=${listData.get(vpCompany.currentItem).ORDER_NO}&rqtClientNo=${listData.get(vpCompany.currentItem).PBLS_CLNT_NO}&orderStkCode=${listData.get(vpCompany.currentItem).STK_CODE}")
            3 -> webFragment!!.loadUrl(BuildConfig.SERVER_URL + "/mobile/invst/infoDetail2?orderNo=${listData.get(vpCompany.currentItem).ORDER_NO}&rqtClientNo=${listData.get(vpCompany.currentItem).PBLS_CLNT_NO}&orderStkCode=${listData.get(vpCompany.currentItem).STK_CODE}")
            4 -> webFragment!!.loadUrl(BuildConfig.SERVER_URL + "/mobile/invst/infoDetail5?orderNo=${listData.get(vpCompany.currentItem).ORDER_NO}&rqtClientNo=${listData.get(vpCompany.currentItem).PBLS_CLNT_NO}&orderStkCode=${listData.get(vpCompany.currentItem).STK_CODE}&LOGIN_ID=${PreferenceUtils.getUserId()}")
        }
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

    override fun onStop() {
        super.onStop()

        disposable.clear()
        toolbar.dispose()
    }

    override fun onBackPressed() {

        if(drawer_layout!!.isDrawerOpen(GravityCompat.END)){
            drawer_layout?.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
        }
    }


    internal inner class CompanyPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            Log.d(OrderDetailActivity::class.simpleName, "instantiateItem($position)")

            var data = listData.get(position)

            if(!data.FAV_CORP_NO.isNullOrEmpty()){
                btnStar.setImageResource(R.drawable.ico_start_y)
            }

            var view: View? = null

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.page_order_chart, container, false)

            view.findViewById<TextView>(R.id.stockCode).text = data.ENTP_NO
            //view.findViewById<TextView>(R.id.stockKind).text = data.CORP_HANGL_NM
            view.findViewById<TextView>(R.id.count).text = numberFormat.format(data.DEAL_QTY ?: 0)
            view.findViewById<TextView>(R.id.price).text = numberFormat.format(data.DEAL_UPRC ?: 0)

            view.findViewById<LinearLayoutCompat>(R.id.btnNego).setOnClickListener {
                requestNego(data.ORDER_NO!!)
            }
            view.findViewById<TextView>(R.id.btnCompany).setOnClickListener {
                var intent = Intent(this@OrderDetailActivity, InvestmentActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("ENTP_NO", entpNo)
                startActivity(intent)
            }

            val chart = view.findViewById<MyLineChart>(R.id.chart)
            if(data.CHART!!.size > 0){
                val values = java.util.ArrayList<Entry>()

                for (i in 0 until data.CHART!!.size) {

                    val `val` = data.CHART!!.get(i).AVERAGE!!.toFloat() - 30
                    values.add(Entry(i.toFloat(), `val`))

                    Log.d(OrderDetailActivity::class.simpleName, "values.add(Entry($i.toFloat(), ${`val`}))")
                }

                chart.setData(values)

                chart.visibility = View.VISIBLE
            }else{
                chart.visibility = View.INVISIBLE
            }

            container.addView(view)

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return listData.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }

    companion object {

        private val PADDING_GAP_DIPS = 15
    }
}
