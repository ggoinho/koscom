package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.koscom.omp.adapter.holder.DrawerLeftViewHolder
import kr.co.koscom.omp.adapter.holder.DrawerRelationSearchViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData

class DrawerRelationSearchAdapter(val listener: OnMenuClickClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: MutableList<DrawerMenuData> = arrayListOf()
    var searchContents: String = ""

    fun getList(): MutableList<DrawerMenuData>{
        return items
    }

    /**
     * 리스트 추가
     */
    fun addItem(item: DrawerMenuData){
        items.add(item)
    }

    /**
     * 리스트 클리어
     */
    fun clearList(){
        items.clear()
    }


    fun notifyChangedRange(){
        notifyItemRangeChanged(0, itemCount);
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return DrawerRelationSearchViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listHolder = holder as DrawerRelationSearchViewHolder
        listHolder.onBind(items[position], position, searchContents)
    }


    /**
     * 리스트 클릭 리스너
     */
    interface OnMenuClickClickListener{
        fun onNotifyChanged()
    }
}