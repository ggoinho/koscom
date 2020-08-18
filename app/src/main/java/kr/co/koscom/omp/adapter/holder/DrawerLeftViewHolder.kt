package kr.co.koscom.omp.adapter.holder

import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_drawerleft.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.DrawerLeftMenuAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.extension.setAppearance

class DrawerLeftViewHolder(parent: ViewGroup, val listener: DrawerLeftMenuAdapter.OnMenuClickClickListener) : BaseViewHolder(parent, R.layout.item_drawerleft) {

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is DrawerMenuData){
            item?.let {
                itemView.tvLeftMenuName.text = it.title

                itemView.layoutDrawerLeft.isSelected = it.isSelectedMenu
                if(it.isSelectedMenu){
                    itemView.tvLeftMenuName.setAppearance(context, R.style.F18B_purple_4e4e9e)
                }else{
                    itemView.tvLeftMenuName.setAppearance(context, R.style.F14R_333333)
                }

                itemView.layoutDrawerLeft.setOnClickListener {
                    listener.onMenuClick(position, item)
                }
            }
        }

    }
}