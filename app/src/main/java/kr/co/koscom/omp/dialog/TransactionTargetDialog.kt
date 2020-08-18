package kr.co.koscom.omp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_transaction_target.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.enums.TransactionTargetType

class TransactionTargetDialog(context: Context): Dialog(context, R.style.DialogFadeAnim) {
    private var positiveListener: ((TransactionTargetType) -> Unit)? = null


    private var currentTarget = TransactionTargetType.SPECIFIC


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_transaction_target)

        initListener()
        layoutUnSpecificTransaction.callOnClick()
    }

    private fun initListener(){
        ivClose.setOnClickListener {
            dismiss()
            positiveListener?.let {
                it.invoke(TransactionTargetType.NONE)
            }
        }

        tvConfirm.setOnClickListener {
            dismiss()
            positiveListener?.let {
                it.invoke(currentTarget)
            }
        }

        layoutSpecificTransaction.setOnClickListener {
            layoutSpecificTransaction.isSelected = true
            layoutUnSpecificTransaction.isSelected = false
            currentTarget = TransactionTargetType.SPECIFIC
        }

        layoutUnSpecificTransaction.setOnClickListener {
            layoutSpecificTransaction.isSelected = false
            layoutUnSpecificTransaction.isSelected = true
            currentTarget = TransactionTargetType.UNSPECIFIC
        }
    }


    class Builder(context: Context){
        val dialog: TransactionTargetDialog = TransactionTargetDialog(context)

        fun setPositiveListener(l: (TransactionTargetType) -> Unit): Builder{
            dialog.positiveListener = l
            return this
        }
        fun show(): TransactionTargetDialog{
            dialog.show()
            return dialog
        }
    }


}