package kr.co.koscom.omp.custom

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_custom_drawer_search.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.extension.toResColor
import kr.co.koscom.omp.util.LogUtil


class CustomDrawerSearchView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onItemClickListener: OnItemClickListener? = null
    private var textStyle: Int? = null
    private var textBackground: Int? = null
    private var drawerMenuData: DrawerMenuData? = null

    init {
        initialize()
    }

    private fun initialize() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_custom_drawer_search, this, true)
        initListener()
    }

    fun initListener() {

        layoutRelationSearch.setOnClickListener {
            drawerMenuData?.let {
                onItemClickListener?.onClick(it)
            }
        }


    }

    fun setTextStyle(style: Int){
        textStyle = style
    }

    fun setTextBackground(background: Int){
        textBackground = background
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setDrawerSearchContents(menuData: DrawerMenuData, searchContents: String) {

        drawerMenuData = menuData
        val searchIndex = menuData.searchName.lastIndexOf(searchContents)

        val sp = SpannableStringBuilder(menuData.searchName)
        sp.setSpan(ForegroundColorSpan(R.color.yellow_ffc000.toResColor()), searchIndex, searchIndex + searchContents.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvRelationSearch.append(sp)

    }


    /**************************************************
     * OnItemClickListener
     */
    interface OnItemClickListener {
        fun onClick(data: DrawerMenuData)
    }

}
