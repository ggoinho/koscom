package kr.co.koscom.omp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.co.koscom.omp.adapter.holder.ContractManagePagerFragment
import kr.co.koscom.omp.adapter.holder.ContractManagePagerView
import kr.co.koscom.omp.adapter.holder.MainHeaderHolder
import kr.co.koscom.omp.adapter.holder.NegoManagePagerFragment
import kr.co.koscom.omp.data.model.Main

/**
 *
 */
class MainPagerContractAdapter(context: Context) : PagerAdapter() {
    var context : Context?= null

    private var listener: MainHeaderHolder.OnContractPagerClickListener? = null
    private var itemsContract: MutableList<Main.NegoContract> = arrayListOf()


    init {
        this.context = context
    }

    fun setOnContractPagerClickListener(listener: MainHeaderHolder.OnContractPagerClickListener){
        this.listener = listener
    }

    fun addAll(list: List<Main.NegoContract>?){
        itemsContract.clear()
        itemsContract.addAll(list?: arrayListOf())
    }

    fun isEmptyData(): Boolean{
        return itemsContract.size == 0
    }

    override fun getCount(): Int {
        return itemsContract.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        ContractManagePagerView(context!!, container, itemsContract[position], position, listener, itemsContract.size).apply {
            onBind()
            return getView()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        try {
            val v = `object` as View
            container.removeView(v)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun getItemPosition(`object`: Any): Int {
//        return POSITION_NONE
//    }

    interface OnItemClickListener{
        fun onItemClick(item: Object, position: Int)
    }
}
/*
class MainPagerContractAdapter(fm: FragmentManager, l: Lifecycle, val listener: MainHeaderHolder.OnContractPagerClickListener): FragmentStateAdapter(fm, l) {

    private var items: MutableList<Main.NegoContract> = arrayListOf()

    fun getOrderList(): MutableList<Main.NegoContract>{
        return items
    }

    fun addAll(list: List<Main.NegoContract>?){
        items.clear()
        items.addAll(list?: arrayListOf())
    }

    fun isEmptyData(): Boolean{
        return items.size == 0
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return ContractManagePagerFragment.newInstance(position, items[position],  items.size, listener)
    }
}
 */