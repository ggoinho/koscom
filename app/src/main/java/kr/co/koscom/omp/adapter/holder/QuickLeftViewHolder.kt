package kr.co.koscom.omp.adapter.holder

import addRecentlyMenu
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_drawerright.view.layoutSubMenu
import kotlinx.android.synthetic.main.item_quickleft.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.QuickLeftMenuAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.extension.dpToPx
import kr.co.koscom.omp.extension.setAppearance
import kr.co.koscom.omp.extension.toDrawable


class QuickLeftViewHolder(parent: ViewGroup, val listener: QuickLeftMenuAdapter.OnMenuClickClickListener) : BaseViewHolder(parent, R.layout.item_quickleft) {

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is DrawerMenuData){
            item?.let {

                itemView.tvLeftMenuName.text = it.title
                itemView.tvLeftMenuName.isSelected = it.isSelectedMenu

                try {
                    itemView.layoutSubMenu.removeAllViews()
                    for((index, data) in it.listSubMenu.withIndex()){
                        itemView.layoutSubMenu.addView(TextView(context).apply{
                            background = R.drawable.color_selector_f2f5f9_f3f9ff.toDrawable()
                            setAppearance(context, R.style.F14R_8f8f91)
                            text = data.title

                            isSelected = data.isSelectedMenu
                            setPadding(14.dpToPx(), 4.dpToPx(), 0, 4.dpToPx())
                            tag = data
                            setOnClickListener {view ->
                                val tagData = view.tag as DrawerMenuData

                                listener.onMenuClick(position, tagData)

                            }
                        })
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }


    private fun getTitleDrawable(type: DrawerMenuType): Drawable?{
        return when(type){
            DrawerMenuType.ORDER->{
                R.drawable.select_drawer_order.toDrawable()
            }
            DrawerMenuType.STOCK_INFO->{
                R.drawable.select_drawer_stock_info.toDrawable()
            }
            DrawerMenuType.MYPAGE->{
                R.drawable.select_drawer_mypage.toDrawable()
            }
            DrawerMenuType.CUSTOMER_CENTER->{
                R.drawable.select_drawer_customer.toDrawable()
            }
            DrawerMenuType.SETTING->{
                R.drawable.select_drawer_setting.toDrawable()
            }
        }
    }


}