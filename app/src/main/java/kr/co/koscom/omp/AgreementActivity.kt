package kr.co.koscom.omp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.android.synthetic.main.activity_agreement.*

// kr.co.koscom.omp
// BeMyUNICORN

class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_agreement)

        btnOk!!.setOnClickListener { finish() }
        btnClose!!.setOnClickListener { finish() }
 

    }
}
