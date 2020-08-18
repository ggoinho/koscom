package kr.co.koscom.omp.custom

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_custom_nego_status.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.enums.NegoStatusType
import kr.co.koscom.omp.extension.toDrawable
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toVisible
import java.io.UnsupportedEncodingException

class CustomNegoStatusView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {


    var negoStatusType = NegoStatusType.NONE
    var isClickableBtn: Boolean = true

    init {
        initialize()
        setNegoStatus(negoStatusType.type)

    }

    private fun initialize() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_custom_nego_status, this, true)
    }

    /**
     * NEGO_WAIT("100"),                //협상대기
        BREAKDOWN("105"),                //결렬
        AUTO_BREAKDOWN("106"),           //자동결렬
        REQUEST_CANCEL("107"),           //요청취소
        CONTRACT_CANCEL("207"),          //계약취소
        COMPLETE("406");                 //완료
     */
    fun setNegoStatus(negoStatusCode: String, dealType: String? = "", rmoty: String? = ""){

        when(NegoStatusType.getType(negoStatusCode)){
            NegoStatusType.NONE->{
                allInvisibleStatus()
            }
            NegoStatusType.NEGO_WAIT ->{
                //협상대기
                if(dealType == DealType.SELL.type){
                    //매도
                    if(isClickableBtn){
                        tvNegoSellWait.background = R.drawable.rectangle_white_707fc6.toDrawable()
                    }else{
                        tvNegoSellWait.background = null
                    }
                    tvNegoSellWait.toVisible()
                }else{
                    //매수
                    if(isClickableBtn){
                        tvNegoBuyWait.background = R.drawable.rectangle_white_ef508b.toDrawable()
                    }else{
                        tvNegoBuyWait.background = null
                    }
                    tvNegoBuyWait.toVisible()
                }
            }
            NegoStatusType.BREAKDOWN ->{
                //결렬
                if(isClickableBtn){
                    tvStatusBreakdown.background = R.drawable.rectangle_white_ccccc3.toDrawable()
                }else{
                    tvStatusBreakdown.background = null
                }
                tvStatusBreakdown.toVisible()
            }
            NegoStatusType.AUTO_BREAKDOWN ->{
                //자동결렬
                if(isClickableBtn){
                    tvStatusAutoBreakdown.background = R.drawable.rectangle_white_ccccc3.toDrawable()
                }else{
                    tvStatusAutoBreakdown.background = null
                }
                tvStatusAutoBreakdown.toVisible()
            }
            NegoStatusType.REQUEST_CANCEL ->{
                //요청취소
                if(isClickableBtn){
                    tvStatusRequestCancel.background = R.drawable.rectangle_white_ccccc3.toDrawable()
                }else{
                    tvStatusRequestCancel.background = null
                }
                tvStatusRequestCancel.toVisible()
            }
            NegoStatusType.CONTRACT_CANCEL ->{
                //계약취소
                if(isClickableBtn){
                    tvStatusContractCancel.background = R.drawable.rectangle_white_ccccc3.toDrawable()
                }else{
                    tvStatusContractCancel.background = null
                }
                tvStatusContractCancel.toVisible()
            }
            NegoStatusType.COMPLETE ->{
                //완료
                if(isClickableBtn){
                    tvStatusComplete.background = R.drawable.rectangle_white_ccccc3.toDrawable()
                }else{
                    tvStatusComplete.background = null
                }
                tvStatusComplete.toVisible()
            }
            else ->{
                //협상
                if(rmoty == "0"){
                    if(isClickableBtn){
                        tvNegoStatus3.background = R.drawable.rectangle_f2f2f2_dddddd.toDrawable()
                    }else{
                        tvNegoStatus3.background = null
                    }
                    tvNegoStatus3.toVisible()
                }else{
                    if(isClickableBtn){
                        tvNegoStatus4.background = R.drawable.rectangle_white_4aac9f.toDrawable()
                    }else{
                        tvNegoStatus4.background = null
                    }
                    tvNegoStatus4.toVisible()
                }
            }
        }

    }

    private fun allInvisibleStatus(){
        tvNegoSellWait.toInvisible()
        tvNegoBuyWait.toInvisible()
        tvNegoStatus3.toInvisible()
        tvNegoStatus4.toInvisible()
        tvContract.toInvisible()
        tvConclusion.toInvisible()
        tvStatusBreakdown.toInvisible()
        tvStatusAutoBreakdown.toInvisible()
        tvStatusRequestCancel.toInvisible()
        tvStatusContractCancel.toInvisible()
        tvStatusComplete.toInvisible()
    }


    /**************************************************
     * OnNegoStatusListener
     */
    interface OnNegoStatusListener {
        fun onActionSearch(v: CustomNegoStatusView)
    }

}
