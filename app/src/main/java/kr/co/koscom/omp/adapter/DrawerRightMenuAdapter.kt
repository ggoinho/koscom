package kr.co.koscom.omp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.adapter.holder.DrawerRightHeaderViewHolder
import kr.co.koscom.omp.adapter.holder.DrawerRightViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData
import java.util.ArrayList

class DrawerRightMenuAdapter(val listener: OnMenuClickClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val COUNT_HEADER = 1
    private val HEADER = 101

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

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            HEADER
        }else{
            super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size + COUNT_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when(viewType){
            HEADER ->{
                DrawerRightHeaderViewHolder(parent, listener)
            }
            else ->{
                DrawerRightViewHolder(parent, listener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
             HEADER ->{
                 val recentlyDrawerMenu = Preference.getRecentlyDrawerMenu(BaseApplication.getAppContext()) ?: ""

//                  Gson().fromJson(recentlyDrawerMenu, DrawerMenuData::class.java) ?: DrawerMenuData()

                val listHolder = holder as DrawerRightHeaderViewHolder
                listHolder.onBind(recentlyDrawerMenu, position)
            }
            else ->{
                val listHolder = holder as DrawerRightViewHolder
                listHolder.onBind(items[position-1], position-1)
            }
        }

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
        fun onNotifyChanged()
    }
}