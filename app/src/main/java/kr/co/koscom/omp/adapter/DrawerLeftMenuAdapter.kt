package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.koscom.omp.adapter.holder.DrawerLeftViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData

class DrawerLeftMenuAdapter(val listener: OnMenuClickClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: MutableList<DrawerMenuData> = arrayListOf()
    var selectPosition = 0


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
        return DrawerLeftViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listHolder = holder as DrawerLeftViewHolder
        listHolder.onBind(items[position], position)
    }


    /**
     * 스크롤시 메뉴 Selected
     */
    fun setSelectedPosition(position: Int){
        if(items[position].isSelectedMenu) return

        items[selectPosition].isSelectedMenu = false
        selectPosition = position
        items[position].isSelectedMenu = true
        notifyDataSetChanged()
    }




    /**
     * 리스트 클릭 리스너
     */
    interface OnMenuClickClickListener{
        fun onMenuClick(position: Int, item: Any)
    }
}