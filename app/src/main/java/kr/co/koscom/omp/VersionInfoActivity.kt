package kr.co.koscom.omp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_version_info.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.viewmodel.UpdateViewModel
import kr.co.koscom.omp.extension.enableView
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.view.ViewUtils

class VersionInfoActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var updateViewModel: UpdateViewModel
    private val disposable = CompositeDisposable()

    private var currentServerVersion = ""

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version_info)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)
        updateViewModel = ViewModelProviders.of(this, viewModelFactory).get(UpdateViewModel::class.java)

        init()
        initListener()
        checkVersion()
    }

    private fun init(){

        toolbar.initTitle(R.string.versioninfo_top_title.toResString(this))
        toolbar.initData(this)
    }

    private fun initListener(){

        tvUpdate.setOnClickListener {
            val appPackageName = packageName // getPackageName() from Context or Activity object
            try {
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }

    private fun checkVersion(){

        progressbar.toVisible()
        disposable.add(updateViewModel.updateVersion("AOS")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progressbar.toInvisible()

                Log.d(VersionInfoActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){
                    val currentAppVersion = getAppVersion()
                    currentServerVersion = it.datas?.resultList?.END_APP_VER ?: currentAppVersion

                    if (versionCompare(currentAppVersion,currentServerVersion)){
                        tvUpdate.enableView(false)
                        tvUpdateContents.text = R.string.versioninfo_update_contents2.toResString(this)
                    } else {
                        tvUpdate.enableView(true)
                        tvUpdateContents.text = R.string.versioninfo_update_contents.toResString(this)
                    }

                    tvVersionInfo.text = String.format(R.string.versioninfo_version_contents.toResString(this),
                        Build.VERSION.RELEASE,
                        "V.${getAppVersion()}",
                        "V.${currentServerVersion}",
                        Build.MODEL)

                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)
                }

            }, {
                progressbar.toInvisible()
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }


    private fun versionCompare(appVersion : String?, serverVersion : String?) : Boolean{
        if (appVersion != null && serverVersion != null) {
            if (serverVersion.compareTo(appVersion) > 0){
                return false
            } else {
                return true
            }
        }
        return true
    }


    private fun getAppVersion(): String{
        var appVersion = ""
        try{
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            appVersion = packageInfo.versionName

        }catch (e: PackageManager.NameNotFoundException){}

        return appVersion

    }


    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.END)){
            drawer_layout.closeDrawer(GravityCompat.END)
        }else{
            super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        toolbar.dispose()
    }
}
