package kr.co.koscom.omp

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.lockincomp.liappagent.LiappAgent
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_intro.progress_bar_login
import kotlinx.android.synthetic.main.activity_intro.version
import kotlinx.android.synthetic.main.activity_login.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.viewmodel.UpdateViewModel
import kr.co.koscom.omp.view.ViewUtils
import kr.co.sdk.vguard2.*
import kr.co.vguard2.RealtimeScanningService
import kr.co.vguard2.TaskUpdate
import java.io.IOException
import java.util.concurrent.TimeoutException

/**
 * 스플래시 화면
 */

class SplashActivity : AppCompatActivity() {

    private val mHandler = Handler()
    private var progressBar: ProgressBar? = null

    val ESSENTIAL_PERMISSION_RESULTCODE = 7439

    private var confirmOverlayDialog: AlertDialog? = null
    private var OVERLAY_PERMIT_ACTIVITY_MOVED_EVER = false

    private var bIsStarted = false
    private var nRet = 0

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var updateViewModel: UpdateViewModel
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainContext = this

        confirmOverlayDialog = null
        OVERLAY_PERMIT_ACTIVITY_MOVED_EVER = false

        setContentView(R.layout.activity_intro)

        version.text = "V.${BuildConfig.VERSION_NAME}"

        viewModelFactory = Injection.provideViewModelFactory(this)
        updateViewModel = ViewModelProviders.of(this, viewModelFactory).get(UpdateViewModel::class.java)

        liappCheck {
            Log.d("SplashActivity", "liappCheck success.")

            parseDeepLink()

            showAnimation()
        }

    }

    private fun checkVersion(){
        disposable.add(updateViewModel.updateVersion("AOS")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_bar_login.visibility = View.INVISIBLE

                Log.d(SplashActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    val currentAppVersion = getVersionInfo(this@SplashActivity)
                    val currentServerVersion = it.datas?.resultList?.END_APP_VER

                    if (versionCompare(currentAppVersion,currentServerVersion)){
                        moveNextScreen()
                    } else {
                        if (it.datas?.resultList?.ARM_MSG_CD.equals("1")){
                            ViewUtils.alertDialog(this@SplashActivity,it.datas?.resultList?.ARM_MSG_TEXT!!) {
                                val appPackageName = packageName // getPackageName() from Context or Activity object
                                try {
                                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=$appPackageName")))
                                } catch (anfe: ActivityNotFoundException) {
                                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                                }

                                finish()
                            }
                        } else {
                            ViewUtils.alertUpdateDialog(this@SplashActivity,it.datas?.resultList?.ARM_MSG_TEXT!!,{moveNextScreen()},{
                                val appPackageName = packageName // getPackageName() from Context or Activity object
                                try {
                                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=$appPackageName")))
                                } catch (anfe: ActivityNotFoundException) {
                                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                                }

                                finish()
                            })
                        }

                    }

                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }

            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun getVersionInfo(context: Context): String? {
        var version: String? = null
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return version
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

    private fun liappCheck(listener: () -> Unit){
        if(bIsStarted == false) {
            nRet = LiappAgent.LA1()
        } else {
            nRet = LiappAgent.LA2()
        }

        if (LiappAgent.LIAPP_SUCCESS == nRet) {
            bIsStarted = true
            var userId = "userid_123456789" + System.currentTimeMillis()
            Log.i("SplashActivity", "Liapp AuthKey : " + LiappAgent.GA(userId))

            listener.invoke()
        }
        else if(LiappAgent.LIAPP_EXCEPTION == nRet)
        {
            //Exception. Bypass or Network Connected Error.
            Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()

            listener.invoke()
        }
        else
        {
            if (LiappAgent.LIAPP_DETECTED == nRet) {
                // DETECTED USER BLOCK or ANTI DEBUGGING or ANTI TAMPER
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else if (LiappAgent.LIAPP_DETECTED_ROOTING == nRet) {
                // Rooting Detection!
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else if (LiappAgent.LIAPP_DETECTED_VM == nRet) {
                // Virtual Machine Detection!
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else if (LiappAgent.LIAPP_DETECTED_HACKING_TOOL == nRet) {
                // Hacking Tool Detection!
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else {
                // unknown Error
            }
            System.exit(0)
        }
    }

    override fun onRestart() {
        super.onRestart()

        if(bIsStarted == false) {
            nRet = LiappAgent.LA1()
        } else {
            nRet = LiappAgent.LA2()
        }
        if (LiappAgent.LIAPP_SUCCESS == nRet) {
            // Success
            bIsStarted = true
        }
        else if(LiappAgent.LIAPP_EXCEPTION == nRet)
        {
            //Exception. Bypass or Error
            Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
        }
        else
        {
            if (LiappAgent.LIAPP_DETECTED == nRet) {
                // DETECTED USER BLOCK or ANTI DEBUGGING or ANTI TAMPER
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else if (LiappAgent.LIAPP_DETECTED_ROOTING == nRet) {
                // Rooting Detection!
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else if (LiappAgent.LIAPP_DETECTED_VM == nRet) {
                // Virtual Machine Detection!
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else if (LiappAgent.LIAPP_DETECTED_HACKING_TOOL == nRet) {
                // Hacking Tool Detection!
                Toast.makeText(applicationContext, LiappAgent.GetMessage(), Toast.LENGTH_LONG).show()
            } else {
                // unknown Error
            }
            System.exit(0)
        }
    }

    private fun parseDeepLink(){
        val uri = intent.data
        if(uri != null){
            DeepLinker.parse(uri)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ESSENTIAL_PERMISSION_RESULTCODE -> {

                var granted = true

                for(grantResult in grantResults){
                    if(grantResult != PackageManager.PERMISSION_GRANTED){
                        granted = false
                        break
                    }
                }

                if(granted){
                    if (Build.VERSION.SDK_INT >= 29) {
                        // Android 10 이상인 경우, "다른 앱 위에 표시" 권한 설정 허용 체크 후 권한 요청 또는 V-Guard 인스턴스 생성 코드 실행.
                        checkOverlayPermission(getApplicationContext())
                    } else {
                        // Android 10 미만인 경우, 기존대로 V-Guard 인스턴스 생성 코드 실행.
                        createVGuardInstance(getApplicationContext())
                    }
                }
                else{
                    // 사용자로부터 권한요청을 거부당했을 경우, 인스턴스 생성불가 사유 메시지 출력과 함께 앱을 종료 처리.
                    val errMsg = "구동에 필수로 필요한 권한이 허용되지 않아 앱을 종료 합니다."
                    Toast.makeText(applicationContext, errMsg, Toast.LENGTH_SHORT).show()
                    finish() // 앱 종료.
                }

            }
        }
    }

    private fun vguard(){
        if (Build.VERSION.SDK_INT < 26) {
            // V-Guard 인스턴스 생성 코드 실행.
            createVGuardInstance(this)
        } else {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 사용자로부터 권한이 허용되어 있지 않다면, 권한허용을 요청.
                requestPermissions(
                    arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    ESSENTIAL_PERMISSION_RESULTCODE
                )
            } else {
                // 사용자로부터 권한이 허용이 되어진 경우라면, V-Guard 인스턴스 생성 코드 실행.
                if (Build.VERSION.SDK_INT >= 29) {
                    // Android 10 이상인 경우, "다른 앱 위에 표시" 권한 설정 허용 체크 후 권한 요청 또는 V-Guard 인스턴스 생성 코드 실행.
                    checkOverlayPermission(getApplicationContext())
                } else {
                    // Android 10 미만인 경우, 기존대로 V-Guard 인스턴스 생성 코드 실행.
                    createVGuardInstance(getApplicationContext())
                }
            }
        }
    }

    fun createVGuardInstance(context: Context) {

        //VGuard의 인스턴스를 생성합니다.
        //VGuard의 멤버변수에 접근하려면 시작하는 부분에서 createInstance를 해야 합니다.
        try {
            VGuard.createInstance(applicationContext)
        } catch (e: NullPointerException) {
            // CreateInstance 호출 시, 전달되는 파라메터인 context정보가 null인경우, NullPointerException 발생.
        } catch (e: TimeoutException) {
            // CreateInstance 호출 시, 인터넷이 불안정 하여 V-Guard2.0의 엔진파일의 무결성 검사가 일정시간 내에 이뤄질 수 없는 경우, TimeoutException 발생.
        } catch (e: IOException) {
            // CreateInstance 호출 시, 앱 내에 위치한 V-Guard2.0 패턴파일에 이상이 생겨 복원을 진행하던 중 에러가 발생한 경우, IOException 발생.
        } catch (e: EssentialPermissionException) {
            // Android 8.0이상 단말에서, 필수 권한인 전화권한("READ_PHONE_STATE")이 허용되지 않은 상태에서 createInstance를 호출하는 경우, EssentialPermissionException발생.
        } catch (e: IntegrityCheckException) {
            // CreateInstance 호출 시, V-Guard인스턴스 생성 중 무결성 검사 시점에서 클라이언트의 네트워크상태가 폐쇄망 또는 문제가 있는 상황이거나, 무결성체크 서버와의 연결 상의 문제로 무결성 검사를 진행할 수 없을 때 발생하는 Exception
        } catch (e: ConnectivityException) {
            // CreateInstance 호출 시, 인터넷 연결이 되어 있지 않은 경우, ConnectivityException 발생.
        } catch (e: LicenseOverException) {
            // 사용 중인 라이선스에 허용된 앱 갯수를 초과하는 경우, LicenseOverException 발생.
        } catch (e: LicenseExpireException) {
            // 사용 중인 라이선스가 만료되었을 경우, LicenseExpireException 발생.
        }

        //createInstance 시, 생성된 인스턴스가 null일 경우, 다시 생성
        if (VGuard.getInstance() == null) {

            var errMsg = ""
            try {
                VGuard.createInstance(applicationContext)
                //VGuard.createInstance(null);
            } catch (e: NullPointerException) {
                // CreateInstance 호출 시, 전달되는 파라메터인 context정보가 null인경우, NullPointerException 발생.
                errMsg = "context정보가 null로 전달되어 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            } catch (e: TimeoutException) {
                // CreateInstance 호출 시, 인터넷이 불안정 하여 V-Guard2.0의 엔진파일의 무결성 검사가 일정시간 내에 이뤄질 수 없는 경우, TimeoutException 발생.
                errMsg =
                    "통신상태의 불안정으로 V-Guard엔진의 무결성검사의 시간제한을 초과하여 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            } catch (e: IOException) {
                // CreateInstance 호출 시, 앱 내에 위치한 V-Guard2.0 패턴파일에 이상이 생겨 복원을 진행하던 중 에러가 발생한 경우, IOException 발생.
                errMsg = "패턴파일 복원과정에서 오류가 발생하여 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            } catch (e: EssentialPermissionException) {
                // Android 8.0이상 단말에서, 필수 권한인 전화권한("READ_PHONE_STATE")이 허용되지 않은 상태에서 createInstance를 호출하는 경우, EssentialPermissionException발생.
                errMsg = "구동에 필수로 필요한 전화권한이 허용되지 않아 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            } catch (e: IntegrityCheckException) {
                // CreateInstance 호출 시, V-Guard인스턴스 생성 중 무결성 검사 시점에서 클라이언트의 네트워크상태가 폐쇄망 또는 문제가 있는 상황이거나, 무결성체크 서버와의 연결 상의 문제로 무결성 검사를 진행할 수 없을 때 발생하는 Exception
                errMsg = "통신상의 문제로 V-Guard엔진의 무결성검사 서버와 연결할 수 없어 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료합니다."
            } catch (e: ConnectivityException) {
                // CreateInstance 호출 시, 인터넷 연결이 되어 있지 않은 경우, ConnectivityException 발생.
                errMsg = "네트워크에 연결되어 있지 않아 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            } catch (e: LicenseOverException) {
                // 사용 중인 라이선스에 허용된 앱 갯수를 초과하는 경우, LicenseOverException 발생.
                errMsg = "라이선스에 허용된 앱 갯수를 초과하여 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            } catch (e: LicenseExpireException) {
                // 사용 중인 라이선스가 만료되었을 경우, LicenseExpireException 발생.
                errMsg = "라이선스가 만료되어 V-Guard인스턴스 생성에 실패하였습니다. 앱을 종료 합니다."
            }

            // CreateInstance 과정에서 Exception발생에 의하여 VGuard인스턴스가 생성이 되지 않은경우, 앱을 종료 합니다.
            if (VGuard.getInstance() == null) {
                Toast.makeText(applicationContext, errMsg, Toast.LENGTH_SHORT).show()
                finish() // 앱 종료.
            }
        }

        VGuard.isFirstRealscanEnded = false


        if (VGuard.getInstance() != null) {

            if (VGuard.getInstance().isRunningRealtimeScaningService(this@SplashActivity) == true) {
                Log.d(SplashActivity::class.simpleName, "is RunningRealtimeScaningService")

                startProgress()
            } else {
                Log.d(SplashActivity::class.simpleName, "is not RunningRealtimeScaningService!!")

                startVguard()
            }
        }
    }

    private fun startVguard(){

        mainProgress = ViewUtils.showProgressDialog(this, "바이러스 검사 중", "검사가 진행중입니다.", false)

        VGuard.NonUIScanActivityMode = true //------ NonUIScanActivity모드로 설정
        try {
            if (VGuard.getInstance().RootedCheck(0)) {
                val alert = android.app.AlertDialog.Builder(this)
                alert.setMessage("루팅폰 입니다")
                alert.setPositiveButton(
                    "확인"
                ) { dialog, which -> finish() }
                alert.show()
            } else {
                //버전 체크 및 업데이트, NonUIScanActivityMode
                val update_task = TaskUpdate(
                    this,
                    VGuard.UpdateType.BackgroundNonUI,
                    VGuard.SCAN_OPT_RUNNING
                ).execute() as TaskUpdate
            }
        } catch (e: Exception) {
            // 루팅검사에 문제가 발생한 경우, 샘플소스상에서는 앱 종료를 시행하며, 이후 동작은 고객사의 정책을 적용 필요.
            finish()
        }

    }

    fun checkOverlayPermission(context:Context) {
        if (Build.VERSION.SDK_INT >= 29)
        {
            if (!Settings.canDrawOverlays(context.applicationContext))
            {
                // Android 10이상의 OS이고, 사용자로부터 "다른 앱 위에 표시"권한이 허용되어 있지 않다면,
                // 권한허용을 요청하기 위한 AlertDialog 출력.
                confirmOverlayDialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("다른 앱 위에 그리기 권한 허용 필요")
                .setMessage(
                    "실시간 악성 앱 탐지 서비스의 정상적인 사용을 위해\n" +
                    "'다른 앱 위에 그리기' 권한의 허용이 필요합니다.\n" +
                    "권한설정 화면으로 이동하시겠습니까?"
                )
                .setPositiveButton("확인") { dialogInterface, i ->
                    val requestWindowPermission = Intent()
                    requestWindowPermission.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                    val strURIThisApp = "package:" + getPackageName()
                    requestWindowPermission.data = Uri.parse(strURIThisApp)
                    requestWindowPermission.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(requestWindowPermission)
                    // 사용자에게 '다른 앱 위에 그리기'권한 요청을 위한 화면을 출력을 시도하였으므로, 해당 변수의 값을 변경.
                    OVERLAY_PERMIT_ACTIVITY_MOVED_EVER = true
                }.setNegativeButton("취소") { dialogInterface, i ->
                    confirmOverlayDialog?.dismiss()
                    val msg = "구동에 필수로 필요한 '다른 앱 위에 표시' 권한이 허용되지 않아 V-Guard인스턴스 생성을 할 수 없습니다. 앱을 종료 합니다."
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    finish()
                }.create()
                confirmOverlayDialog?.show()

            }
            else
            {
                // Android 10이상의 OS이고, 사용자로부터 "다른 앱 위에 표시" 권한이 허용이 되어진 경우라면 인스턴스 생성 진행.
                createVGuardInstance(context.applicationContext)
            }
        }
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= 29 && OVERLAY_PERMIT_ACTIVITY_MOVED_EVER) {
            // 인스턴스 생성 또는 앱 종료 코드가 지속적으로 구동되지 않도록, 변수 값 변경.
            OVERLAY_PERMIT_ACTIVITY_MOVED_EVER = false
            // 사용자에게 '다른 앱 위에 그리기'의 권한요청을 하였으나, 그 권한을 승인받지 못한 경우.
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                val msg = "구동에 필수로 필요한 '다른 앱 위에 표시' 권한이 허용되지 않아 V-Guard인스턴스 생성을 할 수 없습니다. 앱을 종료 합니다."
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // 사용자에게 '다른 앱 위에 그리기'의 권한요청을 하였고, 그 권한을 승인받은 경우.
                createVGuardInstance(getApplicationContext())
            }
        }
        super.onResume()
    }

    private fun showAnimation() {
        try {

            logo1.post {
                arrow2.post {
                    centerLayout.pivotX = 0f
                    arrow1.pivotX = 0f
                    arrow2.pivotX = arrow2.width.toFloat()

                    var logo1TransXAnimator = ObjectAnimator.ofFloat(logo1, "translationX", -300f, 0f)
                    var logo1TransYAnimator = ObjectAnimator.ofFloat(logo1, "translationY", -400f, 0f)
                    var logo1ScaleXAnimator = ObjectAnimator.ofFloat(logo1, "scaleX", 7f, 1f)
                    var logo1ScaleYAnimator = ObjectAnimator.ofFloat(logo1, "scaleY", 7f, 1f)
                    var logo1RotateAnimator = ObjectAnimator.ofFloat(logo1, "rotation", 0f)
                    var logo1Animator = ObjectAnimator.ofFloat(logo1, "alpha", 0.2f, 0.3f, 1f)
                    var logo2Animator = ObjectAnimator.ofFloat(logo2, "alpha", 0.2f, 0.3f, 1f)
                    var centerLayoutAnimator = ObjectAnimator.ofFloat(centerLayout, "scaleX", 0f, 1f)
                    var arrow1Animator = ObjectAnimator.ofFloat(arrow1, "scaleX", 0f, 1f)
                    var arrow2Animator = ObjectAnimator.ofFloat(arrow2, "scaleX", 0f, 1f)
                    var logoAnimator = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)

                    var animatorSet = AnimatorSet()
                    animatorSet.interpolator = LinearInterpolator()
                    animatorSet.duration = 150
                    animatorSet.playTogether(logo1TransXAnimator, logo1TransYAnimator, logo1ScaleXAnimator, logo1ScaleYAnimator, logo1RotateAnimator, logo1Animator, logo2Animator)
                    animatorSet.addListener(object: Animator.AnimatorListener{
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            centerLayoutAnimator.interpolator = DecelerateInterpolator()
                            centerLayoutAnimator.duration = 300
                            centerLayoutAnimator.addListener(object: Animator.AnimatorListener{
                                override fun onAnimationRepeat(p0: Animator?) {
                                }

                                override fun onAnimationEnd(p0: Animator?) {
                                    var animatorSet = AnimatorSet()
                                    animatorSet.interpolator = AccelerateInterpolator()
                                    animatorSet.duration = 250
                                    animatorSet.playTogether(arrow1Animator, arrow2Animator, logoAnimator)
                                    animatorSet.addListener(object: Animator.AnimatorListener{
                                        override fun onAnimationRepeat(p0: Animator?) {
                                        }

                                        override fun onAnimationEnd(p0: Animator?) {
                                            vguard()

                                            //startProgress()
                                        }

                                        override fun onAnimationCancel(p0: Animator?) {
                                        }

                                        override fun onAnimationStart(p0: Animator?) {
                                            arrow1.visibility = View.VISIBLE
                                            arrow2.visibility = View.VISIBLE
                                            logo.visibility = View.VISIBLE
                                        }
                                    })
                                    mHandler.postDelayed({
                                        animatorSet.start()
                                    }, 200)
                                }

                                override fun onAnimationCancel(p0: Animator?) {
                                }

                                override fun onAnimationStart(p0: Animator?) {
                                    centerLayout.visibility = View.VISIBLE
                                }

                            })
                            mHandler.postDelayed({
                                centerLayoutAnimator.start()
                            }, 200)
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                            logo1.visibility = View.VISIBLE
                        }

                    })
                    animatorSet.start()


                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun startProgress(){

        Log.d("SplashActivity", "startProgress")
        progress.pivotX = 0f

        var progressAnimator = ObjectAnimator.ofFloat(progress, "scaleX", 0f, 1f)
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.duration = 1000
        progressAnimator.addUpdateListener {
            Log.d("SplashActivity", "addUpdateListener(${it.currentPlayTime}) duration : ${progressAnimator.duration}")
            if(it.currentPlayTime == 0L){
                progress.visibility = View.VISIBLE

                //XKCoreWrapperToJni.initializeLibrary(this)
            }

            if(it.currentPlayTime <= progressAnimator.duration){
                percent.text = ((it.currentPlayTime * 100) / progressAnimator.duration).toString() + "%"
            }

            if(it.currentPlayTime >= progressAnimator.duration){
                Log.d("SplashActivity", "addUpdateListener it.currentPlayTime >= progressAnimator.duration")

                if(Preference.getPushYn(this) != "Y"){

                    ViewUtils.alertReceiveDialog(this, {
                        //moveNextScreen()
                        checkVersion()
                    }, {
                        Preference.setPushYn(this, "Y")

                        Preference.setAdYn(this, "Y")

                        //moveNextScreen()
                        checkVersion()
                    })

                }
                else{
                    //moveNextScreen()
                    checkVersion()
                }
            }
        }
        progressAnimator.start()
    }

    private fun moveNextScreen(){
        if(!PreferenceUtils.getTutorialCheck()){
            var intent = Intent(this@SplashActivity, TutorialActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        else{
            var intent = Intent(this@SplashActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        finish()
    }

    fun NonUIProcessingResult(resultCode: Int, data: Intent) {

        if (mainProgress != null) {
            mainProgress!!.cancel()
            mainProgress!!.dismiss()
        }
        if (resultCode == Activity.RESULT_OK) {
            if (data.getStringExtra("list") == null || data.getStringExtra("list") == "") {
                if (VGuard.getInstance().isRunningRealtimeScaningService(this) != true) {
                    VGuard.getInstance().Checkup_CurrentStatus(1)
                    Toast.makeText(applicationContext, "실시간 감시가 켜졌습니다", Toast.LENGTH_SHORT).show()
                    Log.d("서비스 시작", "NONUI모드  서비스 시작")
                    VGuard.getInstance().startRealtimeScanningService(this, RealtimeScanningService::class.java)

                    //---------- 서비스 시작 이후 구동하고자 하는 코드는 아래에 입력 요망
                    startProgress()
                }
            }
        }
    }


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
        var mainContext: SplashActivity? = null
        var isProcessing = 0
        var mainProgress: Dialog? = null
    }
}
