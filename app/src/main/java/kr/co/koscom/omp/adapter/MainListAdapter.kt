package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import kr.co.koscom.omp.adapter.holder.MainFooterHolder
import kr.co.koscom.omp.adapter.holder.MainHeaderFilterHolder
import kr.co.koscom.omp.adapter.holder.MainHeaderHolder
import kr.co.koscom.omp.adapter.holder.MainListViewHolder
import kr.co.koscom.omp.data.local.MainListData
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.enums.MainOrderTab
import kr.co.koscom.omp.enums.NegoTiableMyTab

class MainListAdapter(val fm: FragmentManager,val l: Lifecycle) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val COUNT_HEADER = 2
    private val COUNT_FOOTER = 1
    private val HEADER = 101
    private val HEADER_FILTER = 102
    private val FOOTER = 103

    private var mainFooterHolder: MainFooterHolder? = null
    private var mainHeaderHolder: MainHeaderHolder? = null
    private var mainHeaderFilterHolder: MainHeaderFilterHolder? = null

    private var onHeaderFilterClickListener: OnHeaderFilterClickListener? = null                                //검색 필터 탭 클릭 리스너
    private var onNegoPagerClickListener: MainHeaderHolder.OnNegoPagerClickListener? = null                     //협상관리 클릭 리스너
    private var onContractPagerClickListener: MainHeaderHolder.OnContractPagerClickListener? = null             //계약 및 체결관리 클릭 리스너
    private var onMainListClickListener: OnMainListClickListener? = null                   //주문 현황 리스트 클릭 리스너


//    var tabType: MainOrderTab = MainOrderTab.ORDER_STATUS
//    var typeNegotiableMy: NegoTiableMyTab = NegoTiableMyTab.NONE

    private var mainData: MainTotalData = MainTotalData()

    //상단 ViewPager Referesh 시킬지 여부
    private var isRefresh: Boolean = false

    fun setIsRefresh(isRefresh: Boolean){
        this.isRefresh = isRefresh
    }

    /**
     * MainData 셋팅
     */
    fun setMainTotalData(main: MainTotalData){
        mainData = main
    }

    /**
     * 총 건수
     */
    fun setTotalCount(count: Int = 0){
        mainData.totalCount = count
    }

    /**
     * 주문현황 리스트 추가
     */
    fun addTradeList(list: List<Order.OrderItem>){
        for(item in list){
            mainData.listMain?.add(MainListData().apply {
                tabType = MainOrderTab.ORDER_STATUS
                orderItem = item
            })
        }
    }

    /**
     * 나의협상 진행상황 리스트 추가
     */
    fun addNegoList(list: List<OrderNego.OrderNegoItem>){
        for(item in list){
            mainData.listMain?.add(MainListData().apply {
                tabType = MainOrderTab.NEGO_PROGRESS
                orderNego = item
            })
        }
    }

    /**
     * 체결현황 리스트 추가
     */
    fun addContractList(list: List<OrderContract.OrderContractItem>){
        for(item in list){
            mainData.listMain?.add(MainListData().apply {
                tabType = MainOrderTab.CONCLUSION_STATUS
                orderContractItem = item
            })
        }
    }

    /**
     * 리스트 클리어
     */
    fun clearList(){
        mainData.listMain.clear()
    }

    /**
     *  주문현황 리스트 가져오기
     */
    fun getTradeList(): MutableList<MainListData>{
        return mainData.listMain
    }

    /**
     *  주문현황, 나의 협상 진행상황, 체결 현황
     */
    fun setFilterTab(type: MainOrderTab){
        mainData.tabType = type
    }

    /**
     *  협상가능 or MY 체크
     */
    fun setFilterCheckNegoMy(type: NegoTiableMyTab){
        mainData.typeNegotiableMy = type
    }


    /**
     * 검색 필터 탭 및 이벤트
     */
    fun setOnHeaderFilterClickListener(onHeaderFilterClickListener: OnHeaderFilterClickListener){
        this.onHeaderFilterClickListener = onHeaderFilterClickListener
    }

    /**
     * 상단 협상관리 뷰페이저 거절 & 수락 이벤트
     */
    fun setOnNegoPagerClickListener(onNegoPagerClickListener: MainHeaderHolder.OnNegoPagerClickListener){
        this.onNegoPagerClickListener = onNegoPagerClickListener
    }
    /**
     * 상단 계약 및 체결관리 뷰페이저 체결 & 확인 이벤트
     */
    fun setOnContractPagerClickListener(onContractPagerClickListener: MainHeaderHolder.OnContractPagerClickListener){
        this.onContractPagerClickListener = onContractPagerClickListener
    }

    /**
     * 메 리스트 클릭 리스너
     */
    fun setOnMainListClickListener(onMainListClickListener: OnMainListClickListener){
        this.onMainListClickListener = onMainListClickListener
    }


    fun setMainTopHeader(data: MainTotalData){
        mainHeaderHolder?.setAdapterData(data)
    }

    fun notifyChangedRange(){
        notifyItemRangeChanged(0, itemCount);
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            HEADER
        } else if(position == 1){
            HEADER_FILTER
        } else if(mainData.listMain != null && mainData.listMain!!.size > (position-COUNT_HEADER)){
            super.getItemViewType(position)
        }else{
            FOOTER
        }
    }

    override fun getItemCount(): Int {
        return if(mainData.listMain !=null) mainData.listMain!!.size + COUNT_HEADER + COUNT_FOOTER else COUNT_HEADER + COUNT_FOOTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> {
                mainHeaderHolder = MainHeaderHolder(parent, mainData, fm, l)
                mainHeaderHolder!!
            }
            HEADER_FILTER -> {
                mainHeaderFilterHolder = MainHeaderFilterHolder(parent, mainData)
                mainHeaderFilterHolder!!
            }
            FOOTER -> {
                mainFooterHolder = MainFooterHolder(parent, mainData)
                mainFooterHolder!!
            }
            else -> {
                MainListViewHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            getItemViewType(position) == HEADER -> {
                mainHeaderHolder?.setOnNegoPagerClickListener(onNegoPagerClickListener)
                mainHeaderHolder?.setOnContractPagerClickListener(onContractPagerClickListener)
                mainHeaderHolder?.onBind(mainData, position, isRefresh)
            }
            getItemViewType(position) == HEADER_FILTER -> {
                mainHeaderFilterHolder?.setOnHeaderFilterClickListener(onHeaderFilterClickListener)
                mainHeaderFilterHolder?.onBind(mainData, position)
            }
            getItemViewType(position) == FOOTER -> {
                mainFooterHolder?.onBind(mainData, position)
            }
            else -> {
                val listHolder = holder as MainListViewHolder
                listHolder.onBind(mainData.listMain[position-COUNT_HEADER], position-COUNT_HEADER, onMainListClickListener)
            }
        }
    }



    interface OnHeaderFilterClickListener{
        fun onSelectTabEvent(type: MainOrderTab, isSynchronized: Boolean = false)
        fun checkNegoTiableMyEvent(type: NegoTiableMyTab, isSynchronized: Boolean = false)
        fun onClickDetailView(item: Any, position: Int)
    }

    /**
     * 메인 리스트 클릭 리스너
     */
    interface OnMainListClickListener{
        fun onRquestCancel(item: Any)
    }
}