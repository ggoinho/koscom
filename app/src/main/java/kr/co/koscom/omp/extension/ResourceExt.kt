package kr.co.koscom.omp.extension

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import kr.co.koscom.omp.BaseApplication

fun Int.toDrawable(): Drawable? {
    return AppCompatResources.getDrawable(BaseApplication.getAppContext(), this)
}