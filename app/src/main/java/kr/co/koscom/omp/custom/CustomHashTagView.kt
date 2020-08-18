package kr.co.koscom.omp.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.fragment_web.view.*
import kotlinx.android.synthetic.main.view_custom_hashtag.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.extension.*


class CustomHashTagView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var listHashTag = arrayListOf<Order.Hash>()

    private var onItemClickListener: OnItemClickListener? = null
    private var textStyle: Int? = null
    private var textBackground: Int? = null

    init {
        initialize()
    }

    private fun initialize() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_custom_hashtag, this, true)
    }

    fun initListener() {

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

    fun setHashTagList(list: List<Order.Hash>) {
        listHashTag.clear()
        listHashTag.addAll(list)
    }

    /**
     * Custom 해시태그 텍스트뷰 생성
     */
    private fun createHashTagView(hashData: Order.Hash): TextView{
        return TextView(context).apply {
            textStyle?.let {it
                setAppearance(context, it)
            }
            textBackground?.let {it
                background = it.toDrawable()
            }
            text = hashData.HASH_TAG_NM.toHashTag()
            this.setPadding(10.dpToPx(), 3.dpToPx(), 10.dpToPx(), 3.dpToPx())

            val params = FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 0, 3.dpToPx(), 5.dpToPx())
            layoutParams = params

            setOnClickListener {
                onItemClickListener?.onClick(hashData)
            }
        }
    }

    fun notifyDataSetChanged(){
        try{
            layoutHashTag.removeAllViews()
            for (i in 0 until listHashTag.size){
                layoutHashTag.addView(createHashTagView(listHashTag[i]))
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    /**************************************************
     * OnItemClickListener
     */
    interface OnItemClickListener {
        fun onClick(data: Order.Hash)
    }

}
