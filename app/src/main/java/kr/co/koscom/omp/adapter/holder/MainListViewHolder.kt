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
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.enums.MainOrderTab
import kr.co.koscom.omp.enums.NegoStatusType
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.util.ActivityUtil
import toDealType
import toDealTypeBackground

class MainListViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.item_mainlist) {

    private var onMainListClickListener: MainListAdapter.OnMainListClickListener? = null

    fun onBind(item: Any, position: Int, onMainListClickListener: MainListAdapter.OnMainListClickListener?) {
        super.onBind(item, position)

        this.onMainListClickListener = onMainListClickListener

        if(item is MainListData){

            visibleListType(item.tabType)

            when(item.tabType){
                MainOrderTab.ORDER_STATUS->{
                    //주문현황
                    item.orderItem?.let {
                        setOrderItem(it)
                    }
                }
                MainOrderTab.NEGO_PROGRESS->{
                    //나의협상 진행상황
                    item.orderNego?.let {
                        setOrderItem(it)
                    }
                }
                MainOrderTab.CONCLUSION_STATUS->{
                    //체결현황
                    item.orderContractItem?.let {
                        setOrderItem(it)
                    }
                }
            }
        }
    }

    /**
     * 타입별로 보여줘야하는 뷰 처리
     */
    private fun visibleListType(tabType: MainOrderTab){
        when(tabType){
            MainOrderTab.ORDER_STATUS, MainOrderTab.CONCLUSION_STATUS->{
                //주문현황
                itemView.tvCount.toVisible()
                itemView.tvCountUnit.toVisible()
                itemView.tvPrice.toVisible()
                itemView.tvPriceUnit.toVisible()

                itemView.tvTime.toGone()
                itemView.tvRequestCancel.toGone()
                itemView.tvView.toGone()

                itemView.layoutBottomContents.toGone()

            }
            MainOrderTab.NEGO_PROGRESS->{
                //나의협상 진행상황
                itemView.tvCount.toGone()
                itemView.tvCountUnit.toGone()
                itemView.tvPrice.toGone()
                itemView.tvPriceUnit.toGone()

                itemView.tvTime.toVisible()
                itemView.tvRequestCancel.toVisible()
                itemView.tvView.toVisible()

                itemView.layoutBottomContents.toVisible()
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
        allClickableBtn(true)


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

        itemView.tvRequestCancel.setOnClickListener {
        }
        itemView.tvView.setOnClickListener {
        }
    }


    /**
     * 나의협상 진행상황 데이터 셋팅
     */
    private fun setOrderItem(item: OrderNego.OrderNegoItem){

        itemView.tvStockName.text = item.CORP_HANGL_NM
        itemView.tvStockGubun.text = item.STOCK_TP_CODE_NM

        if(!item.PUBLIC_YN.isY()){
            // 여긴 비공개주문임

            if(!item.PBLS_CLNT_NO.isMe()){
                // 여긴 비공개 남의주문임

                if((item.NEGO_SETT_STAT_CODE == "100") || (item.NEGO_SETT_STAT_CODE == "105") || (item.NEGO_SETT_STAT_CODE == "106") || (item.NEGO_SETT_STAT_CODE == "107")){
                    // 여긴 비공개 남의주문 협상상태만족임
                    itemView.ivSecret.toVisible()
                    itemView.tvBottomCount.text = R.string.star6.toResString()
                    itemView.tvBottomPrice.text = R.string.star6.toResString()
                }else{
                    // 여긴 비공개 남의주문 협상상태 불만족임
                    itemView.ivSecret.toGone()
                    itemView.tvBottomCount.text = item.DEAL_QTY
                    itemView.tvBottomPrice.text = item.DEAL_UPRC
                }

            }else{
                // 여긴 비공개 나의주문
                itemView.ivSecret.toGone()
                itemView.tvBottomCount.text = item.DEAL_QTY
                itemView.tvBottomPrice.text = item.DEAL_UPRC
            }

        }else{
            // 여긴 공개주문
            itemView.ivSecret.toGone()
            itemView.tvBottomCount.text = item.DEAL_QTY
            itemView.tvBottomPrice.text = item.DEAL_UPRC
        }


//        if (item.PUBLIC_YN.isY() || item.PBLS_CLNT_NO.isMe()){
//            itemView.ivSecret.toGone()
//            itemView.tvBottomCount.text = item.DEAL_QTY
//            itemView.tvBottomPrice.text = item.DEAL_UPRC
//        }else{
//            itemView.ivSecret.toVisible()
//            itemView.tvBottomCount.text = R.string.star6.toResString()
//            itemView.tvBottomPrice.text = R.string.star6.toResString()
//        }

        //매수, 매도
        itemView.tvDealType.text = item.DEAL_TP.toDealType()
        item.DEAL_TP.toDealTypeBackground(itemView.tvDealType)

        //MY
        if(item.PBLS_CLNT_NO.isMe()){
            itemView.tvMy.toVisible()
        }else{
            itemView.tvMy.toGone()
        }

        //시작 변경
        itemView.tvTime.text = String.format(R.string.main_start_end_time.toResString(), item.REG_DTTM, item.CHG_DTTM)


        //요청하기 버튼
        if(item.NEGO_SETT_STAT_CODE == "100" &&
                item.IS_MY_ORDER.isY() &&
                !item.CHANNEL_DEL_YN.isY()){
            itemView.tvRequestCancel.toVisible()
        }else{
            itemView.tvRequestCancel.toInvisible()
        }

        allInvisibleStatus()
        allClickableBtn(false)


        when(NegoStatusType.getType(item.NEGO_SETT_STAT_CODE)){
            NegoStatusType.NEGO_WAIT ->{
                //협상대기
                if(item.DEAL_TP == DealType.SELL.type){
                    //매도
                    itemView.tvNegoSellWait.toVisible()
                }else{
                    //매수
                    itemView.tvNegoBuyWait.toVisible()
                }
            }
            NegoStatusType.BREAKDOWN ->{
                //결렬
                itemView.tvStatusBreakdown.toVisible()
            }
            NegoStatusType.AUTO_BREAKDOWN ->{
                //자동결렬
                itemView.tvStatusAutoBreakdown.toVisible()
            }
            NegoStatusType.REQUEST_CANCEL ->{
                //요청취소
                itemView.tvStatusRequestCancel.toVisible()
            }
            NegoStatusType.CONTRACT_CANCEL ->{
                //계약취소
                itemView.tvStatusContractCancel.toVisible()
            }
            NegoStatusType.COMPLETE ->{
                //완료
                itemView.tvStatusComplete.toVisible()
            }
            else ->{
                //협상
                if(item.RMQTY == "0"){
                    itemView.tvNegoStatus3.toVisible()
                }else{
                    itemView.tvNegoStatus4.toVisible()
                }
            }
        }


        itemView.tvRequestCancel.setOnClickListener {
            onMainListClickListener?.onRquestCancel(item)
        }
        itemView.tvView.setOnClickListener {
            ActivityUtil.startWebActivity(context as Activity, R.string.main_tab_nego_progress.toResString(), Constants.URL_MY_NEGO_PROGRESS+"?LOGIN_ID="+ PreferenceUtils.getUserId()+"&CHANNEL_URL=${item.CHANNEL_URL}&ORDER_NO=${item.ORDER_NO}")
        }

        itemView.layoutItemContents.setOnClickListener {
        }
        itemView.layoutNegoStatus.setOnClickListener {
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
        allClickableBtn(false)

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
        itemView.tvRequestCancel.setOnClickListener {
        }
        itemView.tvView.setOnClickListener {
        }


    }

    private fun allInvisibleStatus(){
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

    private fun allClickableBtn(isClickableBtn: Boolean){
        if(isClickableBtn){
            itemView.tvNegoSellWait.background = R.drawable.rectangle_white_707fc6.toDrawable()
            itemView.tvNegoBuyWait.background = R.drawable.rectangle_white_ef508b.toDrawable()
            itemView.tvNegoStatus3.background = R.drawable.rectangle_f2f2f2_dddddd.toDrawable()
            itemView.tvNegoStatus4.background = R.drawable.rectangle_white_4aac9f.toDrawable()
            itemView.tvContract.background = R.drawable.rectangle_white_ccccc3.toDrawable()
            itemView.tvConclusion.background = R.drawable.rectangle_white_ccccc3.toDrawable()
            itemView.tvStatusBreakdown.background = R.drawable.rectangle_white_ccccc3.toDrawable()
            itemView.tvStatusAutoBreakdown.background = R.drawable.rectangle_white_ccccc3.toDrawable()
            itemView.tvStatusRequestCancel.background = R.drawable.rectangle_white_ccccc3.toDrawable()
            itemView.tvStatusContractCancel.background = R.drawable.rectangle_white_ccccc3.toDrawable()
            itemView.tvStatusComplete.background = R.drawable.rectangle_white_ccccc3.toDrawable()

        }else{
            itemView.tvNegoSellWait.background = null
            itemView.tvNegoBuyWait.background = null
            itemView.tvNegoStatus3.background = null
            itemView.tvNegoStatus4.background = null
            itemView.tvContract.background = null
            itemView.tvConclusion.background = null
            itemView.tvStatusBreakdown.background = null
            itemView.tvStatusAutoBreakdown.background = null
            itemView.tvStatusRequestCancel.background = null
            itemView.tvStatusContractCancel.background = null
            itemView.tvStatusComplete.background = null
        }
    }


}