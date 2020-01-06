package kr.co.koscom.omp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.android.synthetic.main.activity_person_regist3.*

// kr.co.koscom.omp
// BeMyUNICORN

class PersonRegist3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_person_regist3)

        toolbar.initTitle("투자자 서비스 가입")
        toolbar.initData(this)

        btnCancel!!.setOnClickListener { finish() }
        btnApply!!.setOnClickListener {
            startActivity(Intent(this@PersonRegist3Activity, MainActivity::class.java))
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
