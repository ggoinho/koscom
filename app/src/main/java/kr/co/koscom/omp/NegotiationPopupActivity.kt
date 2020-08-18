package kr.co.koscom.omp

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_negotiation_popup.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.NegotiationData
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.enums.NegotiationEnterType
import kr.co.koscom.omp.enums.TransactionTargetType
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.view.ViewUtils


/**
 * 비공개 주식 협상요청 팝업
 */

class NegotiationPopupActivity : AppCompatActivity() {


    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var chatViewModel: ChatViewModel

    private val disposable = CompositeDisposable()

    private var negotiationData: NegotiationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_negotiation_popup)

        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)

        negotiationData = intent.getSerializableExtra("negotiationData") as NegotiationData

        init()
        initListener()
    }

    private fun init(){


        when(negotiationData?.enterType){
            NegotiationEnterType.LIST->{
                tvTopContents1.text = R.string.negotiation_request_contents1_2.toResString()
            }
            NegotiationEnterType.DETAIL->{
                tvTopContents1.text = R.string.negotiation_request_contents1_2.toResString()
//                tvTopContents1.toTextfromHtml(R.string.negotiation_request_contents1.toResString(), negotiationData?.registNickName ?: "", negotiationData?.stockTpCodeNm ?: "")
            }
        }


        when(negotiationData?.transactionTargetType){
            TransactionTargetType.SPECIFIC ->{
                tvAgreeAfterPassword.toTextfromHtml(R.string.negotiation_request_agree_after_input.toResString())
                tvAgreeAfterPassword.toVisible()
                vDivide.toVisible()
                tvPasswordTitle.toVisible()
                etPassword.toVisible()
            }
            TransactionTargetType.UNSPECIFIC ->{
                tvAgreeAfterPassword.toGone()
                vDivide.toGone()
                tvPasswordTitle.toGone()
                etPassword.toGone()
            }
        }

    }

    private fun initListener(){

        ivClose.setOnClickListener {
            finish()
        }

        tvConfirm.setOnClickListener {
            when{
                !layoutAgree.isSelected ->{
                    //이용 동의 X
                    tvAgreePlz.toVisible()

                }
                etPassword.text.isNullOrEmpty() && negotiationData?.transactionTargetType == TransactionTargetType.SPECIFIC->{
                    //비밀번호 입력 X
                    tvAgreePlz.toInvisible()
                    tvPasswordInputPlz.toVisible()
                }
                else ->{
                    tvAgreePlz.toInvisible()
                    tvPasswordInputPlz.toInvisible()
                    requestNego(etPassword.text.toString())
                }
            }
        }

        layoutAgree.setOnClickListener {
            layoutAgree.isSelected = !layoutAgree.isSelected
        }
    }


    private fun requestNego(certiNum: String = ""){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(chatViewModel.requestNego(PreferenceUtils.getUserId(), negotiationData?.orderNo?: "", certiNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    ViewUtils.alertDialog(this, it.rMsg){
                        finish()
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

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    companion object {

    }
}
