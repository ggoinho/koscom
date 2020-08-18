package kr.co.koscom.omp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.adapter.holder.OrderDetailFragment

class OrderDetailPagerAdapter(fm: FragmentManager, l: Lifecycle, val listener: OnNegoPagerClickListener): FragmentStateAdapter(fm, l) {

    private var items: MutableList<Order.OrderItem> = arrayListOf()

    fun getOrderList(): MutableList<Order.OrderItem>{
        return items
    }

    fun addAll(list: List<Order.OrderItem>?){
        items.clear()
        items.addAll(list?: arrayListOf())
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return OrderDetailFragment.newInstance(position, items[position], listener)
    }

    /**
     * 협상관리 클릭 리스너
     */
    interface OnNegoPagerClickListener{
        fun onContractRequestClick(item: Any)
    }

}