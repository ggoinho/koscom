package kr.co.koscom.omp.extension

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import kr.co.koscom.omp.R

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

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun View.isInvisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun TextView.setAppearance(context: Context, res: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        this.setTextAppearance(context, res)
    } else {
        this.setTextAppearance(res)
    }
}

fun TextView.toBold(context: Context){
    this.setTypeface(ResourcesCompat.getFont(context, R.font.spoqa_han_sans), Typeface.BOLD)
}

fun TextView.toNormal(context: Context){
    this.setTypeface(ResourcesCompat.getFont(context, R.font.spoqa_han_sans), Typeface.NORMAL)
}


fun TextView.toTextfromHtml(format: String, vararg args: Any) {
    text = if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(String.format(format, *args))
    } else {
        Html.fromHtml(String.format(format, *args), Html.FROM_HTML_MODE_LEGACY)
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


/**
 * 입력 가능 여부
 */
fun EditText.setInputAvailable(isAvailable: Boolean){
    this.isFocusableInTouchMode = isAvailable
}

/**
 * 소프트 키보드를 띄운다
 */
fun View?.showKeyboardImplicit(delayMills: Long =0) {
    this?.let { view ->
        view.requestFocus()
        view.postDelayed({
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                view,
                InputMethodManager.SHOW_IMPLICIT
            )
        }, delayMills)
    }
}

/**
 * 소프트 키보드를 숨긴다
 */
fun View?.hideKeyboard() {
    this?.let { view ->
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }
}