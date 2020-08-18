package kr.co.koscom.omp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.sendbird.syncmanager.utils.ComUtil
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.activity_mypage.tabLayout
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.enums.MyPageTabType
import kr.co.koscom.omp.extension.setAppearance

/**
 * 마이페이지
 */

class MyPageActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mypage)

        toolbar.initTitle("마이페이지")
        toolbar.initData(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
//            fragment = AlarmFragment()
            initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/joinInfoMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&AUTH_TYPE="+PreferenceUtils.getLoginType())
            transaction.replace(R.id.subFragment, fragment!!)
            transaction.commitAllowingStateLoss()

        }

//        for (i in 0 until tabLayout.tabCount) {
//
//            val tab = tabLayout.getTabAt(i)
//            if (tab != null) {
//
//                val tabTextView = TextView(this)
//                tab.customView = tabTextView
//
//                tabTextView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//
//                tabTextView.text = tab.text
//                tabTextView.setTextColor(Color.parseColor("#ffffff"))
////                tabTextView.textSize = 13f
//                tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15F)
//
//                // First tab is the selected tab, so if i==0 then set BOLD typeface
//                if (i == 0) {
//                    tabTextView.setTypeface(ResourcesCompat.getFont(this, R.font.spoqa_han_sans), Typeface.BOLD)
//                }else{
//                    tabTextView.setTypeface(ResourcesCompat.getFont(this, R.font.spoqa_han_sans), Typeface.NORMAL)
//                }
//
//            }
//        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text = tab?.customView as TextView?

                text?.setTypeface(ResourcesCompat.getFont(this@MyPageActivity, R.font.spoqa_han_sans),Typeface.NORMAL)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val text = tab?.customView as TextView?

                text?.setTypeface(ResourcesCompat.getFont(this@MyPageActivity, R.font.spoqa_han_sans),Typeface.BOLD)

                val transaction = supportFragmentManager.beginTransaction()

                /**
                 * 0: 가입정보관리
                 * 1: 나의 주문
                 * 2: 나의 협상
                 * 3: 나의 계약 및 체결
                 * 4: 보유잔고
                 * 5: 증빙서류관리
                 * 6: 서비스해지
                 * 7: 알림수신
                 */
                if(fragment is WebFragment){
                    (fragment as WebFragment).clearHistory()
                }
                when(MyPageTabType.getType(tab!!.position)){

                    MyPageTabType.SIGN_MANAGEMENT -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/joinInfoMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&AUTH_TYPE="+PreferenceUtils.getLoginType())}else{
                        initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/joinInfoMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&AUTH_TYPE="+PreferenceUtils.getLoginType())
                    }
                    MyPageTabType.MY_ORDER -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMyOrdLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")}else{
                        initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMyOrdLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")
                    }
                    MyPageTabType.MY_NEGO -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMyNegoLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")}else{
                        initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMyNegoLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")
                    }
                    MyPageTabType.MY_CONTRACT_CONCLUSION -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMyContLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")}else{
                        initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMyContLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")
                    }
                    MyPageTabType.BALANCE -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMySecBalLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")}else{
                        initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/invstMyPageMySecBalLstMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&list=${intent.getStringExtra("list").also { intent.removeExtra("list") }}")
                    }
                    MyPageTabType.SERVICE_TERMINATION -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/svnAbndGuide?LOGIN_ID="+ PreferenceUtils.getUserId())}else{
                            initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/svnAbndGuide?LOGIN_ID="+ PreferenceUtils.getUserId())
                    }
                    MyPageTabType.DOCUMENT_MANAGEMENT -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/evndDataMng?LOGIN_ID="+PreferenceUtils.getUserId())}else {
                        initWebFragment(BuildConfig.SERVER_URL + "/mobile/mypage/evndDataMng?LOGIN_ID="+PreferenceUtils.getUserId())
                    }
                    MyPageTabType.ALARM -> fragment = AlarmFragment()

                }
                transaction.replace(R.id.subFragment, fragment!!)
                transaction.commitAllowingStateLoss()

                var diff = ((tabLayout.width/tabLayout.tabCount)/tabLayout.tabCount) * 2
//                tabScrollView.smoothScrollTo(diff * tab.position, 0)
            }
        })

        subFragment.post {
            val tab = intent.getIntExtra("tab", 0)
            if(tab > 0){
                tabLayout.getTabAt(tab)?.select()
            }
        }
    }

    fun refreshAlarmCount(){
        toolbar.initData(this)
        if(fragment is AlarmFragment){
            (fragment as AlarmFragment).searchAlarm()
        }
    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (currentFocus != null) {
//            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
//        }
//
//        return super.dispatchTouchEvent(ev)
//    }

    private fun initWebFragment(url: String){
        fragment = WebFragment()
        var bundle = Bundle()
        bundle.putString("url", url)
        fragment!!.arguments = bundle
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            super.onBackPressed()
            return true
        }
        if (id == R.id.menu_alarm) {
            (fragment as WebFragment).goPageJavascript("/mobile/corpInfo/infoMng", "")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

        toolbar.dispose()
    }

    override fun onBackPressed() {

        if(drawer_layout!!.isDrawerOpen(GravityCompat.END)){
            drawer_layout?.closeDrawer(GravityCompat.END)
        }
        else{

            if(fragment != null && fragment is WebFragment){
                if((fragment!! as WebFragment).webView!!.canGoBack()){
                    (fragment!! as WebFragment).webView!!.goBack()
                }
                else{
                    super.onBackPressed()
                    overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
                }
            }
            else{

                super.onBackPressed()
                overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(MyPageActivity::class.java.simpleName, "fun onNewIntent")

        var pairwiseDID = intent?.getStringExtra("pairwiseDID")
        var publicKey = intent?.getStringExtra("publicKey")
        var clientName = intent?.getStringExtra("clientName")
        var signature = intent?.getStringExtra("signature")
        Log.d(MyPageActivity::class.java.simpleName, "pairwiseDID : $pairwiseDID")
        Log.d(MyPageActivity::class.java.simpleName, "publicKey : $publicKey")
        Log.d(MyPageActivity::class.java.simpleName, "clientName : $clientName")
        Log.d(MyPageActivity::class.java.simpleName, "signature : $signature")

        ComUtil.handler(pairwiseDID,publicKey)
    }

    companion object
}
