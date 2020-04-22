package kr.co.koscom.omp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.LoginViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_detail.toolbar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting.progress_bar_login
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.view.ViewUtils

/**
 * 알림 및 서비스설정
 */

class SettingActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var chatViewModel: ChatViewModel

    private val disposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting)

        toolbar.initTitle("알림 및 서비스 설정")
        toolbar.initData(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)


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
        btnSignPage.setOnClickListener {
            var intent = Intent(this, SignLaunchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        layoutVersionInfo.setOnClickListener {
            Intent(this, VersionInfoActivity::class.java).run {
                startActivity(this)
            }
        }

        getUserPush()
    }

    private fun getUserPush(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(loginViewModel.getUserPush(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    if(it.datas!!.notificationAgreement!!){
                        alarmYn.isChecked = true

                        Preference.setPushYn(this, "Y")
                    }else{
                        alarmYn.isChecked = false

                        Preference.setPushYn(this, "N")
                    }

                    if(it.datas!!.adAgreement!!){
                        adYn.isChecked = true

                        Preference.setAdYn(this, "Y")
                    }else{
                        adYn.isChecked = false

                        Preference.setAdYn(this, "N")
                    }

                    bindAlarmToggle(alarmYn)
                    bindAdToggle(adYn)

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

    private fun registToken(listener: Runnable?){
        progress_bar_login.visibility = View.VISIBLE
        disposable.add(chatViewModel.registPushToken(PreferenceUtils.getUserId(), Preference.getPushToken(this)!!, Preference.getPushYn(this) == "Y", Preference.getAdYn(this) == "Y")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"registPushToken : success")

                    if(listener != null){
                        listener.run()
                    }
                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)
                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    override fun onBackPressed() {
        if(drawer_layout!!.isDrawerOpen(GravityCompat.END)){
            drawer_layout?.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
        }
    }

    private fun bindAlarmToggle(toggleButton: ToggleButton){

        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            var dlg = Dialog(this@SettingActivity);

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var btnClose = dlg.findViewById<LinearLayout>(R.id.btnClose)
            btnClose.setOnClickListener {
                dlg.cancel()
            }
            var confirmZone = dlg.findViewById<LinearLayout>(R.id.confirmZone)
            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var btnOk = dlg.findViewById<TextView>(R.id.btnOk)
            var message = dlg.findViewById<TextView>(R.id.message)
            if(b){
                Preference.setPushYn(this@SettingActivity, "Y")

                registToken(null)

                message.text = "Push 수신동의 처리를 완료하였습니다."
                btnOk.setOnClickListener { dlg.dismiss() }

            }
            else{
                btnOk.visibility = View.GONE
                confirmZone.visibility = View.VISIBLE

                message.text = "알림설정을 해제하시면 비상장주식거래에 필요한 Push 서비스가 중단됩니다.\n해제하시겠습니까?"
                btnConfirm.setOnClickListener {
                    Preference.setPushYn(this@SettingActivity, "N")

                    registToken(null)

                    dlg.dismiss()
                }
                btnCancel.setOnClickListener {
                    dlg.cancel()
                }
                dlg.setOnCancelListener {
                    toggleButton.setOnCheckedChangeListener(null)
                    toggleButton.isChecked = true
                    bindAlarmToggle(toggleButton)
                }
            }
        }

    }

    private fun bindAdToggle(toggleButton: ToggleButton){

        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            var dlg = Dialog(this@SettingActivity);

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var btnClose = dlg.findViewById<LinearLayout>(R.id.btnClose)
            btnClose.setOnClickListener {
                dlg.cancel()
            }
            var confirmZone = dlg.findViewById<LinearLayout>(R.id.confirmZone)
            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var btnOk = dlg.findViewById<TextView>(R.id.btnOk)
            var message = dlg.findViewById<TextView>(R.id.message)
            if(b){
                Preference.setAdYn(this@SettingActivity, "Y")

                registToken(null)

                message.text = "마케팅 수신동의 처리를 완료하였습니다."
                btnOk.setOnClickListener { dlg.dismiss() }

            }
            else{
                btnOk.visibility = View.GONE
                confirmZone.visibility = View.VISIBLE

                message.text = "마케팅 수신동의를 해제하시면 비상장주식거래에 필요한 정보를 받아보실 수 없습니다.\n해제하시겠습니까?"
                btnConfirm.setOnClickListener {
                    Preference.setAdYn(this@SettingActivity, "N")

                    registToken(null)

                    dlg.dismiss()
                }
                btnCancel.setOnClickListener {
                    dlg.cancel()
                }
                dlg.setOnCancelListener {
                    toggleButton.setOnCheckedChangeListener(null)
                    toggleButton.isChecked = true
                    bindAdToggle(toggleButton)
                }
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
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    companion object {
        val REQUEST_CODE = 1000
    }
}
