package kr.co.koscom.omp.extension

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView

/**
 *  make view gone
 */
fun View.toGone() {
    this.visibility = View.GONE
}

fun View.toVisible() {
    this.visibility = View.VISIBLE
}

/**
 *  make view invisible
 */
fun View.toInvisible() {
    this.visibility = View.INVISIBLE

}

fun TextView.setAppearance(context: Context, res: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        this.setTextAppearance(context, res)
    } else {
        this.setTextAppearance(res)
    }
}

/**
 * Enable View & Alpha 0.4
 */
fun View.enableView(enable: Boolean){
    this.isEnabled = enable
    if(enable){
        this.alpha = 1.0f
    }else{
        this.alpha = 0.4f
    }
}
