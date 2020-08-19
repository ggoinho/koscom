package kr.co.koscom.omp.adapter.holder

import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.item_drawerright.view.tvRightMenuName
import kotlinx.android.synthetic.main.item_quickright.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData


class BottomToolViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.item_quickright) {

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is DrawerMenuData){
            item?.let {


            }
        }
    }


}