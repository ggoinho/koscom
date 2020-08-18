package kr.co.koscom.omp.adapter.holder

import addRecentlyMenu
import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_search.view.*
import kotlinx.android.synthetic.main.item_drawerright.view.*
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.DrawerRightMenuAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.extension.dpToPx
import kr.co.koscom.omp.extension.setAppearance
import kr.co.koscom.omp.extension.toDrawable
import kr.co.koscom.omp.util.ActivityUtil
import java.util.ArrayList


class DrawerRightViewHolder(parent: ViewGroup, val listener: DrawerRightMenuAdapter.OnMenuClickClickListener) : BaseViewHolder(parent, R.layout.item_drawerright) {

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is DrawerMenuData){
            item?.let {

                itemView.tvRightMenuName.text = it.title
                itemView.tvRightMenuName.isSelected = it.isSelectedMenu
                itemView.ivRightIcon.background = getTitleDrawable(it.menuType)
                itemView.ivRightIcon.isSelected = it.isSelectedMenu

                try {
                    itemView.layoutSubMenu.removeAllViews()
                    for(data in it.listSubMenu){
                        itemView.layoutSubMenu.addView(TextView(context).apply{
                            val outValue = TypedValue()
                            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue,true)
                            setBackgroundResource(outValue.resourceId)
                            setAppearance(context, R.style.F14R_8f8f91)
                            text = data.title
                            setPadding(0, 9.dpToPx(), 0, 9.dpToPx())
                            tag = data
                            setOnClickListener {tagItem ->
                                val tagData = tagItem.tag as DrawerMenuData

                                tagData.addRecentlyMenu()
                                listener.onNotifyChanged()

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