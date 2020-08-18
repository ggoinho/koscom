package kr.co.koscom.omp.custom

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_custom_search.view.*
import kr.co.koscom.omp.R
import java.io.UnsupportedEncodingException

class CustomSearchView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var stringText: String = ""
    var isClearVisible: Boolean = true
    private var isSearching: Boolean = false

    private var limitLength = 20

    private var onEditTextListener: OnEditTextListener? = null

    init {
        initialize()
    }

    private fun initialize() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_custom_search, this, true)
    }

    fun initListener() {

        etSearch.setOnEditorActionListener(onEditorActionListener)
        etSearch.addTextChangedListener(textWatcher)

        btnSearchDelete.setOnClickListener {
            etSearch.setText("")
            onEditTextListener?.onActionClear(this@CustomSearchView)
        }

        btnSearch.setOnClickListener {
            isSearching = true
            onEditTextListener?.onActionSearch(this@CustomSearchView)
            (etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(etSearch.windowToken, 0)

            setSearchEnd()
        }

        etSearch.setOnClickListener {
            layoutSearch.isSelected = true
            if(!isSearching){
                onEditTextListener?.onOptionClick(this@CustomSearchView, true)
            }else{
                onEditTextListener?.onOptionClick(this@CustomSearchView, false)
            }

        }

//        etSearch.setOnFocusChangeListener { v, hasFocus ->
//            if(!isSearching && hasFocus){
//                onEditTextListener?.onOptionClick(this@CustomSearchView, true)
//            }else{
//                onEditTextListener?.onOptionClick(this@CustomSearchView, false)
//            }
//            layoutSearch.isSelected = hasFocus
//        }
    }

    /**
     * 입력 가능 여부
     */
    fun setInputAvailable(isAvailable: Boolean){
        etSearch.isFocusableInTouchMode = isAvailable
    }

    fun getEtSearch(): EditText{
        return etSearch
    }

    fun setonEditTextListener(listener: OnEditTextListener) {
        onEditTextListener = listener
    }

    fun getSearchTextString(): String {
        return etSearch.text.toString()
    }

    fun setText(data: String) {
        etSearch.setText(data)
    }

    fun setHint(data: String){
        etSearch.hint = data
    }

    fun setTextAndSearch(data: String) {
        etSearch.setText(data)
        onEditTextListener?.onActionSearch(this@CustomSearchView)
        (etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(etSearch.windowToken, 0)
        layoutSearch.isSelected = false
    }

    private fun setSearchEnd(){
        Handler().postDelayed({
            this.isSearching = false
            layoutSearch.isSelected = false
        }, 30)
    }


    /**************************************************
     * TextView.OnEditorActionListener
     */
    private var onEditorActionListener: TextView.OnEditorActionListener = TextView.OnEditorActionListener { v, actionId, event ->
        when (actionId) {
            EditorInfo.IME_ACTION_NEXT -> {
                onEditTextListener?.onImeActionNext(this@CustomSearchView)
                return@OnEditorActionListener true
            }
            EditorInfo.IME_ACTION_DONE -> (etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(etSearch.windowToken, 0)
            EditorInfo.IME_ACTION_SEARCH -> {
                isSearching = true
                onEditTextListener?.onActionSearch(this@CustomSearchView)
                (etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(etSearch.windowToken, 0)

                setSearchEnd()
            }
        }
        false
    }

    /**************************************************
     * EditText - TextWatcher
     */
    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            stringText = s.toString()

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isNotEmpty() && isClearVisible) {
                btnSearchDelete.visibility = View.VISIBLE
            } else {
                btnSearchDelete.visibility = View.GONE
            }
        }

        override fun afterTextChanged(s: Editable) {
            onEditTextListener?.onValueChanged(this@CustomSearchView)

//            if (limitLength > 0) {
//                if (limitLength < getByteLength(s.toString())) {
//                    if (limitLength < getByteLength(stringText))
//                        stringText = KonaStringUtil.subStringBytes(stringText, limitLength)
//
//                    etSearch.setText(stringText)
//                    etSearch.setSelection(stringText.length)
//                } else {
                    stringText = s.toString()
//                }
//            }
        }
    }


    /**
     * 문자열 바이트 길이
     *
     * @param str 문자열
     * @return Length
     */
    private fun getByteLength(str: String): Int {
        try {
            return str.toByteArray(charset("KSC5601")).size
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return 0
    }


    /**************************************************
     * OnEditTextListener
     */
    interface OnEditTextListener {
        fun onImeActionNext(v: CustomSearchView)

        fun onValueChanged(v: CustomSearchView)

        fun onActionSearch(v: CustomSearchView)

        fun onActionClear(v: CustomSearchView)

        fun onOptionClick(v: CustomSearchView, data: Any?)

    }

}
