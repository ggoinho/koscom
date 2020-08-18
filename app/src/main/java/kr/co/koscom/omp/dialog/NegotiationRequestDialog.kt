package kr.co.koscom.omp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_negotiation_request.*
import kotlinx.android.synthetic.main.dialog_transaction_target.ivClose
import kotlinx.android.synthetic.main.dialog_transaction_target.tvConfirm
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.enums.TransactionTargetType
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toTextfromHtml
import kr.co.koscom.omp.extension.toVisible

class NegotiationRequestDialog(context: Context): Dialog(context, R.style.DialogFadeAnim) {
    private var positiveListener: ((String) -> Unit)? = null
    private var transactionTargetType: TransactionTargetType? = null
    private var enterType: EnterType? = null
    private var clientHanglNm: String = ""

    enum class EnterType{
        LIST,
        DETAIL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_negotiation_request)

        init()
        initListener()
    }

    private fun init(){

        when(enterType){
            EnterType.LIST->{
                tvTopContents1.text = R.string.negotiation_request_contents1_2.toResString()
            }
            EnterType.DETAIL->{
                tvTopContents1.text = R.string.negotiation_request_contents1_2.toResString()
//                tvTopContents1.toTextfromHtml(R.string.negotiation_request_contents1.toResString(), clientHanglNm?: "")
            }
        }

        when(transactionTargetType){
            TransactionTargetType.SPECIFIC ->{
                tvAgreeAfterPassword.toTextfromHtml(R.string.negotiation_request_agree_after_input.toResString())
                tvAgreeAfterPassword.toVisible()
                tvPasswordTitle.toVisible()
                etPassword.toVisible()
            }
            TransactionTargetType.UNSPECIFIC ->{
                tvAgreeAfterPassword.toGone()
                tvPasswordTitle.toGone()
                etPassword.toGone()
            }
        }
    }

    private fun initListener(){
        ivClose.setOnClickListener {
            dismiss()
        }

        tvConfirm.setOnClickListener {
            when{
                !layoutAgree.isSelected ->{
                    //이용 동의 X
                    tvAgreePlz.toVisible()

                }
                etPassword.text.isNullOrEmpty() && transactionTargetType == TransactionTargetType.SPECIFIC->{
                    //비밀번호 입력 X
                    tvPasswordInputPlz.toVisible()
                }
                else ->{
                    dismiss()
                    positiveListener?.let {
                        it.invoke(etPassword.text.toString())
                    }
                }
            }
        }

        layoutAgree.setOnClickListener {
            layoutAgree.isSelected = !layoutAgree.isSelected
        }

    }


    class Builder(context: Context){
        val dialog: NegotiationRequestDialog = NegotiationRequestDialog(context)

        fun setTransactionTargetType(transactionTargetType: TransactionTargetType): Builder{
            dialog.transactionTargetType = transactionTargetType
            return this
        }

        fun setClientHanglNm(clientHanglNm: String): Builder{
            dialog.clientHanglNm = clientHanglNm
            return this
        }


        fun setEnterType(type: EnterType): Builder{
            dialog.enterType = type
            return this
        }

        fun setPositiveListener(l: (String) -> Unit): Builder{
            dialog.positiveListener = l
            return this
        }
        fun show(): NegotiationRequestDialog{
            dialog.show()
            return dialog
        }
    }


}