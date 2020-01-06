package kr.co.koscom.omp.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import android.text.Selection.getSelectionStart
import android.util.Log


class NumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : EditText(context, attrs, defStyleAttr), TextWatcher {

    var isEdiging: Boolean = false

    override fun afterTextChanged(s: Editable?) {
        Log.d(NumberEditText::class.simpleName, "afterTextChanged isEdiging : " + isEdiging)

        if (isEdiging) return

        isEdiging = true

        var inilen = getText().length
        val cp = getSelectionStart()

        val str = s.toString().replace("[^\\d]", "")
        var s1 = 0.0
        try {
            s1 = java.lang.Double.parseDouble(str)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        val nf2 = NumberFormat.getInstance(Locale.ENGLISH)
        (nf2 as DecimalFormat).applyPattern("###,###.###")
        s!!.replace(0, s!!.length, nf2.format(s1))

        if (s.toString().equals("0")) {
            setText("")
        }

        var endlen = getText().length
        val sel = cp + (endlen - inilen)

        if (sel > 0 && sel <= getText().length) {
            setSelection(sel);
            Log.d(NumberEditText::class.simpleName, "setSelection($sel)")
        } else {
            var sel2 = getText().length - 1
            setSelection(sel2);
            Log.d(NumberEditText::class.simpleName, "setSelection($sel2)")
        }

        isEdiging = false
    }
    override fun onTextChanged(text: CharSequence?,start: Int,lengthBefore: Int,lengthAfter: Int) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}