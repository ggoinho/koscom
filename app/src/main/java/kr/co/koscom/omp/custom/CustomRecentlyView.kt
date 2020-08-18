package kr.co.koscom.omp.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.fragment_web.view.*
import kotlinx.android.synthetic.main.view_custom_hashtag.view.*
import kotlinx.android.synthetic.main.view_custom_recently.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.extension.*


class CustomRecentlyView
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
        inflater.inflate(R.layout.view_custom_recently, this, true)
        initListener()
    }

    fun initListener() {

        layoutRecently.setOnClickListener {
            drawerMenuData?.let {
                onItemClickListener?.onClick(it)
            }
        }

        ivRecentlyRemove.setOnClickListener {
            drawerMenuData?.let {
                onItemClickListener?.onRemove(this, it)
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

    fun setDrawerMenuData(data: DrawerMenuData) {
        drawerMenuData = data
        tvRecentlyName.text = data.title

    }



//    fun notifyDataSetChanged(){
//        try{
//            layoutHashTag.removeAllViews()
//            for (i in 0 until listHashTag.size){
//                layoutHashTag.addView(createHashTagView(listHashTag[i]))
//            }
//        }catch (e: Exception){
//            e.printStackTrace()
//        }
//    }


    /**************************************************
     * OnItemClickListener
     */
    interface OnItemClickListener {
        fun onClick(data: DrawerMenuData)
        fun onRemove(view: View, data: DrawerMenuData)
    }

}
