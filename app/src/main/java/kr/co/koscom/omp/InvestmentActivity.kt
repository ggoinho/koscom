package kr.co.koscom.omp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_investment.*
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.enums.InvestTabType
import kr.co.koscom.omp.extension.toResString

/**
 * 종목정보
 */

class InvestmentActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    private var fragment: WebFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_investment)

        toolbar.initTitle(R.string.invest_title.toResString())
        toolbar.initData(this)
        //test

        intent?.extras?.let {
            if(it.getBoolean("isFirstMain")){
                toolbar.setBackButtonImg(R.drawable.icon_home_logo)
            }
        }

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            fragment = WebFragment()
            var bundle = Bundle()
            bundle.putString("url", BuildConfig.SERVER_URL + "/mobile/corpInfo/infoMng?ENTP_NO=${intent.getStringExtra("ENTP_NO") ?: ""}")
            fragment!!.arguments = bundle
            transaction.replace(R.id.subFragment, fragment!!)
            transaction.commitAllowingStateLoss()
        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()

                /**
                 * 0: 기업정보
                 * 1: 전문가진단
                 * 2: 기업알림/홍보/평가자료
                 * 3: 토론게시판
                 * 4: 뉴스
                 * 5: 특허
                 */
                fragment?.clearHistory()
                when(InvestTabType.getType(tab!!.position)){
                    InvestTabType.BUSINESS_INFO -> fragment!!.loadUrl(Constants.URL_BUSINESS_INFO)
                    InvestTabType.EXPERT -> fragment!!.loadUrl(Constants.URL_EXPERT_DIAGNOSIS)
                    InvestTabType.CORPORATE_PROMOTION -> fragment!!.loadUrl(Constants.URL_CORPORATE_PROMOTION)
                    InvestTabType.DISCUSSION_BOARD -> fragment!!.loadUrl(Constants.URL_DISCUSSION_BOARD)
                    InvestTabType.NEWS -> fragment!!.loadUrl(Constants.URL_NEWS)
                    InvestTabType.PATENT -> fragment!!.loadUrl(Constants.URL_PATENT)
                }
                transaction.replace(R.id.subFragment, fragment!!)
                transaction.commitAllowingStateLoss()
            }
        })

        subFragment.post {
            val tab = intent.getIntExtra("tab", 0)
            if(tab > 0){
                tabLayout.getTabAt(tab)?.select()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            super.onBackPressed()
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

            if(fragment != null){
                if(fragment!!.webView!!.canGoBack()){
                    fragment!!.webView!!.goBack()
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

    companion object {
    }
}
