package kr.co.koscom.omp.adapter.holder

import addRecentlyMenu
import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_drawerheader.view.*
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.DrawerRightMenuAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.custom.CustomRecentlyView
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.extension.toDrawable
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.util.ActivityUtil
import removeRecently
import java.util.ArrayList


class DrawerRightHeaderViewHolder(parent: ViewGroup, val listener: DrawerRightMenuAdapter.OnMenuClickClickListener) : BaseViewHolder(parent, R.layout.item_drawerheader) {

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(!(item as String).isNullOrEmpty()){
            val recentlyMenu = Gson().fromJson(item, object : TypeToken<ArrayList<DrawerMenuData>>() {}.type) as ArrayList<DrawerMenuData>

            recentlyMenu?.let {
                try {

                    if(recentlyMenu.isNullOrEmpty()){
                        itemView.layoutDrawerRightHeaderFrame.toGone()
                        return@let
                    }else{
                        itemView.layoutDrawerRightHeaderFrame.toVisible()
                    }

                    itemView.layoutRecentlyMenu.removeAllViews()
                    for(data in recentlyMenu){
                        itemView.layoutRecentlyMenu.addView(CustomRecentlyView(context).apply{
                            setDrawerMenuData(data)
                            setOnItemClickListener(object: CustomRecentlyView.OnItemClickListener{
                                override fun onClick(data: DrawerMenuData) {
                                    data.addRecentlyMenu()
                                    listener.onNotifyChanged()
                                    ActivityUtil.startDrawerType(context as Activity, data.title)
                                }

                                override fun onRemove(view: View, data: DrawerMenuData) {
                                    data.removeRecently()
                                    listener.onNotifyChanged()
                                }
                            })
                        })
                    }

                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }else{
            itemView.layoutDrawerRightHeaderFrame.toGone()
        }

        itemView.tvRightHeaderAllRemove.setOnClickListener {
            Preference.setRecentlyDrawerMenu(context, "")
            listener.onNotifyChanged()
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