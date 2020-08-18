package kr.co.koscom.omp.adapter.holder

import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_main_header_filter.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.MainListAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.enums.MainOrderTab
import kr.co.koscom.omp.enums.NegoTiableMyTab
import kr.co.koscom.omp.extension.*

class MainHeaderFilterHolder(parent: ViewGroup, private var mainTotalData: MainTotalData) : BaseViewHolder(parent, R.layout.item_main_header_filter) {

    private var onHeaderFilterClickListener: MainListAdapter.OnHeaderFilterClickListener? = null

    init {
        initView()
        initListener()
    }

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is MainTotalData){
            mainTotalData = item
        }

//        tabType = tabType2
//        typeNegotiableMy = typeNegotiableMy2

        with(mainTotalData){

//            if(isRefresh){
                onSelectTab(tabType)
                checkNegoTiableMy(typeNegotiableMy)

            itemView.holdertvOrderTotalCount.toTextfromHtml(R.string.main_order_total_count.toResString(), totalCount)

//            }


        }

        itemView.holdertvOrderDetailView.setOnClickListener {
            onHeaderFilterClickListener?.onClickDetailView(mainTotalData, position)
        }

    }

    private fun initView(){

    }

    private fun initListener(){

        itemView.holdertvTabOrderStatus.setOnClickListener {
            onSelectTab(MainOrderTab.ORDER_STATUS, true)
        }
        itemView.holdertvTabMyNegoProgress.setOnClickListener {
            onSelectTab(MainOrderTab.NEGO_PROGRESS, true)
        }
        itemView.holdertvConclusionStatus.setOnClickListener {
            onSelectTab(MainOrderTab.CONCLUSION_STATUS, true)
        }

        itemView.holdertvNegotiable.setOnClickListener {
            if(itemView.holdertvNegotiable.isSelected){
                checkNegoTiableMy(NegoTiableMyTab.NONE, true)
            }else{
                checkNegoTiableMy(NegoTiableMyTab.NEGOTIABLE, true)
            }
        }

        itemView.holdertvMy.setOnClickListener {
            if(itemView.holdertvMy.isSelected){
                checkNegoTiableMy(NegoTiableMyTab.NONE, true)
            }else{
                checkNegoTiableMy(NegoTiableMyTab.MY, true)
            }
        }

    }

    /**
     * 검색 필터 탭 및 이벤트
     */
    fun setOnHeaderFilterClickListener(onHeaderFilterClickListener: MainListAdapter.OnHeaderFilterClickListener?){
        this.onHeaderFilterClickListener = onHeaderFilterClickListener
    }

    /**
     * 주문현황, 나의협상진행상황, 체결현황 TAB
     */
    private fun onSelectTab(type: MainOrderTab, isSynchronized: Boolean = false){

        when(type){
            MainOrderTab.ORDER_STATUS ->{
                //주문현황
                itemView.holdertvTabOrderStatus.isSelected = true
                itemView.holdertvTabOrderStatus.toBold(context)
                itemView.holdervTabDivide1.isSelected = true
                itemView.holdertvTabMyNegoProgress.isSelected = false
                itemView.holdertvTabMyNegoProgress.toNormal(context)
                itemView.holdervTabDivide2.isSelected = false
                itemView.holdertvConclusionStatus.isSelected = false
                itemView.holdertvConclusionStatus.toNormal(context)

                itemView.holdertvNegotiable.toVisible()
                itemView.holdertvMy.toVisible()
                itemView.holdertvOrderTotalCount.toVisible()
            }
            MainOrderTab.NEGO_PROGRESS ->{
                //나의협상 진행상황
                itemView.holdertvTabOrderStatus.isSelected = false
                itemView.holdertvTabOrderStatus.toNormal(context)
                itemView.holdervTabDivide1.isSelected = true
                itemView.holdertvTabMyNegoProgress.isSelected = true
                itemView.holdertvTabMyNegoProgress.toBold(context)
                itemView.holdervTabDivide2.isSelected = true
                itemView.holdertvConclusionStatus.isSelected = false
                itemView.holdertvConclusionStatus.toNormal(context)

                itemView.holdertvNegotiable.toGone()
                itemView.holdertvMy.toGone()
                itemView.holdertvOrderTotalCount.toGone()
            }
            MainOrderTab.CONCLUSION_STATUS ->{
                //체결현황
                itemView.holdertvTabOrderStatus.isSelected = false
                itemView.holdertvTabOrderStatus.toNormal(context)
                itemView.holdervTabDivide1.isSelected = false
                itemView.holdertvTabMyNegoProgress.isSelected = false
                itemView.holdertvTabMyNegoProgress.toNormal(context)
                itemView.holdervTabDivide2.isSelected = true
                itemView.holdertvConclusionStatus.isSelected = true
                itemView.holdertvConclusionStatus.toBold(context)

                itemView.holdertvNegotiable.toGone()
                itemView.holdertvMy.toGone()
                itemView.holdertvOrderTotalCount.toGone()
            }
            else ->{
                itemView.holdertvTabOrderStatus.isSelected = false
                itemView.holdertvTabOrderStatus.toNormal(context)
                itemView.holdervTabDivide1.isSelected = false
                itemView.holdertvTabMyNegoProgress.isSelected = false
                itemView.holdertvTabMyNegoProgress.toNormal(context)
                itemView.holdervTabDivide2.isSelected = false
                itemView.holdertvConclusionStatus.isSelected = false
                itemView.holdertvConclusionStatus.toNormal(context)

                itemView.holdertvNegotiable.toGone()
                itemView.holdertvMy.toGone()
                itemView.holdertvOrderTotalCount.toGone()
            }
        }

        if(isSynchronized){
            onHeaderFilterClickListener?.onSelectTabEvent(type)
        }else{

        }
    }

    /**
     * 협상가능, MY 체크
     */
    private fun checkNegoTiableMy(type: NegoTiableMyTab, isSynchronized: Boolean = false){

        when(type){
            NegoTiableMyTab.NONE->{
                itemView.holdertvNegotiable.isSelected = false
                itemView.holdertvMy.isSelected = false
            }
            NegoTiableMyTab.NEGOTIABLE->{
                itemView.holdertvNegotiable.isSelected = true
                itemView.holdertvMy.isSelected = false
            }
            NegoTiableMyTab.MY->{
                itemView.holdertvNegotiable.isSelected = false
                itemView.holdertvMy.isSelected = true
            }
            else ->{
                itemView.holdertvNegotiable.isSelected = true
                itemView.holdertvMy.isSelected = true
            }
        }

        if(isSynchronized){
            onHeaderFilterClickListener?.checkNegoTiableMyEvent(type)
        }else{

        }
    }



}