package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.koscom.omp.adapter.holder.BottomToolViewHolder
import kr.co.koscom.omp.adapter.holder.QuickRightViewHolder
import kr.co.koscom.omp.custom.QuickItemTouchHelperCallback
import kr.co.koscom.omp.data.local.DrawerMenuData
import java.util.*

class BottomToolAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: MutableList<DrawerMenuData> = arrayListOf()

    fun getList(): MutableList<DrawerMenuData> {
        return items
    }

    /**
     * 리스트 추가
     */
    fun addItem(item: DrawerMenuData) {
        items.add(item)
    }

    fun addAllItem(list: MutableList<DrawerMenuData>){
        items.clear()
        items.addAll(list)
    }

    fun removeItem(item: DrawerMenuData){
        items.remove(item)
    }

    /**
     * 리스트 클리어
     */
    fun clearList() {
        items.clear()
    }


    fun notifyChangedRange() {
        notifyItemRangeChanged(0, itemCount);
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BottomToolViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listHolder = holder as BottomToolViewHolder
        listHolder.onBind(items[position], position)
    }


    /**
     * 리스트 클릭 리스너
     */
    interface OnMenuClickClickListener {
        fun onMenuClick(position: Int, item: Any)
    }

    interface OnStartDragListener{
        fun onStartDrag(holder: QuickRightViewHolder)
        fun onEndDrag(holder: QuickRightViewHolder)
    }

}