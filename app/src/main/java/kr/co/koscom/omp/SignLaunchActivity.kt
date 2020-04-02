package kr.co.koscom.omp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.sendbird.syncmanager.utils.ComUtil
import com.signkorea.openpass.interfacelib.SKCallback
import com.signkorea.openpass.interfacelib.SKCertManager
import com.signkorea.openpass.interfacelib.SKConstant
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_order_detail.toolbar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_sign_launch.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.view.MyDialog
import kr.co.koscom.omp.view.ViewUtils

/**
 * 통합인증 간편로그인 등록 및 변경
 */

class SignLaunchActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    var mainProgress: MyDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_launch)

        viewModelFactory = Injection.provideViewModelFactory(this)

        btnClose.setOnClickListener {
            finish()
        }

        val test : String? = ""

        btnFingerRegist.setOnClickListener {
            SKCertManager.callOpenPassPage(120, ComUtil.policyMode,null, { i: Int, i1: Int, s: String ->
            })
        }
        btnPinRegist.setOnClickListener {
            SKCertManager.callOpenPassPage(117,ComUtil.policyMode,null, { i: Int, i1: Int, s: String ->
            })
        }
        btnPinChange.setOnClickListener {
            SKCertManager.callOpenPassPage(118,ComUtil.policyMode,null, { i: Int, i1: Int, s: String ->
            })
        }
        btnPatternRegist.setOnClickListener {
            SKCertManager.callOpenPassPage(141,ComUtil.policyMode,null, { i: Int, i1: Int, s: String ->
            })
        }
        btnPatternChange.setOnClickListener {
            SKCertManager.callOpenPassPage(142,ComUtil.policyMode,null, { i: Int, i1: Int, s: String ->
            })
        }

        initializeOpenPass()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
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

    private fun initializeOpenPass() {
        showProgressDialog("", "초기화 중입니다.")

        // 초기화 함수 호출.
        val nResult = SKCertManager.initOpenPass(this,
            LoginActivity.OPENPASS_LICENSE,
            LoginActivity.OPENPASS_LAUNCHMODE,
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

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    companion object {
        val REQUEST_CODE = 1000
    }
}
