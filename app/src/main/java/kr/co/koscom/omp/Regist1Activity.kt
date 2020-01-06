package kr.co.koscom.omp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.android.synthetic.main.activity_regist1.*

// kr.co.koscom.omp
// BeMyUNICORN

class Regist1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_regist1)

        toolbar.initTitle("투자자 서비스 가입")
        toolbar.initData(this)

        chkPerson!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                personLoginWayZone!!.visibility = View.VISIBLE
                companyLoginWayZone!!.visibility = View.GONE
            }
        }
        chkCompany!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                companyLoginWayZone!!.visibility = View.VISIBLE
                personLoginWayZone!!.visibility = View.GONE
            }
        }

        btnLogin!!.setOnClickListener {
            if (chkPerson!!.isChecked) {
                startActivity(Intent(this@Regist1Activity, PersonRegist2Activity::class.java))
            } else {
                startActivity(Intent(this@Regist1Activity, CompanyRegist2Activity::class.java))
            }
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
