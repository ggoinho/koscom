package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.koscom.omp.adapter.holder.QuickRightViewHolder
import kr.co.koscom.omp.custom.QuickItemTouchHelperCallback
import kr.co.koscom.omp.data.local.DrawerMenuData
import java.util.*

class QuickRightMenuAdapter(val listener: OnMenuClickClickListener, val dragListener: OnStartDragListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), QuickItemTouchHelperCallback.OnItemMoveListener {

    var items: MutableList<DrawerMenuData> = arrayListOf()
    var selectPosition = 0


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
        return QuickRightViewHolder(parent, listener, dragListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listHolder = holder as QuickRightViewHolder
        listHolder.onBind(items[position], position)
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for(index in fromPosition until toPosition){
                Collections.swap(items, index, index + 1)
            }

        } else {
            for(index in toPosition downTo fromPosition){
                Collections.swap(items, index, index - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true

//        Collections.swap(items, fromPosition, toPosition)
//        notifyItemChanged(fromPosition, toPosition)
//        return false
    }

    /**
     * 스크롤시 메뉴 Selected
     */
    fun setSelectedPosition(position: Int) {
        if (items[position].isSelectedMenu) return

        items[selectPosition].isSelectedMenu = false
        selectPosition = position
        items[position].isSelectedMenu = true
        notifyDataSetChanged()
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