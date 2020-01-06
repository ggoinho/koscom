package kr.co.koscom.omp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.android.synthetic.main.activity_person_regist2.*

// kr.co.koscom.omp
// BeMyUNICORN

class PersonRegist2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_person_regist2)

        toolbar.initTitle("투자자 서비스 가입")
        toolbar.initData(this)

        btnViewAgreement1!!.setOnClickListener {
            startActivity(
                Intent(
                    this@PersonRegist2Activity,
                    AgreementActivity::class.java
                )
            )
        }
        btnViewAgreement2!!.setOnClickListener {
            startActivity(
                Intent(
                    this@PersonRegist2Activity,
                    AgreementActivity::class.java
                )
            )
        }
        btnAgreeNot!!.setOnClickListener { finish() }
        btnAgree!!.setOnClickListener {
            startActivity(Intent(this@PersonRegist2Activity, PersonRegist3Activity::class.java))
            finish()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

        toolbar.dispose()
    }
}
