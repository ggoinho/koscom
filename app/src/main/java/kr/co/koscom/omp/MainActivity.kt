package kr.co.koscom.omp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.scsoft.boribori.data.viewmodel.*
import com.sendbird.syncmanager.utils.PreferenceUtils
import com.signkorea.openpass.sksystemcrypto.SKDefine.v
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progress_bar_login
import kotlinx.android.synthetic.main.fragment_web.*
import kr.co.koscom.omp.data.Constants
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.view.ViewUtils
import java.text.DecimalFormat
import java.util.*

/**
 * 메인화면
 */

class MainActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private val deepLinker = DeepLinker

    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var testViewModel: TestViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var negoViewModel: NegoViewModel
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private var deaLData = ArrayList<Main.Nego>()
    private var contractData = ArrayList<Main.NegoContract>()
    private var dealPagerAdapter: DealPagerAdapter? = null
    private var contractPagerAdapter: ContractPagerAdapter? = null
    private var padding = 0

    val orderList = ArrayList<Order.OrderItem>()
    private val contractList = ArrayList<OrderContract.OrderContractItem>()

    private val numberFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = this

        setContentView(R.layout.activity_drawer)

        viewModelFactory = Injection.provideViewModelFactory(this)
        testViewModel = ViewModelProviders.of(this, viewModelFactory).get(TestViewModel::class.java)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        alarmViewModel = ViewModelProviders.of(this, viewModelFactory).get(AlarmViewModel::class.java)
        negoViewModel = ViewModelProviders.of(this, viewModelFactory).get(NegoViewModel::class.java)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        logo.setOnClickListener {
            Log.d("logo", "logo")
            search()
        }

        myNego.setOnClickListener {
            var intent = Intent(this@MainActivity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 2)
            intent.putExtra("list", "list2")
            startActivity(intent)
        }
        myContract.setOnClickListener {
            var intent = Intent(this@MainActivity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 2)
            intent.putExtra("list", "list3")
            startActivity(intent)
        }

        mainScroll.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if(v!!.scrollY > mainScroll.height/2){
                btnTop.visibility = View.VISIBLE
            }
            else{
                btnTop.visibility = View.INVISIBLE
            }
        }

        btnTop.setOnClickListener {
            mainScroll.scrollY = 0

            appBarLayout.setExpanded(false)

        }

        btnMenu!!.setOnClickListener { drawer_layout!!.openDrawer(GravityCompat.END) }

        btnAlarm.setOnClickListener {
            var intent = Intent(this@MainActivity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            /*try{
                var url2 = "http://speedtest.ftp.otenet.gr/files/test10Mb.db"
                var fileName = "test10Mb.db"
                Log.d(WebFragment::class.simpleName, "fileName : " + fileName)

                val file = File(getExternalFilesDir(null), fileName)
                Log.d(WebFragment::class.simpleName, "file path : " + file.path)
                Log.d(WebFragment::class.simpleName, "getExternalFilesDir(null).toString() : " + getExternalFilesDir(null).toString())
                val youtubeUrl = url2

                val request = DownloadManager.Request(Uri.parse(youtubeUrl))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setTitle("Downloading a video")
                    .setDescription("Downloading Dev Summit")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)3
                    .setDestinationUri(Uri.fromFile(file))
                    .setVisibleInDownloadsUi(true)
                    //.setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                var downloadId = downloadManager!!.enqueue(request)
                Log.d(WebFragment::class.simpleName, "$downloadId path : " + file.path)
            }catch(e: Exception){e.printStackTrace()}*/


        }

        btnOrderList.setOnClickListener {
            var intent = Intent(this@MainActivity, OrderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("gubn",1)
            startActivity(intent)
        }
        btnOrderedList.setOnClickListener {
            var intent = Intent(this@MainActivity, OrderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("gubn",2)
            startActivity(intent)
        }

        padding = (PADDING_GAP_DIPS * resources.displayMetrics.density).toInt()

        dealPagerAdapter = DealPagerAdapter()
        vpMyDeal!!.adapter = dealPagerAdapter
        vpMyDeal!!.currentItem = dealPagerAdapter!!.count / 2
        vpMyDeal!!.setPadding(padding, 0, padding, 0)
        vpMyDeal!!.clipToPadding = false
        vpMyDeal!!.pageMargin = padding / 4
        vpMyDeal.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                dealPage.setText((position+1).toString())
            }
        })
        vpMyDeal.setPageTransformer(false, object: ViewPager.PageTransformer{
            override fun transformPage(page: View, position: Float) {

                if(position <= -1.0F || position >= 1.0F) {
                    page.setAlpha(0.3F);
                } else if( position == 0.0F ) {
                    page.setAlpha(1.0F);
                } else {
                    var alpha = 1.0F - Math.abs(position)
                    if(alpha < 0.3)
                        alpha = 0.3f
                    page.setAlpha(alpha);
                }
            }

        })

        contractPagerAdapter = ContractPagerAdapter()
        vpMyContract!!.adapter = contractPagerAdapter
        vpMyContract!!.currentItem = contractPagerAdapter!!.count / 2
        vpMyContract!!.setPadding(padding, 0, padding, 0)
        vpMyContract!!.clipToPadding = false
        vpMyContract!!.pageMargin = padding / 4
        vpMyContract.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                contractPage.setText((position+1).toString())
            }

        })
        /*vpMyContract.setPageTransformer(false, object: ViewPager.PageTransformer{
            override fun transformPage(page: View, position: Float) {

                if(position <= -0.5F) {
                    page.findViewById<LinearLayout>(R.id.page).setBackgroundResource(R.drawable.shape_rect_fill36)
                } else if(position >= 0.5F) {
                    page.findViewById<LinearLayout>(R.id.page).setBackgroundResource(R.drawable.shape_rect_fill40)
                } else{
                    page.findViewById<LinearLayout>(R.id.page).setBackgroundResource(R.drawable.shape_rect_fill39)
                }
            }

        })*/
        btnContract.setOnClickListener {
            val intent = Intent(this, ContractDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("groupChannelTitle", contractData.get(vpMyContract!!.currentItem).STOCK_TP_CODE_NM)
            intent.putExtra("groupChannelUrl", contractData.get(vpMyContract!!.currentItem).CHANNEL_URL)
            intent.putExtra("orderNo", contractData.get(vpMyContract!!.currentItem).ORDER_NO)

            startActivity(intent)
        }

        listOrder!!.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        horizontalDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider)!!)
        val verticalDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        verticalDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider)!!)

        listOrder!!.addItemDecoration(horizontalDivider)
        listOrder!!.addItemDecoration(verticalDivider)

        listOrder!!.adapter = OrderAdapter(orderList)

        listOrdered!!.layoutManager = LinearLayoutManager(this)
        val divider2 = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider2.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider2)!!)
        listOrdered!!.addItemDecoration(divider2)

        listOrdered!!.adapter = OrderedAdapter(contractList)

        //initNavigation()

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        btnUseAgreement.setOnClickListener {
            var intent = Intent(this, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "서비스 이용약관")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mSrvAdminPoc")
            startActivity(intent)
        }
        btnInfoAgreement.setOnClickListener {
            var intent = Intent(this, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "개인정보취급처리방침")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mPrivacyPolicy")
            startActivity(intent)
        }
        btnRightAgreement.setOnClickListener {
            var intent = Intent(this, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "저작권보호방침")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mCopyrightPolicy")
            startActivity(intent)
        }

        notice.setOnClickListener {
            val intent = Intent(this, CustomerCenterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 0)
            startActivity(intent)
        }

        processDeepLink()

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter(Constants.pushMessageReceived))
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("", Constants.pushMessageReceived)
            searchAlram()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    private fun processDeepLink(){

        val screen = deepLinker.subscribeScreen()
        val url = deepLinker.subscribeUrl()
        val tab = deepLinker.subscribeTab()
        val message = deepLinker.subscribeMessage()

        if(!screen.isNullOrEmpty()){
            var intent = Intent(this, Class.forName(screen))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            intent.putExtra("tab", tab)
            intent.putExtra("message", message)

            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        searchAlram()

        search()
    }

    private fun search(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(negoViewModel.searchMain(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    if(!it.datas!!.upBoardList!!.CLNT_TTL.isNullOrEmpty()){
                        notice.text = it.datas!!.upBoardList!!.CLNT_TTL
                        noticeZone.visibility = View.VISIBLE
                    }

                    if(it.datas!!.myNegoList!!.size > 0){
                        deaLData.clear()
                        deaLData.addAll(it.datas!!.myNegoList!!)
                        vpMyDeal.adapter?.notifyDataSetChanged()
                        vpMyDeal!!.currentItem = it.datas!!.myNegoList!!.size / 2

                        vpMyDeal.post {
                            dealPage.setText((vpMyDeal.currentItem+1).toString())
                            dealTotal.setText(vpMyDeal.adapter!!.count.toString())
                        }
                    }
                    else{
                        myDealZone.visibility = View.GONE
                    }

                    if(it.datas!!.myContList!!.size > 0){
                        contractData.clear()
                        contractData.addAll(it.datas!!.myContList!!)
                        vpMyContract.adapter?.notifyDataSetChanged()
                        vpMyContract!!.currentItem = it.datas!!.myContList!!.size / 2

                        vpMyContract.post {
                            contractPage.setText((vpMyContract.currentItem+1).toString())
                            contractTotal.setText(vpMyContract.adapter!!.count.toString())
                        }
                    }
                    else{
                        myContractZone.visibility = View.GONE
                    }

                    orderList.clear()
                    orderList.addAll(it.datas!!.orderList!!)
                    listOrder.adapter?.notifyDataSetChanged()

                    if(orderList.size == 0){
                        nothing1.visibility = View.VISIBLE
                    }
                    else{
                        nothing1.visibility = View.GONE
                    }

                    contractList.clear()
                    contractList.addAll(it.datas!!.trdMgmtList!!)
                    listOrdered.adapter?.notifyDataSetChanged()

                    if(contractList.size == 0){
                        nothing2.visibility = View.VISIBLE
                    }
                    else{
                        nothing2.visibility = View.GONE
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

    private fun searchAlram(){

        disposable.add(alarmViewModel.searchNotReadCount(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){
                    alarm.text = it.datas!!.result.toString()
                }
            }, {
                it.printStackTrace()
            }))
    }

    override fun onBackPressed() {

        if(drawer_layout!!.isDrawerOpen(GravityCompat.END)){
            drawer_layout?.closeDrawer(GravityCompat.END)
        }
        else{
            ViewUtils.alertLogoutDialog(this, "로그아웃하시겠습니까?"){
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

                finish()
            }
        }
    }


    internal inner class DealPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            var data = deaLData.get(position)

            var view: View? = null

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.page_mydeal, container, false)
            view.findViewById<TextView>(R.id.stockName).text = data.CORP_HANGL_NM
            view.findViewById<TextView>(R.id.stockName2).text = data.STOCK_TP_CODE_NM
            view.findViewById<TextView>(R.id.stockGubn).text = data.STOCK_TP_CODE_NM + if(data.SEC_KIND_DTL_TP_CODE == "02"){"(상환)"}else if(data.SEC_KIND_DTL_TP_CODE == "03"){"(전환상환)"}else{""}
            view.findViewById<TextView>(R.id.count).text = data.DEAL_QTY
            view.findViewById<TextView>(R.id.price).text = data.DEAL_UPRC
            view.findViewById<TextView>(R.id.host).text = data.NEGO_REQST_CLNT_NM

            view.findViewById<TextView>(R.id.btnDeny).setOnClickListener {
                disposable.add(chatViewModel.denyNego(PreferenceUtils.getUserId(), data.ORDER_NO!!, data.CHANNEL_URL!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        Log.d(LoginActivity::class.simpleName, "response : " + it)

                        if("0000".equals(it.rCode)){
                            ViewUtils.alertDialog(this@MainActivity, "거절했습니다."){}


                        }else{

                            ViewUtils.showErrorMsg(this@MainActivity, it.rCode, it.rMsg)

                        }
                        progress_bar_login.visibility = View.INVISIBLE
                    }, {
                        progress_bar_login.visibility = View.INVISIBLE
                        it.printStackTrace()
                        ViewUtils.alertDialog(this@MainActivity, "네트워크상태를 확인해주세요."){}
                    }))
            }
            view.findViewById<LinearLayoutCompat>(R.id.btnAccept).setOnClickListener {
                disposable.add(chatViewModel.acceptNego(PreferenceUtils.getUserId(), data.ORDER_NO!!, data.CHANNEL_URL!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        Log.d(LoginActivity::class.simpleName, "response : " + it)

                        if("0000".equals(it.rCode)){

                            ViewUtils.alertDialog(this@MainActivity, "성공적으로 수락했습니다."){

                            }

                        }else{
                            ViewUtils.showErrorMsg(this@MainActivity, it.rCode, it.rMsg)

                        }
                        progress_bar_login.visibility = View.INVISIBLE
                    }, {
                        progress_bar_login.visibility = View.INVISIBLE
                        it.printStackTrace()
                        ViewUtils.alertDialog(this@MainActivity, "네트워크상태를 확인해주세요."){}
                    }))
            }

            container.addView(view)

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return deaLData.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }
    }

    internal inner class ContractPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            var data = contractData.get(position)

            var view: View? = null

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.page_mycontract, container, false)
            view.findViewById<TextView>(R.id.stockName).text = data.CORP_HANGL_NM
            view.findViewById<TextView>(R.id.stockGubn).text = data.STOCK_TP_CODE_NM + if(data.SEC_KIND_DTL_TP_CODE == "02"){"(상환)"}else if(data.SEC_KIND_DTL_TP_CODE == "03"){"(전환상환)"}else{""}
            view.findViewById<TextView>(R.id.count).text = data.DEAL_QTY
            view.findViewById<TextView>(R.id.price).text = data.DEAL_UPRC
            view.findViewById<TextView>(R.id.host).text = data.NEGO_REQST_CLNT_NM

            if(data.NEGO_SETT_STAT_CODE == "204" || data.NEGO_SETT_STAT_CODE == "206"){
                view.findViewById<ImageView>(R.id.imgStatus).setImageResource(R.drawable.ico_state_11_success)
                view.findViewById<TextView>(R.id.status).setText("계약")
            }
            else{
                view.findViewById<ImageView>(R.id.imgStatus).setImageResource(R.drawable.ico_state_12_complete)
                view.findViewById<TextView>(R.id.status).setText("체결")
            }

            /*view.findViewById<LinearLayoutCompat>(R.id.btnApprove).setOnClickListener {
                val intent = Intent(this@MainActivity, ContractDetailActivity::class.java)
                intent.putExtra("groupChannelTitle", data.STOCK_TP_CODE_NM)
                intent.putExtra("groupChannelUrl", data.CHANNEL_URL)
                intent.putExtra("orderNo", data.ORDER_NO)

                startActivity(intent)
            }

            if(data.NEGO_SETT_STAT_CODE == "206"){
                view.findViewById<TextView>(R.id.info).xt = "입금을 진행해주세요"
                view.findViewById<LinearLayoutCompat>(R.id.btnApprove).visibility = View.VISIBLE

                view.findViewById<LinearLayoutCompat>(R.id.btnApprove).setOnClickListener {
                    var intent = Intent(this@MainActivity, EscrowSellerActivity::class.java)
                    if(data.DEAL_TP == "20"){
                        intent = Intent(this@MainActivity, EscrowBuyerActivity::class.java)
                    }

                    intent.putExtra("groupChannelTitle", data.STOCK_TP_CODE_NM)
                    intent.putExtra("groupChannelUrl", data.CHANNEL_URL)
                    intent.putExtra("orderNo", data.ORDER_NO)

                    startActivity(intent)
                }
            }*/

            if(position % 3 == 0){
                view.findViewById<LinearLayout>(R.id.page).setBackgroundResource(R.drawable.shape_rect_fill36)
            }
            else if(position % 3 == 1){
                view.findViewById<LinearLayout>(R.id.page).setBackgroundResource(R.drawable.shape_rect_fill39)
            }
            else if(position % 3 == 2){
                view.findViewById<LinearLayout>(R.id.page).setBackgroundResource(R.drawable.shape_rect_fill40)
            }

            container.addView(view)

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return contractData.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }
    }

    internal inner class OrderAdapter(val list: ArrayList<Order.OrderItem>) :
        RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            val row = itemView.findViewById<LinearLayout>(R.id.llRow)
            val stockName = itemView.findViewById<TextView>(R.id.stockName)
            val stockName2 = itemView.findViewById<TextView>(R.id.stockName2)
            val stockGubn = itemView.findViewById<TextView>(R.id.stockGubn)
            val count = itemView.findViewById<TextView>(R.id.count)
            val price = itemView.findViewById<TextView>(R.id.price)
            var status1 = itemView.findViewById<LinearLayoutCompat>(R.id.status1)
            var status2 = itemView.findViewById<LinearLayoutCompat>(R.id.status2)
            var status3 = itemView.findViewById<LinearLayoutCompat>(R.id.status3)
            var status4 = itemView.findViewById<LinearLayoutCompat>(R.id.status4)
            var btnNego = itemView.findViewById<LinearLayoutCompat>(R.id.btnNego)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_orders, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
            val data = list!![position]

            holder.stockName.text = data.CORP_HANGL_NM
            holder.stockGubn.text = data.STOCK_TP_CODE_NM
            holder.count.text = numberFormat.format(data.DEAL_QTY)
            holder.price.text = numberFormat.format(data.DEAL_UPRC)
            //holder.stockGubn.text = data.SEC_KIND_TP_CODE + if(data.SEC_KIND_DTL_TP_CODE == "02"){"(상환)"}else if(data.SEC_KIND_DTL_TP_CODE == "03"){"(전환상환)"}else{""}

            holder.status1.visibility = View.INVISIBLE
            holder.status2.visibility = View.INVISIBLE
            holder.status3.visibility = View.INVISIBLE
            holder.status4.visibility = View.INVISIBLE

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

            holder.row.setOnClickListener {
                val intent = Intent(this@MainActivity, OrderDetailActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("entpNo", data.ENTP_NO)
                intent.putExtra("stkCode", data.STK_CODE)
                intent.putExtra("stkName", data.STK_NM)
                intent.putExtra("orderNo", data.ORDER_NO)
                startActivity(intent)
            }
            holder.btnNego.setOnClickListener {
                var intent = Intent(this@MainActivity, OrderPopupActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("orderItem", data)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return list!!.size
        }
    }

    internal inner class OrderedAdapter(val list: ArrayList<OrderContract.OrderContractItem>) :
        RecyclerView.Adapter<OrderedAdapter.ViewHolder>() {

        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            val row = itemView.findViewById<LinearLayout>(R.id.llRow)
            val stockName = itemView.findViewById<TextView>(R.id.stockName)
            val stockName2 = itemView.findViewById<TextView>(R.id.stockName2)
            val count = itemView.findViewById<TextView>(R.id.count)
            val price = itemView.findViewById<TextView>(R.id.price)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_ordered, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderedAdapter.ViewHolder, position: Int) {
            val data = list!![position]

            holder.stockName.text = data.CORP_HANGL_NM
            holder.stockName2.text = data.STOCK_TP_CODE_NM
            holder.count.text = numberFormat.format(data.DEAL_QTY)
            holder.price.text = numberFormat.format(data.DEAL_UPRC ?: 0)

            holder.row.setOnClickListener {
                var intent = Intent(this@MainActivity, ContractPopupActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("contractItem", data)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return list!!.size
        }
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)

        mainActivity = null

        disposable.add(loginViewModel.logout(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(LoginActivity::class.simpleName, "response : " + it)
            }, {
                it.printStackTrace()
            }))

        super.onDestroy()
    }

    companion object {
        var mainActivity: MainActivity? = null

        private val PADDING_GAP_DIPS = 15

        var REQUIRE_REFRESH = true

    }
}
