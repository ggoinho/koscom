package kr.co.koscom.omp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.LoginViewModel
import com.sendbird.android.SendBird
import com.sendbird.syncmanager.ConnectionManager
import com.sendbird.syncmanager.utils.Base64Utils
import com.sendbird.syncmanager.utils.ComUtil
import com.sendbird.syncmanager.utils.PreferenceUtils
import com.signkorea.openpass.interfacelib.SKCallback
import com.signkorea.openpass.interfacelib.SKCertManager
import com.signkorea.openpass.interfacelib.SKConstant
import com.signkorea.openpass.sksystemcrypto.SKSystemCertInfo
import com.signkorea.openpass.sksystemcrypto.SKUtil
import com.softforum.xecurekeypad.XKConstants
import com.softforum.xecurekeypad.XKKeypadCustomInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.view.MyDialog
import kr.co.koscom.omp.view.ViewUtils

/**
 * 로그인
 */

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var loginViewModel: LoginViewModel

    private val disposable = CompositeDisposable()

    private var secureKeypad: View? = null

    private val mE2EURL = BuildConfig.SERVER_URL + "/xkp/xkservice"

    var mainProgress: MyDialog? = null

    // OpenPass 연동 단계
    private val _STEP_IDLE = -1
    private val _STEP_INITIALIZE = 0                       // OpenPass 초기화
    private val _STEP_CHECK_VERSION = 1                    // 버전 체크
    private val _STEP_SEND_CUSTOM_UI_RESOURCES = 2         // 커스컴 UI 리소스 설정
    private val _STEP_COMPLETED = 3                        // 연동 완료
    private var mCurrentStep = _STEP_IDLE


    private val METHOD_PERSON = 0                           //개인
    private val METHOD_COMPANY = 1                          //법인
    private val METHOD_INTERGRATED = 2                      //통합인증 앱
    private val METHOD_DID = 3                              //DID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginActivity = this

        setContentView(R.layout.activity_login)

        tv_title.setText(Html.fromHtml(getString(R.string.login_title)))
        tv_sub_title.setText(Html.fromHtml(getString(R.string.login_auth_type)))


        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)


        chkPerson.setOnClickListener {
            clickMethod(METHOD_PERSON)
        }
        chkCompany.setOnClickListener {
            clickMethod(METHOD_COMPANY)
        }
        chkIntegrated.setOnClickListener {
            clickMethod(METHOD_INTERGRATED)
        }
        chkDid.setOnClickListener {
            clickMethod(METHOD_DID)
        }

        chkPerson.callOnClick()
        //chkDid!!.visibility = View.INVISIBLE
        chkIntegrated.callOnClick()

        btnLogin!!.setOnClickListener {

            if(integratedCheckbox.isSelected){

//                if(BuildConfig.DEBUG){
//                    login("1","2222")
//                    //login("1","07")
//                }
//                else{
                    mCurrentStep = _STEP_IDLE
                    proceedNextStep()
//                }

            }
            else{
                //Login type = L , verify = "J" , sign = "S"
                getSkdid("L"){ didNonce: String, didSvcPublic: String ->
                    val url = "initial://reqService?orgName=Koscom&vcType=login&svcPublicDID=${Base64Utils.getBase64encode(didSvcPublic)}&nonce=${Base64Utils.getBase64encode(didNonce)}"

                    Log.d("LoginActivity", "didSvcPublic : $didSvcPublic \ndidNonce : $didNonce")
                    Log.d("LoginActivity", "did scheme url : $url")

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)

                }

            }

        }

        btnRegist!!.setOnClickListener {
            var intent = Intent(this, RegistActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btnClose!!.setOnClickListener {
            closeScreen()
        }

        password_chatId.setE2EURL(mE2EURL)
        password_chatId.setXKViewType(XKConstants.XKViewType.XKViewTypeNormalView)
        password_chatId.setMaskString("*")
        password_chatId.setXKKeypadType (XKConstants.XKKeypadType.XKKeypadTypeQwerty)

        password_chatId.setXKDimAlpha(0.5f)
        password_chatId.setTopViewOnOff(false)
        password_chatId.setLayoutIdentifier(R.id.container)

        /*--------------------------------------------------------------------------------------*
		 * 보안 세션 만료되었을 경우의 결과 처리를 위한 인터페이스 클래스 & 함수 추가.
		 * 원하는 결과 처리 추가하면됨.
		 * 현재는 알림창으로 보안 세션 만료를 알리고 확인 버튼을 클릭하면 키패드가 내려감.
		 *--------------------------------------------------------------------------------------*/
        val aXKKeypadCustomInterface = XKKeypadCustomInterface { pContext ->
            val aAlertDialogBuilder = AlertDialog.Builder(pContext)
            aAlertDialogBuilder.setMessage("보안 세션이 만료되었습니다.\n다시 실행해 주세요.")
            aAlertDialogBuilder.setPositiveButton("확인") { dialog, which ->
                password_chatId.inputCancel()
            }

            val aAlertDialog = aAlertDialogBuilder.create()
            aAlertDialog.show()
        }

        password_chatId.setXKKeypadCustomInterface(aXKKeypadCustomInterface)

        initToken()

        val redirect_regist = intent.getBooleanExtra("redirect_regist", false)
        if (redirect_regist) {

            var intent = Intent(this, RegistActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    /**
     * 상단 로그인 방법 선택
     */
    private fun clickMethod(method: Int){

        when(method){
            METHOD_PERSON ->{   //개인
                loginWayZone!!.visibility = View.VISIBLE
                btnPersonalLogin!!.visibility = View.VISIBLE
                imgLoginKey!!.visibility = View.GONE
                btnCompanyLogin!!.visibility = View.GONE

                chkCompany.setBackgroundResource(R.drawable.shape_rect_fill33)
                companyCheck.isSelected = false
                companyTitle.setTextColor(Color.parseColor("#333333"))

                chkPerson.setBackgroundResource(R.drawable.shape_rect_fill31)
                personCheck.isSelected = true
                personTitle.setTextColor(Color.parseColor("#3348ae"))
            }
            METHOD_COMPANY ->{  //법인
                loginWayZone!!.visibility = View.GONE
                btnPersonalLogin!!.visibility = View.GONE
                imgLoginKey!!.visibility = View.VISIBLE
                btnCompanyLogin!!.visibility = View.VISIBLE

                chkPerson.setBackgroundResource(R.drawable.shape_rect_fill33)
                personCheck.isSelected = false
                personTitle.setTextColor(Color.parseColor("#333333"))

                chkCompany.setBackgroundResource(R.drawable.shape_rect_fill31)
                companyCheck.isSelected = true
                companyTitle.setTextColor(Color.parseColor("#3348ae"))
            }
            METHOD_INTERGRATED ->{  //통합인증 앱
                chkDid.setBackgroundResource(R.drawable.shape_rect_fill33)
                didCheckbox.isSelected = false
                didTitle.setTextColor(Color.parseColor("#000000"))
                didIcon.setImageResource(R.drawable.ico_login_did)

                chkIntegrated.setBackgroundResource(R.drawable.shape_rect_fill31)
                integratedCheckbox.isSelected = true
                integratedTitle.setTextColor(Color.parseColor("#3348ae"))
                integratedIcon.setImageResource(R.drawable.ico_login_key_b)

                PreferenceUtils.setLoginType("openpass")
            }
            METHOD_DID ->{  //DID
                //DID 서버 기동 오류로 인한 임시 주석처리
                /*
                chkIntegrated.setBackgroundResource(R.drawable.shape_rect_fill33)
                integratedCheckbox.isSelected = false
                integratedTitle.setTextColor(Color.parseColor("#000000"))
                integratedIcon.setImageResource(R.drawable.ico_login_key)

                chkDid.setBackgroundResource(R.drawable.shape_rect_fill31)
                didCheckbox.isSelected = true
                didTitle.setTextColor(Color.parseColor("#3348ae"))
                didIcon.setImageResource(R.drawable.ico_login_did_b)

                PreferenceUtils.setLoginType("did")
                */
            }

        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d(LoginActivity::class.java.simpleName, "onNewIntent()")

        var pairwiseDID = intent?.getStringExtra("pairwiseDID")
        var publicKey = intent?.getStringExtra("publicKey")
        var clientName = intent?.getStringExtra("clientName")
        var signature = intent?.getStringExtra("signature")
        Log.d(LoginActivity::class.java.simpleName, "pairwiseDID : $pairwiseDID")
        Log.d(LoginActivity::class.java.simpleName, "publicKey : $publicKey")
        Log.d(LoginActivity::class.java.simpleName, "clientName : $clientName")
        Log.d(LoginActivity::class.java.simpleName, "signature : $signature")

        if(!pairwiseDID.isNullOrEmpty()){
            login("2", pairwiseDID!!)
        }
    }

    private fun closeScreen(){
        if(PreferenceUtils.getUserId().isNullOrEmpty()){
            var intent = Intent(this@LoginActivity, TutorialActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    private fun logintest(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(chatViewModel.logintest(password_chatId.data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_bar_login.visibility = View.INVISIBLE

                Log.d(LoginActivity::class.simpleName, "response : " + it)
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun login(gubn: String, certified: String){
        progress_bar_login.visibility = View.VISIBLE

        //CL0000140 : 601333332641178467171688483832714453434050407531286401470446502543282728771
        loginCertify(gubn, certified) { userId ->

            chatLogin(userId) {accessToken : String ->

                ConnectionManager.login(userId,accessToken) { user, e ->

                    if (e != null) {
                        Snackbar.make(container, "chatting login failed", Snackbar.LENGTH_LONG).show();

                        e.printStackTrace()
                        PreferenceUtils.setConnected(false)
                    } else {

                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                            Log.d(LoginActivity::class.java.simpleName,"firebase token : " + instanceIdResult.token)

                            Preference.setPushToken(this@LoginActivity, instanceIdResult.token)

                            registToken(userId, instanceIdResult.token, object: Runnable{
                                override fun run() {
                                    Log.d(LoginActivity::class.java.simpleName,"registerPushTokenForCurrentUser : " + instanceIdResult.token)

                                    //sendPush(userId, object: Runnable{
                                    //    override fun run() {

                                    SendBird.registerPushTokenForCurrentUser(instanceIdResult.token,
                                        SendBird.RegisterPushTokenWithStatusHandler { pushTokenRegistrationStatus, e ->
                                            if (e != null) {

                                                //Toast.makeText(applicationContext, "registerPushTokenForCurrentUser failed",Toast.LENGTH_LONG).show()

                                                e.printStackTrace()
                                                return@RegisterPushTokenWithStatusHandler
                                            }

                                            if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                                                //Toast.makeText(applicationContext, "Connection required to register push token.", Toast.LENGTH_LONG).show()

                                                Log.d(LoginActivity::class.java.simpleName, "Connection required to register push token.")
                                            } else {
                                                Log.d(LoginActivity::class.java.simpleName, "registerPushTokenForCurrentUser success.")
                                            }
                                        })


                                    PreferenceUtils.setConnected(true)
                                    // Update the user's nickname
                                    /*SendBird.updateCurrentUserInfo(chatId.text.toString(), null,
                                                    SendBird.UserInfoUpdateHandler { e ->
                                                        if (e != null) {
                                                            e.printStackTrace()
                                                            // Error!
                                                            Snackbar.make(window.decorView, "Update user nickname failed", Snackbar.LENGTH_LONG).show();

                                                            return@UserInfoUpdateHandler
                                                        }

                                                        PreferenceUtils.setUserId(chatId.text.toString())
                                                        //PreferenceUtils.setNickname(chatId.text.toString())
                                                    })*/


                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                    finish()
                                    //    }
                                    //})
                                }
                            })


                        }

                    }
                }
            }
        }
    }

    private fun login(gubn: String, certified: String, signData: String, opCode: String){
        progress_bar_login.visibility = View.VISIBLE

        //CL0000140 : 601333332641178467171688483832714453434050407531286401470446502543282728771
        loginCertify(gubn, certified,signData,opCode) { userId ->

            chatLogin(userId) {accessToken : String ->

                ConnectionManager.login(userId,accessToken) { user, e ->

                    if (e != null) {
                        Snackbar.make(container, "chatting login failed", Snackbar.LENGTH_LONG).show();

                        e.printStackTrace()
                        PreferenceUtils.setConnected(false)
                    } else {

                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                            Log.d(LoginActivity::class.java.simpleName,"firebase token : " + instanceIdResult.token)

                            Preference.setPushToken(this@LoginActivity, instanceIdResult.token)

                            registToken(userId, instanceIdResult.token, object: Runnable{
                                override fun run() {
                                    Log.d(LoginActivity::class.java.simpleName,"registerPushTokenForCurrentUser : " + instanceIdResult.token)

                                    //sendPush(userId, object: Runnable{
                                    //    override fun run() {

                                    SendBird.registerPushTokenForCurrentUser(instanceIdResult.token,
                                        SendBird.RegisterPushTokenWithStatusHandler { pushTokenRegistrationStatus, e ->
                                            if (e != null) {

                                                //Toast.makeText(applicationContext, "registerPushTokenForCurrentUser failed",Toast.LENGTH_LONG).show()

                                                e.printStackTrace()
                                                return@RegisterPushTokenWithStatusHandler
                                            }

                                            if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                                                //Toast.makeText(applicationContext, "Connection required to register push token.", Toast.LENGTH_LONG).show()

                                                Log.d(LoginActivity::class.java.simpleName, "Connection required to register push token.")
                                            } else {
                                                Log.d(LoginActivity::class.java.simpleName, "registerPushTokenForCurrentUser success.")
                                            }
                                        })


                                    PreferenceUtils.setConnected(true)
                                    // Update the user's nickname
                                    /*SendBird.updateCurrentUserInfo(chatId.text.toString(), null,
                                                    SendBird.UserInfoUpdateHandler { e ->
                                                        if (e != null) {
                                                            e.printStackTrace()
                                                            // Error!
                                                            Snackbar.make(window.decorView, "Update user nickname failed", Snackbar.LENGTH_LONG).show();

                                                            return@UserInfoUpdateHandler
                                                        }

                                                        PreferenceUtils.setUserId(chatId.text.toString())
                                                        //PreferenceUtils.setNickname(chatId.text.toString())
                                                    })*/


                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                    finish()
                                    //    }
                                    //})
                                }
                            })


                        }

                    }
                }
            }
        }
    }

    private fun chatLogin(chatId: String, listener: (accessToken : String) -> Unit){
        progress_bar_login.visibility = View.VISIBLE
        disposable.add(chatViewModel.login(chatId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"chatLogin : success")

                    if(listener != null){
                        listener.invoke(it.ACCESS_TOKEN!!)
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

    private fun getSkdid(type: String, listener: (didNonce: String, didSvcPublic: String) -> Unit){

        disposable.add(loginViewModel.getSkdid(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    Log.d(LoginActivity::class.java.simpleName,"resultCertify : success")

                    if(listener != null){
                        listener.invoke(it.DID_NONCE!!, it.DID_SVC_PUBLIC!!)
                    }
                }else{
                    ViewUtils.alertDialog(this, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))

    }

    private fun resultCertify(opCode: String, signData: String, snData: String, listener: (securityNum: String?, dn: String, signature: String, publicKey: String, name: String) -> Unit){
        disposable.add(loginViewModel.resultCertOpenPass(opCode, signData, snData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    Log.d(LoginActivity::class.java.simpleName,"resultCertify : success")

                    if(listener != null){
                        listener.invoke(it.SECURITY_NUM, it.DN!!, it.SIGNATURE!!, it.PUBLIC_KEY!!, it.NAME!!)
                    }
                }else{
                    ViewUtils.alertDialog(this, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun loginCertify(gubn: String, certified: String, listener: (userId: String) -> Unit){
        disposable.add(loginViewModel.login(gubn, certified)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({


                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"loginCertify : success")

                    PreferenceUtils.setUserId(it.CLNT_NO!!)
                    PreferenceUtils.setUserName(it.CNLT_NM)
                    Preference.setServerToken(this@LoginActivity, it.MOBILE_TOCKEN!!)

                    if(listener != null){
                        listener.invoke(it.CLNT_NO!!)
                    }
                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                    var intent = Intent(this, WebActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("title", "인증정보 갱신")
                    intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/renew/invstSvJoinRenew?AUTH_TYPE="+PreferenceUtils.getLoginType())
                    startActivity(intent)
                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun loginCertify(gubn: String, certified: String, signData: String, opCode: String, listener: (userId: String) -> Unit){
        disposable.add(loginViewModel.login(gubn, certified,signData,opCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({


                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"loginCertify : success")

                    PreferenceUtils.setUserId(it.CLNT_NO!!)
                    PreferenceUtils.setUserName(it.CNLT_NM)
                    Preference.setServerToken(this@LoginActivity, it.MOBILE_TOCKEN!!)

                    if(listener != null){
                        listener.invoke(it.CLNT_NO!!)
                    }
                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                    var intent = Intent(this, WebActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("title", "인증정보 갱신")
                    intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/renew/invstSvJoinRenew?AUTH_TYPE="+PreferenceUtils.getLoginType())
                    startActivity(intent)
                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun registToken(userId: String, deviceToken: String, listener: Runnable?){
        progress_bar_login.visibility = View.VISIBLE
        disposable.add(chatViewModel.registPushToken(userId, deviceToken, Preference.getPushYn(this) == "Y", Preference.getAdYn(this) == "Y")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"registPushToken : success")
                }

                if(listener != null){
                    listener.run()
                }

                progress_bar_login.visibility = View.INVISIBLE
            }, {
                it.printStackTrace()

                if(listener != null){
                    listener.run()
                }
            }))
    }

    private fun sendPush(userId: String, listener: Runnable?){
        disposable.add(chatViewModel.sendPush(userId, "03", "test")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"send push : success")

                    if(listener != null){
                        listener.run()
                    }
                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun initToken(){
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            Log.d(LoginActivity::class.java.simpleName,"firebase token : " + instanceIdResult.token)

            token.setText(instanceIdResult.token)
        }
    }

    override fun onActivityResult(aRequestCode: Int, aResultCode: Int, data: Intent?) {
        super.onActivityResult(aRequestCode, aResultCode, data)

        /*--------------------------------------------------------------------------------------*
		 * XecureKeypad가 종료되면 Result 값을 받아 처리해야함.
		 * 	- RequestCode는 XKConstants.XKKeypadRequestCode로 설정.
		 *--------------------------------------------------------------------------------------*/
        if (aRequestCode == XKConstants.XKKeypadRequestCode && aResultCode == Activity.RESULT_OK) {
            password.text = password_chatId.data
        }
    }

    private fun showProgressDialog(mTitle: String, mMessage: String){

        if(mainProgress == null){
            mainProgress = ViewUtils.showProgressDialog(this, mTitle, mMessage, true)
        }
        else{
            mainProgress!!.title?.setText(mTitle)
            mainProgress!!.message?.setText(mMessage)
            mainProgress!!.show()
        }

        if(mTitle.isNullOrEmpty()){
            mainProgress!!.title?.visibility = View.GONE
        }
    }


    private fun proceedNextStep() {
        mCurrentStep += 1
        mHandler.sendEmptyMessage(mCurrentStep)
    }

    /**
     * [Step 2] (OpenPass 화면 비전환) OpenPass 연등을 위한 초기화.<br></br>
     * (이 단계는 필수 사항입니다.)<br></br><br></br>
     * OpenPass 연동을 위한 채널 생성 등의 초기화 작업을 진항합니다.<br></br>
     * [SKCertManager.initOpenPass] 함수를 사용합니다.<br></br>
     * OpenPass가 설치되어 있지 않다면 설치 의사를 묻는 팝업을 표시합니다.
     */
    private fun initializeOpenPass() {
        showProgressDialog("", "초기화 중입니다.")

        // 초기화 함수 호출.
        val nResult = SKCertManager.initOpenPass(this, OPENPASS_LICENSE, OPENPASS_LAUNCHMODE,
            SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->

            mainProgress?.dismiss()

            if (resultCode == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                resultCode == SKConstant.RESULT_CODE_ERROR_APP_DISABLED ||
                resultCode == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE){
                // OpenPass 설치 등 상태 관련 오류
                SKCertManager.showErrorPopup(resultCode)
            }
            else if (resultCode == SKConstant.RESULT_CODE_OK){
                // OpenPass 초기화 성공
                proceedNextStep()
            }
            else {
                // OpenPass 초기화 실패
                Toast.makeText(applicationContext, resultMessage, Toast.LENGTH_LONG).show()
                mainProgress?.dismiss()
            }
        })

        if (SKConstant.RESULT_CODE_OK != nResult) {
            Toast.makeText(applicationContext, "MyPass 초기화 중 오류가 발생하였습니다.($nResult)", Toast.LENGTH_SHORT)
                .show()
            mainProgress?.dismiss()

            if (nResult == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                nResult == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE ||
                nResult == SKConstant.RESULT_CODE_ERROR_APP_DISABLED
            ) {
                SKCertManager.showErrorPopup(nResult)
            } else
                finish()
        }
    }

    /**
     * [Step 1] (OpenPass 화면 비전환) 기기에 설치된 OpenPass의 버전을 확인.<br></br>
     * (이 단계는 옵션 사항입니다.)<br></br><br></br>
     * [SKCertManager.getOpenPassVersion] 함수를 사용합니다.<br></br>
     * OpenPass의 버전을 확인하고 아직 설치가 되어 있지 않다면 설치 의사를 묻는 팝업을 표시합니다.
     */
    private fun checkOpenPassVersion() {
        showProgressDialog("", "MyPass 버전을 확인합니다.")

        SKCertManager.getOpenPassVersion(this
        , SKCallback.MessageCallback{ requestCode, resultCode, resultMessage ->

            mainProgress?.dismiss()

            if (resultCode == SKConstant.RESULT_CODE_OK) {
                Toast.makeText(applicationContext, "설치된 MyPass의 버전은 $resultMessage 입니다.", Toast.LENGTH_LONG).show()
                proceedNextStep()
            } else if ((resultCode == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                        resultCode == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE ||
                        resultCode == SKConstant.RESULT_CODE_ERROR_APP_DISABLED)) {
                mainProgress?.dismiss()
                SKCertManager.showErrorPopup(resultCode)
            }
        })
    }

    private fun requestSign() {

        try
        {
            showProgressDialog("", "인증을 요청합니다.")


            SKCertManager.sign(SKConstant.REQUEST_CODE_KOSCOM_FULL_SIGN, null, "ServerRandom".toByteArray(), SKConstant.AUTH_TYPE_ALL, true,
                ComUtil.policyMode , null,
                SKCallback.SignCallback { requestCode, resultCode, resultMessage, binSignData, b64Cert, isTrustZone ->

                    mainProgress?.dismiss()

                    if (resultCode == SKConstant.RESULT_CODE_OK) {

                        //Log.v("OpenPassClient", "Sign result(hex) : " + SKUtil.bin2hex(binSignData))
                        //Log.v("OpenPassClient", "signData : " + signData)
                        // 샘플 앱에서는 서명 검증 구현이 생략되어 있습니다.
                        // 서명 검증은 서버 모듈의 기능을 참조하여 주십시오.
                        Log.d(LoginActivity::class.simpleName, binSignData)

                        val certInfo = SKSystemCertInfo(SKUtil.b642bin(b64Cert))
                        //val certInfo = binSignData
                        Log.d(
                            LoginActivity::class.simpleName,
                            "certInfo.issuerDN : " + certInfo.issuerDN
                        )
                        Log.d(
                            LoginActivity::class.simpleName,
                            "certInfo.b64PublicKeyInfo : " + certInfo.b64PublicKeyInfo
                        )
                        Log.d(
                            LoginActivity::class.simpleName,
                            "certInfo.subjectDN : " + certInfo.subjectDN
                        )
                        Log.d(
                            LoginActivity::class.simpleName,
                            "certInfo.serialNumber : " + certInfo.serialNumber
                        )
                        Log.d(
                            LoginActivity::class.simpleName,
                            "certInfo.certPolicyIdString : " + certInfo.certPolicyIdString
                        )

                        // 서명 검증, 비식별 아이디 전달 완료 후 로그인 완료 페이지로 이동
                        val strSubjectDN = if ((certInfo == null)) "" else certInfo.subjectDN

                        Log.d(
                            LoginActivity::class.simpleName,
                            "certInfo.subjectDN : " + strSubjectDN
                        )

                        login("1", "", binSignData, "1")

                        /* 2019.12.19
                        resultCertify("1", signData, "", { securityNum: String?, dn: String, signature: String, publicKey: String, name: String ->

                            login("1", dn)
                        })
                         */

                    } else if (resultCode == SKConstant.RESULT_CODE_ERROR_SESSION_EXPIRED) {
                        Log.e(LoginActivity::class.simpleName, "RESULT_CODE_ERROR_SESSION_EXPIRED")
                    } else {
                        // 에러 처리
                        // 만들어진 단축서명용 전자서명 생성키 삭제
                        // SKCertManager.clear();

                        Toast.makeText(
                            getApplicationContext(),
                            "[SKCertManager.sign()] ERR($resultCode\n$resultMessage)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
        catch (e:Exception) {
            e.printStackTrace()
        }

    }

    internal var mHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            _STEP_INITIALIZE -> initializeOpenPass()

            _STEP_CHECK_VERSION -> checkOpenPassVersion()

            _STEP_SEND_CUSTOM_UI_RESOURCES -> requestSign()
        }
        false
    })

    override fun onDestroy() {
        super.onDestroy()

        //---- 4/1 금융결제원 요청, 추가 부분
        if (mainProgress != null) {
            mainProgress!!.cancel()
            mainProgress!!.dismiss()
        }

        disposable.clear()
    }

    companion object{

        var loginActivity: LoginActivity? = null

        val OPENPASS_LAUNCHMODE = SKCertManager.LAUNCH_MODE_REAL
        val OPENPASS_LICENSE =
            "{\"licenseSign\":\"QHbig+ViXPOoxJBuiaZ4PgO6b7pNgwsJP9eci2aAMmFXG1FWG0WSqGezukAP6WDgdeqHCBtbGrr1C3mQ+a/xYvm5UaQfmA+fFKV/f97N5CUu1AceM/vtpDhBZSeIN9nSCpIgH+JaV/qNRUORKAjLENXIM7hlFAeWYbCFCklIe07ScPzK4NxEqa+jRnV3JuOnFmgxcnm1xrgTXnl5sSuUsnbEDzVZMbBBOdvvBu2moWxDWirkUEWqcTgds7YEevRJQqr5NTL33BuNod395n6gCiUPZxeMiY/VAj0cvNi7+vVdW2B+h/TuNd8YUK4ULQgpZSbk57Wi/xNtJiXx107vPQ==\",\"licenseInfo\":\"eyJDb21wYW55TmFtZSI6Iuy9lOyKpOy9pOu4lOufreyytOyduCIsIkxpY2Vuc2VWZXJzaW9uIjoiMS4wIiwiQ29tcGFueUlEIjoiMDAwMDEiLCJQbGF0Zm9ybSI6IjEiLCJBcHBJRCI6ImtyLmNvLmtvc2NvbS5vbXAiLCJBcHBOYW1lIjoiQmVNeVVuaWNvcm4ifQ==\"}"

    }
}
