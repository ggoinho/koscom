package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.koscom.omp.adapter.holder.*
import kr.co.koscom.omp.data.local.MainListData
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.enums.MainOrderTab
import kr.co.koscom.omp.enums.NegoTiableMyTab
import kr.co.koscom.omp.enums.OrderListTab

class OrderListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onOrderListClickListener: OnOrderListClickListener? = null                   //주문 현황 리스트 클릭 리스너


    var tabType = OrderListTab.ORDER_STATUS
    var dealType = DealType.ALL

    val listOrder = ArrayList<Order.OrderItem>()
    val listContract = ArrayList<OrderContract.OrderContractItem>()


    fun getOrderList(): MutableList<Order.OrderItem>{
        return listOrder
    }

    fun getContractList(): MutableList<OrderContract.OrderContractItem>{
        return listContract
    }

    /**
     * 주문현황 리스트 추가
     */
    fun addOrderList(list: List<Order.OrderItem>){
        listOrder.addAll(list)
    }

    /**
     * 체결현황 리스트 추가
     */
    fun addContractList(list: List<OrderContract.OrderContractItem>){
        listContract.addAll(list)
    }


    /**
     * 리스트 클리어
     */
    fun clearList(){
        listOrder.clear()
        listContract.clear()
    }

    /**
     * 리스트 클릭 리스너
     */
    fun setOnOrderListClickListener(onOrderListClickListener: OnOrderListClickListener){
        this.onOrderListClickListener = onOrderListClickListener
    }

    fun notifyChangedRange(){
        notifyItemRangeChanged(0, itemCount);
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return when(tabType){
            OrderListTab.CONCLUSION_STATUS->{
                listContract.size
            }
            OrderListTab.ORDER_STATUS->{
                listOrder.size
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return OrderListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listHolder = holder as OrderListViewHolder
        when(tabType){
            OrderListTab.CONCLUSION_STATUS->{
                listHolder.onBind(listContract[position], position, tabType)
            }
            OrderListTab.ORDER_STATUS->{
                listHolder.onBind(listOrder[position], position, tabType)
            }
        }

    }



    /**
     * 리스트 클릭 리스너
     */
    interface OnOrderListClickListener{
        fun onRquestCancel(item: Any)
    }
}