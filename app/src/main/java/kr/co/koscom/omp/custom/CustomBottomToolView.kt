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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.fragment_web.view.*
import kotlinx.android.synthetic.main.view_custom_bottom_tool.view.*
import kotlinx.android.synthetic.main.view_custom_hashtag.view.*
import kotlinx.android.synthetic.main.view_custom_recently.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.BottomToolAdapter
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.extension.*


class CustomBottomToolView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var drawerMenuData: DrawerMenuData? = null

    private val adapterBottom: BottomToolAdapter by lazy{
        BottomToolAdapter()
    }

    init {
        initialize()
    }

    private fun initialize() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_custom_bottom_tool, this, true)
        init()
        initListener()
    }

    private fun init(){

        rvBottom.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            itemAnimator = DefaultItemAnimator()

            adapter = adapterBottom
        }

    }

    fun initListener() {




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
