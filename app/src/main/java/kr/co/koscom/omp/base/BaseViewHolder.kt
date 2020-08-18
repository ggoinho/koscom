package kr.co.koscom.omp.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * BaseViewHolder
 * 베이스 뷰홀더
 */
open class BaseViewHolder(viewGroup: ViewGroup, layoutRes: Int) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(
            layoutRes,
            viewGroup,
            false)
    ) {
    val context: Context = viewGroup.context
    open fun onBind(item: Any, position: Int) {}

}