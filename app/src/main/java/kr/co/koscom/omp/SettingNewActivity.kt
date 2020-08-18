package kr.co.koscom.omp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
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
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.enums.SettingTabType
import kr.co.koscom.omp.extension.toResString

/**
 * 설정
 */
class SettingNewActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting_new)

        toolbar.initTitle(R.string.setting_new_title.toResString())
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
            fragment = SettingAlarmFragment()
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
////                tabTextView.setTextColor(Color.parseColor("#ffffff"))
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
//
//        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {1
                val text = tab?.customView as TextView?

                text?.setTypeface(ResourcesCompat.getFont(this@SettingNewActivity, R.font.spoqa_han_sans),Typeface.NORMAL)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val text = tab?.customView as TextView?

                text?.setTypeface(ResourcesCompat.getFont(this@SettingNewActivity, R.font.spoqa_han_sans),Typeface.BOLD)

                val transaction = supportFragmentManager.beginTransaction()

                /**
                 * 0: 알림 설정 및 마케팅 수신동의
                 * 1: 버전정보
                 * 2: 서비스 이용약관
                 * 3: 개인정보 취급처리방침
                 * 4: 저작권 보호방침
                 * 5: 간편 로그인
                 */
                when(SettingTabType.getType(tab!!.position)){

                    SettingTabType.ALARM -> fragment = SettingAlarmFragment()
                    SettingTabType.VERSION -> fragment = SettingVersionFragment()
                    SettingTabType.SERVICE_USETERM -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(Constants.URL_POLICY_USETERMS)}else{
                        initWebFragment(Constants.URL_POLICY_USETERMS)
                    }
                    SettingTabType.USERINFO_POLICY -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(Constants.URL_POLICY_USERINFO)}else{
                        initWebFragment(Constants.URL_POLICY_USERINFO)
                    }
                    SettingTabType.COPYRIGHT -> if(fragment is WebFragment){(fragment!! as WebFragment).loadUrl(Constants.URL_POLICY_COPYRIGHT)}else{
                        initWebFragment(Constants.URL_POLICY_COPYRIGHT)
                    }
                    SettingTabType.EASY_LOGIN -> fragment = SettingEasyLoginFragment()
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


    private fun initWebFragment(url: String){
        fragment = WebFragment()
        var bundle = Bundle()
        bundle.putString("url", url)
        fragment!!.arguments = bundle
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
        Log.d(SettingNewActivity::class.java.simpleName, "fun onNewIntent")

        var pairwiseDID = intent?.getStringExtra("pairwiseDID")
        var publicKey = intent?.getStringExtra("publicKey")
        var clientName = intent?.getStringExtra("clientName")
        var signature = intent?.getStringExtra("signature")
        Log.d(SettingNewActivity::class.java.simpleName, "pairwiseDID : $pairwiseDID")
        Log.d(SettingNewActivity::class.java.simpleName, "publicKey : $publicKey")
        Log.d(SettingNewActivity::class.java.simpleName, "clientName : $clientName")
        Log.d(SettingNewActivity::class.java.simpleName, "signature : $signature")

        ComUtil.handler(pairwiseDID,publicKey)
    }

    companion object
}
