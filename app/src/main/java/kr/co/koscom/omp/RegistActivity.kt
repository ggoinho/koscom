package kr.co.koscom.omp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.syncmanager.utils.ComUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_web.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.view.MyBottomNavigationView

// kr.co.koscom.omp
// BeMyUNICORN

class RegistActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    private var webFragment: WebFragment? = null
    var bottomNavView: MyBottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web)

        toolbar.initTitle("투자자 서비스 가입")
        toolbar.hideMenu()
        toolbar.initData(this)

        viewModelFactory = Injection.provideViewModelFactory(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            webFragment = WebFragment()
            var bundle = Bundle()
            bundle.putString("url", BuildConfig.SERVER_URL + "/mobile/join/invstIndivSvJoinCertifi")
            bundle.putBoolean("isBottomHideView", true)
            webFragment!!.arguments = bundle
            transaction.replace(R.id.webFragment, webFragment!!)
            transaction.commitAllowingStateLoss()
        }

        bottomNavView = findViewById(R.id.bottom_navigation_view)
        bottomNavView?.toGone()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(RegistActivity::class.java.simpleName, "fun onNewIntent")

        var pairwiseDID = intent?.getStringExtra("pairwiseDID")
        var publicKey = intent?.getStringExtra("publicKey")
        var clientName = intent?.getStringExtra("clientName")
        var signature = intent?.getStringExtra("signature")
        Log.d(RegistActivity::class.java.simpleName, "pairwiseDID : $pairwiseDID")
        Log.d(RegistActivity::class.java.simpleName, "publicKey : $publicKey")
        Log.d(RegistActivity::class.java.simpleName, "clientName : $clientName")
        Log.d(RegistActivity::class.java.simpleName, "signature : $signature")

        ComUtil.handler(pairwiseDID,publicKey)

    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (getCurrentFocus() != null) {
//            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        if(webFragment != null){
            if(webFragment!!.webView!!.canGoBack()){
                webFragment!!.webView!!.goBack()
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

    override fun onStop() {
        super.onStop()

        disposable.clear()
        toolbar.dispose()
    }

    companion object {
    }
}
