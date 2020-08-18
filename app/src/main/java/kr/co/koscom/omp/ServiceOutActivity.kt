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
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_service_out.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory

/**
 * 서비스 해지
 */

class ServiceOutActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    private var webFragment: WebFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_service_out)

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
            webFragment = WebFragment()
            var bundle = Bundle()
            bundle.putString("url", BuildConfig.SERVER_URL + "/mobile/mypage/svnAbndGuide?LOGIN_ID="+ PreferenceUtils.getUserId())
            webFragment!!.arguments = bundle
            transaction.replace(R.id.webFragment, webFragment!!)
            transaction.commitAllowingStateLoss()
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
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }

    companion object {
    }
}
