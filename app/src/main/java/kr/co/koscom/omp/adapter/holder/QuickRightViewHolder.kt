package kr.co.koscom.omp.adapter.holder

import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.item_drawerright.view.tvRightMenuName
import kotlinx.android.synthetic.main.item_quickright.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.QuickRightMenuAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType


class QuickRightViewHolder(parent: ViewGroup, val listener: QuickRightMenuAdapter.OnMenuClickClickListener, private val dragListener: QuickRightMenuAdapter.OnStartDragListener) : BaseViewHolder(parent, R.layout.item_quickright) {

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is DrawerMenuData){
            item?.let {

                itemView.tvRightMenuName.text = it.title
                itemView.tvRightNumber.text = String.format("%02d", position+1)

                itemView.ivRightRemove.setOnClickListener {
                    listener.onMenuClick(position, item)
                }


                itemView.ivRightMove.setOnTouchListener {v, event ->
                    if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){
                        dragListener.onStartDrag(this)
                    }
                    else if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP){
                        dragListener.onEndDrag(this)
                    }
                    false
                }

//                try {
//                    itemView.layoutSubMenu.removeAllViews()
//                    for(data in it.listSubMenu){
//                        itemView.layoutSubMenu.addView(TextView(context).apply{
//                            val outValue = TypedValue()
//                            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue,true)
//                            setBackgroundResource(outValue.resourceId)
//                            setAppearance(context, R.style.F14R_8f8f91)
//                            text = data.title
//                            setPadding(14.dpToPx(), 4.dpToPx(), 0, 4.dpToPx())
//                            setOnClickListener {
//                                listener.onMenuClick(position, item)
//                            }
//                        })
//                    }
//                }catch (e: Exception){
//                    e.printStackTrace()
//                }
            }
        }
    }


}