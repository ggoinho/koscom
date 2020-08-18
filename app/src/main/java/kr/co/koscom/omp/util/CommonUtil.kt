package kr.co.koscom.omp.util

import android.content.Context

object CommonUtil {

    /**
     * Screen Width
     */
    fun getScreenWidth(context: Context): Int{
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * Screen Height
     */
    fun getScreenHeight(context: Context): Int{
        return context.resources.displayMetrics.heightPixels
    }

}