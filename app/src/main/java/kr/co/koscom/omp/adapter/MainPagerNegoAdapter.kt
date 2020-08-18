package kr.co.koscom.omp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.co.koscom.omp.adapter.holder.MainHeaderHolder
import kr.co.koscom.omp.adapter.holder.NegoManagePagerFragment
import kr.co.koscom.omp.adapter.holder.NegoManagePagerView
import kr.co.koscom.omp.data.model.Main

/**
 *
 */
class MainPagerNegoAdapter(context: Context) : PagerAdapter() {

    var context : Context?= null

    private var listener: MainHeaderHolder.OnNegoPagerClickListener? = null
    private var itemsNego: MutableList<Main.Nego> = arrayListOf()

    init {
        this.context = context
    }

    fun setOnNegoPagerClickListener(listener: MainHeaderHolder.OnNegoPagerClickListener){
        this.listener = listener
    }

    fun addAll(list: List<Main.Nego>?){
        itemsNego.clear()
        itemsNego.addAll(list?: arrayListOf())

    }

    fun isEmptyData(): Boolean{
        return itemsNego.size == 0
    }

    override fun getCount(): Int {
        return itemsNego.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        NegoManagePagerView(context!!, container, itemsNego[position], position, listener, itemsNego.size).apply {
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


}

/*
class MainPagerNegoAdapter(fm: FragmentManager, l: Lifecycle, val listener: MainHeaderHolder.OnNegoPagerClickListener): FragmentStateAdapter(fm, l) {

    private var items: MutableList<Main.Nego> = arrayListOf()

    fun getOrderList(): MutableList<Main.Nego>{
        return items
    }

    fun addAll(list: List<Main.Nego>?){
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
        return NegoManagePagerFragment.newInstance(position, items[position],  items.size, listener)
    }

}
 */