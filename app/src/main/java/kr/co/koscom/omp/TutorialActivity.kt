package kr.co.koscom.omp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sendbird.syncmanager.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.activity_tutorial.view.*

import kr.co.koscom.omp.view.MyPageIndicator

/**
 * 튜토리얼 화면
 */

class TutorialActivity : AppCompatActivity() {

    private var viewPagerAdapter: ViewPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tutorial)

        viewPagerAdapter = ViewPagerAdapter()
        viewPager!!.adapter = viewPagerAdapter

        val mIndicator =
            MyPageIndicator(this, pagesContainer, viewPager, R.drawable.indicator_circle)
        mIndicator.setPageCount(viewPagerAdapter!!.count)
        mIndicator.show()

        btnLogin!!.setOnClickListener {

            if (checkBox.isChecked){
                PreferenceUtils.setTutorialCheck(true)
            } else {
                PreferenceUtils.setTutorialCheck(false)
            }

            var intent = Intent(this@TutorialActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            finish()
        }
        btnRegist!!.setOnClickListener {

            if (checkBox.isChecked){
                PreferenceUtils.setTutorialCheck(true)
            } else {
                PreferenceUtils.setTutorialCheck(false)
            }

            val intent = Intent(this@TutorialActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("redirect_regist", true)
            startActivity(intent)

            finish()
        }

        checkBox.setOnClickListener {
            if (checkBox.isChecked){
                checkBox.setButtonDrawable(R.drawable.checkbox_w_on)
            } else {
                checkBox.setButtonDrawable(R.drawable.checkbox_w_off)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    internal inner class ViewPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            var view: View? = null

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            if (position == 0)
                view = inflater.inflate(R.layout.page_tutorial1, container, false)
            if (position == 1)
                view = inflater.inflate(R.layout.page_tutorial2, container, false)
            if (position == 2)
                view = inflater.inflate(R.layout.page_tutorial3, container, false)
            if (position == 3)
                view = inflater.inflate(R.layout.page_tutorial4, container, false)

            container.addView(view)

            return view!!
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return 4
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }
    }
}
