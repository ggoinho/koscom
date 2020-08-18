package kr.co.koscom.omp.adapter.holder

import android.app.Activity
import android.view.ViewGroup
import com.sendbird.syncmanager.utils.PreferenceUtils
import isMe
import kotlinx.android.synthetic.main.item_mainlist.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.MainListAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.data.local.MainListData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.enums.*
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.util.ActivityUtil
import toDealType
import toDealTypeBackground

class OrderListViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.item_orderlist) {

//    private var onMainListClickListener: MainListAdapter.OnMainListClickListener? = null

    fun onBind(item: Any, position: Int, tabType: OrderListTab) {
        super.onBind(item, position)

//        this.onMainListClickListener = onMainListClickListener

        when(tabType){
            OrderListTab.ORDER_STATUS->{
                //주문현황
                if(item is Order.OrderItem){
                    item?.let {
                        setOrderItem(it)
                    }
                }

            }
            OrderListTab.CONCLUSION_STATUS->{
                //체결현황
                if(item is OrderContract.OrderContractItem){
                    item?.let {
                        setOrderItem(it)
                    }
                }

            }
        }
    }


    /**
     * 종목현황 데이터 셋팅
     */
    private fun setOrderItem(item: Order.OrderItem){
        itemView.tvStockName.text = item.CORP_HANGL_NM
        itemView.tvStockGubun.text = item.STOCK_TP_CODE_NM


        if (item.PUBLIC_YN.isY()){
            itemView.ivSecret.toGone()
            itemView.tvCount.text = item.DEAL_QTY?.toStringNumberFormat()
            itemView.tvPrice.text = item.DEAL_UPRC?.toStringNumberFormat()
        }else{
            itemView.ivSecret.toVisible()
            itemView.tvCount.text = R.string.star6.toResString()
            itemView.tvPrice.text = R.string.star6.toResString()
        }

        //매수, 매도
        itemView.tvDealType.text = item.DEAL_TP.toDealType()
        item.DEAL_TP.toDealTypeBackground(itemView.tvDealType)

        if(item.PBLS_CLNT_NO.isMe()){
            itemView.tvMy.toVisible()
        }else{
            itemView.tvMy.toGone()
        }

        allInvisibleStatus()

        when{
            item.POST_ORD_STAT_CODE == "0" && item.DEAL_TP == DealType.SELL.type ->{
                //매도 & 협상대기
                itemView.tvNegoSellWait.toVisible()
            }
            item.POST_ORD_STAT_CODE == "0" ->{
                //매수 & 협상대기
                itemView.tvNegoBuyWait.toVisible()
            }
            item.POST_ORD_STAT_CODE == "1" && item.RMQTY?:0 > 0 ->{
                //협상 초록색
                itemView.tvNegoStatus4.toVisible()
            }
            item.POST_ORD_STAT_CODE == "1" ->{
                //협상 회색
                itemView.tvNegoStatus3.toVisible()
            }
        }

        itemView.layoutItemContents.setOnClickListener {
            item?.let {
                ActivityUtil.startOrderDetailActivity(context as Activity, it)
            }
        }

        itemView.layoutNegoStatus.setOnClickListener {
            item?.let {
                ActivityUtil.startOrderPopupActivity(context as Activity, it)
            }
        }

    }

    /**
     * 체결현황 데이터 셋팅
     */
    private fun setOrderItem(item: OrderContract.OrderContractItem){
        itemView.tvStockName.text = item.CORP_HANGL_NM
        itemView.tvStockGubun.text = item.STOCK_TP_CODE_NM

        if (item.PUBLIC_YN.isY()){
            itemView.ivSecret.toGone()
            itemView.tvCount.text = item.DEAL_QTY?.toStringNumberFormat()
            itemView.tvPrice.text = item.DEAL_UPRC?.toStringNumberFormat()
        }else{
            itemView.ivSecret.toVisible()
            itemView.tvCount.text = R.string.star6.toResString()
            itemView.tvPrice.text = R.string.star6.toResString()
        }

        //매수, 매도
        itemView.tvDealType.text = item.DEAL_TP.toDealType()
        item.DEAL_TP.toDealTypeBackground(itemView.tvDealType)

        if(item.PBLS_CLNT_NO.isMe()){
            itemView.tvMy.toVisible()
        }else{
            itemView.tvMy.toGone()
        }

        allInvisibleStatus()

        /**
         * 체겷현황 리스트 상태
         */
        when (item.NEGO_SETT_STAT_CODE) {
            "204", "206" -> {
                //계약
                itemView.tvContract.toVisible()
            }
            "400", "405", "406" -> {
                //체결
                itemView.tvConclusion.toVisible()
            }
        }


        itemView.layoutItemContents.setOnClickListener {
            ActivityUtil.startContractPopupActivity(context as Activity, item)
        }
        itemView.layoutNegoStatus.setOnClickListener {
        }


    }

    fun allInvisibleStatus(){
        itemView.tvNegoSellWait.toInvisible()
        itemView.tvNegoBuyWait.toInvisible()
        itemView.tvNegoStatus3.toInvisible()
        itemView.tvNegoStatus4.toInvisible()
        itemView.tvContract.toInvisible()
        itemView.tvConclusion.toInvisible()
        itemView.tvStatusBreakdown.toInvisible()
        itemView.tvStatusAutoBreakdown.toInvisible()
        itemView.tvStatusRequestCancel.toInvisible()
        itemView.tvStatusContractCancel.toInvisible()
        itemView.tvStatusComplete.toInvisible()

    }


}