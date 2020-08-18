package kr.co.koscom.omp.extension

import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import kr.co.koscom.omp.BaseApplication
import kotlin.math.roundToInt

fun Int.toDrawable(): Drawable? {
    return AppCompatResources.getDrawable(BaseApplication.getAppContext(), this)
}


fun Int.toResString(): String {
    return BaseApplication.getAppContext().resources.getString(this)
}

fun Int.dpToPx(): Int {
    val displayMetrics = BaseApplication.getAppContext().resources.displayMetrics
    return (this * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Int.pxToDp(): Int {
    val displayMetrics = BaseApplication.getAppContext().resources.displayMetrics
    return (this / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}



fun Int.toResColor(view: TextView) {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        view.setTextColor(BaseApplication.getAppContext().getColor(this))
    } else {
        view.setTextColor(ContextCompat.getColor(BaseApplication.getAppContext(), this))
    }
}

fun Int.toResColor(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        BaseApplication.getAppContext().getColor(this)
    } else {
        ContextCompat.getColor(BaseApplication.getAppContext(), this)
    }
}

fun Int.toResStyle(view: TextView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        view.setTextAppearance(this)
    } else {
        view.setTextAppearance(BaseApplication.getAppContext(), this)
    }
}