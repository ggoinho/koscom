package kr.co.koscom.omp.adapter

import android.bluetooth.BluetoothDevice
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.ui.main.MainCardFragment
import kr.co.koscom.omp.util.LogUtil

class MainViewPagerAdapter(fm: FragmentManager, l: Lifecycle): FragmentStateAdapter(fm, l) {

    private var listener: OnItemClickListener? = null
    private var itemsNego: MutableList<Main.Nego> = arrayListOf()
    private var itemsContract: MutableList<Main.NegoContract> = arrayListOf()

    private var type: LIST_TYPE = LIST_TYPE.NEGO

    enum class LIST_TYPE{
        NEGO,
        CONTRACT
    }

    fun setItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    fun addAll(main: Main){
        itemsNego.clear()
        itemsContract.clear()

        main.datas?.let {
            if(!it.myNegoList.isNullOrEmpty()){
                itemsNego.addAll(it.myNegoList!!)
            }

            if(!it.myContList.isNullOrEmpty()){
                itemsContract.addAll(it.myContList!!)
            }
        }
    }

    fun isEmptyNegoList(): Boolean{
        return itemsNego.isNullOrEmpty()
    }

    fun isEmptyContractList(): Boolean{
        return itemsContract.isNullOrEmpty()
    }


    override fun getItemCount(): Int {
        return if(type == LIST_TYPE.NEGO) itemsNego.size else itemsContract.size
    }

    override fun createFragment(position: Int): Fragment {

        return when(type){
            LIST_TYPE.NEGO ->{
                MainCardFragment()
            }
            LIST_TYPE.CONTRACT ->{
                MainCardFragment()
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(item: Object, position: Int)
    }


}