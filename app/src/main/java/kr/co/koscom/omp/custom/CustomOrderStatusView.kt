package kr.co.koscom.omp.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_custom_nego_status.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.enums.OrderStatusType
import kr.co.koscom.omp.extension.toDrawable
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toVisible

class CustomOrderStatusView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {


    var orderStatusType = OrderStatusType.NONE
    var isClickableBtn: Boolean = true

    init {
        initialize()
        setOrderStatus(orderStatusType)

    }

    private fun initialize() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_custom_order_status, this, true)
    }

    /**
     * NONE: 기본상태
     * WAIT_SELL_NEGO: 협상대기 & 매도
     * WAIT_BUY_NEGO: 협상대기 & 매수
     * NEGOTIATING: 협상진행중
     * NEGOTIATING_NO_REMAIN: 협상진행중 & 잔량 없음
     */
    fun setOrderStatus(orderType: OrderStatusType){

        allInvisibleStatus()
        when(orderType){
            OrderStatusType.NONE->{

            }
            OrderStatusType.WAIT_SELL_NEGO ->{
                //협상대기 & 매도
                if(isClickableBtn){
                    tvNegoSellWait.background = R.drawable.rectangle_white_707fc6.toDrawable()
                }else{
                    tvNegoSellWait.background = null
                }
                tvNegoSellWait.toVisible()
            }
            OrderStatusType.WAIT_BUY_NEGO ->{
                //협상대기 & 매수
                if(isClickableBtn){
                    tvNegoBuyWait.background = R.drawable.rectangle_white_ef508b.toDrawable()
                }else{
                    tvNegoBuyWait.background = null
                }
                tvNegoBuyWait.toVisible()
            }
            OrderStatusType.NEGOTIATING ->{
                //협상진행중
                if(isClickableBtn){
                    tvNegoStatus4.background = R.drawable.rectangle_white_4aac9f.toDrawable()
                }else{
                    tvNegoStatus4.background = null
                }
                tvNegoStatus4.toVisible()
            }
            OrderStatusType.NEGOTIATING_NO_REMAIN ->{
                //협상진행중 & 잔량 없음
                if(isClickableBtn){
                    tvNegoStatus3.background = R.drawable.rectangle_f2f2f2_dddddd.toDrawable()
                }else{
                    tvNegoStatus3.background = null
                }
                tvNegoStatus3.toVisible()
            }
        }
    }

    private fun allInvisibleStatus(){
        tvNegoSellWait.toInvisible()
        tvNegoBuyWait.toInvisible()
        tvNegoStatus3.toInvisible()
        tvNegoStatus4.toInvisible()
    }


    /**************************************************
     * OnNegoStatusListener
     */
    interface OnNegoStatusListener {
        fun onAction(v: CustomOrderStatusView)
    }

}
