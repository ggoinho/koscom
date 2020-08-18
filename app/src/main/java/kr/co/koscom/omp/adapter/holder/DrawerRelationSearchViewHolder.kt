package kr.co.koscom.omp.adapter.holder

import addRecentlyMenu
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_custom_drawer_search.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.DrawerRelationSearchAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.extension.toResColor

class DrawerRelationSearchViewHolder(parent: ViewGroup, val listener: DrawerRelationSearchAdapter.OnMenuClickClickListener) : BaseViewHolder(parent, R.layout.view_custom_drawer_search) {

    fun onBind(item: Any, position: Int, searchContents: String) {
        super.onBind(item, position)

        if(item is DrawerMenuData){
            item?.let {

                try {
                    val searchIndex = item.searchName.lastIndexOf(searchContents)

                    val sp = SpannableStringBuilder(item.searchName)
                    sp.setSpan(ForegroundColorSpan(R.color.yellow_ffc000.toResColor()), searchIndex, searchIndex + searchContents.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    itemView.tvRelationSearch.text = sp
                }catch (e: Exception){
                    e.printStackTrace()
                }



                itemView.layoutRelationSearch.setOnClickListener {
                    item.addRecentlyMenu()
                    listener.onNotifyChanged()
                }
            }
        }

    }
}