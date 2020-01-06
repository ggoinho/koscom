package kr.co.koscom.omp.view

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Window
import android.widget.TextView

import kr.co.koscom.omp.R

class MyDialog(context: Context, cancelable: Boolean = true, cancelListener: DialogInterface.OnCancelListener? = null) : Dialog(context, cancelable, cancelListener) {

    init {
        initialize()
    }

    var title: TextView? = null
    var message: TextView? = null

    private fun initialize() {
        // 액티비티의 타이틀바를 숨긴다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.activity_progress)

        title = findViewById(R.id.title)
        message = findViewById(R.id.message)
    }
}
