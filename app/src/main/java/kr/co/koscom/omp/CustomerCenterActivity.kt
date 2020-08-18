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
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_customer_center.*
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.enums.CustomerTabType
import kr.co.koscom.omp.extension.toResString

/**
 * 고객센터
 */

class CustomerCenterActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    private var fragment: WebFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_customer_center)

        toolbar.initTitle(R.string.customer_title.toResString())
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
            fragment = WebFragment()
            var bundle = Bundle()
            bundle.putString("url", Constants.URL_GONGJI+"?LOGIN_ID=${PreferenceUtils.getUserId()}")
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
                 * 0: 공지사항
                 * 1: 서비스소개
                 * 2: FAQ
                 * 3: 1:1문
                 */

                fragment?.clearHistory()
                when(CustomerTabType.getType(tab!!.position)){
                    CustomerTabType.GONGJI -> fragment!!.loadUrl(Constants.URL_GONGJI+"?LOGIN_ID=${PreferenceUtils.getUserId()}")
                    CustomerTabType.SERVICE -> fragment!!.loadUrl(Constants.URL_SERVICE_GUIDE)
                    CustomerTabType.FAQ -> fragment!!.loadUrl(Constants.URL_FAQ+"?LOGIN_ID=${PreferenceUtils.getUserId()}")
                    CustomerTabType.ONE_AND_ONE -> fragment!!.loadUrl(Constants.URL_ONE_AND_ONE_QUESTION+"?LOGIN_ID=${PreferenceUtils.getUserId()}")
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
