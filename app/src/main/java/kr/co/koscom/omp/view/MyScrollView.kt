package kr.co.koscom.omp.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.widget.NestedScrollView

class MyScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    var firstHeight = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d("MyScrollView", "onSizeChanged($w, $h, $oldw, $oldh)")

        if(firstHeight == 0){
            firstHeight = oldh
        }

        getChildAt(0).alpha = (h.toFloat() / firstHeight)
    }
}