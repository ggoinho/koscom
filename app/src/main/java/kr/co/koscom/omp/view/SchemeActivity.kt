package kr.co.koscom.omp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.koscom.omp.R
import kr.co.koscom.omp.SplashActivity

class SchemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, SplashActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}
