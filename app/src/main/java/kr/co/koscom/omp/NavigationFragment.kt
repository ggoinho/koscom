package kr.co.koscom.omp

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.sendbird.syncmanager.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_navigation.*
import kr.co.koscom.omp.view.ViewUtils

class NavigationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnExit.setOnClickListener {

            ViewUtils.alertLogoutDialog(activity!!, "로그아웃하시겠습니까?"){
                val intent = Intent(activity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

                activity?.finish()
            }

        }

        btnClose.setOnClickListener {
            activity?.window?.decorView?.getRootView()?.findViewById<DrawerLayout>(R.id.drawer_layout)?.closeDrawer(GravityCompat.END)
        }

        btnHome.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)

            btnClose.callOnClick()
        }

        menu1!!.setOnClickListener {
            if(sub1.visibility != View.VISIBLE){

                openSub(sub1, menu1.findViewWithTag<ImageView>("more"))

            }
            else{
                sub1.visibility = View.GONE
                menu1.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
            }
        }
        menu2!!.setOnClickListener {
            if(sub2.visibility != View.VISIBLE){
                openSub(sub2, menu2.findViewWithTag<ImageView>("more"))
            }
            else{
                sub2.visibility = View.GONE
                menu2.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
            }
        }
        menu3.setOnClickListener {
            var intent = Intent(context, ChatListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            btnClose.callOnClick()
        }
        menu4!!.setOnClickListener {
            if(sub4.visibility != View.VISIBLE){
                openSub(sub4, menu4.findViewWithTag<ImageView>("more"))
            }
            else{
                sub4.visibility = View.GONE
                menu4.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
            }
        }
        menu5!!.setOnClickListener {
            var intent = Intent(activity, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "서비스 소개")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mServiceGuide")
            startActivity(intent)

            btnClose.callOnClick()
        }
        menu6!!.setOnClickListener {
            if(sub6.visibility != View.VISIBLE){
                openSub(sub6, menu6.findViewWithTag<ImageView>("more"))
            }
            else{
                sub6.visibility = View.GONE
                menu6.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
            }
        }
        menu7!!.setOnClickListener {
            if(sub7.visibility != View.VISIBLE){
                openSub(sub7, menu7.findViewWithTag<ImageView>("more"))
            }
            else{
                sub7.visibility = View.GONE
                menu7.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
            }
        }

        btnTrading1.setOnClickListener {

            var intent = Intent(activity, OrderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("gubn",1)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnTrading2.setOnClickListener {

            var intent = Intent(activity, OrderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("gubn",2)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnTrading3.setOnClickListener {

            var intent = Intent(activity, OrderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("gubn",3)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnInvestment1.setOnClickListener {

            val intent = Intent(activity, InvestmentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 0)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnInvestment2.setOnClickListener {

            val intent = Intent(activity, InvestmentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 1)
            startActivity(intent)

            btnClose.callOnClick()
        }

        btnAlarmList.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 0)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnRegist.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 1)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnOrder.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 2)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnFavorite.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 3)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnFile.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 4)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnQna.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 5)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnOut.setOnClickListener {
            val intent = Intent(activity, ServiceOutActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            btnClose.callOnClick()
        }

        btnNotice.setOnClickListener {
            val intent = Intent(activity, CustomerCenterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 0)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnFaq.setOnClickListener {
            val intent = Intent(activity, CustomerCenterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tab", 1)
            startActivity(intent)

            btnClose.callOnClick()
        }

        btnAlarm.setOnClickListener {
            var intent = Intent(activity, SettingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnAgree.setOnClickListener {
            var intent = Intent(activity, SettingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnVersion.setOnClickListener {
            var intent = Intent(activity, SettingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            btnClose.callOnClick()
        }
        btnUseAgreement.setOnClickListener {
            var intent = Intent(activity, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "서비스 이용약관")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mSrvAdminPoc")
            startActivity(intent)

            btnClose.callOnClick()

        }
        btnInfoAgreement.setOnClickListener {
            var intent = Intent(activity, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "개인정보취급처리방침")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mPrivacyPolicy")
            startActivity(intent)

            btnClose.callOnClick()

        }
        btnRightAgreement.setOnClickListener {
            var intent = Intent(activity, WebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("title", "저작권보호방침")
            intent.putExtra("url", BuildConfig.SERVER_URL + "/mobile/common/mCopyrightPolicy")
            startActivity(intent)

            btnClose.callOnClick()
        }

        nicname.text = PreferenceUtils.getUserName()
    }

    private fun openSub(sub: View, more: ImageView){
        sub.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val height = sub.measuredHeight
        Log.d(NavigationFragment::class.simpleName, "sub height : " + height)
        var animator = ValueAnimator.ofInt(0, height)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 200
        animator.addUpdateListener {
            if(it.currentPlayTime == 0L){
                closeAllSub()
                sub.visibility = View.VISIBLE
                more.setImageResource(R.drawable.ico_expand_less)
            }

            val params = sub.layoutParams as LinearLayout.LayoutParams
            params.height = it.animatedValue as Int
            sub.layoutParams = params
        }
        animator.start()
    }

    private fun closeAllSub() {
        sub1.visibility = View.GONE
        menu1.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
        sub2.visibility = View.GONE
        menu2.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
        sub4.visibility = View.GONE
        menu4.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
        sub6.visibility = View.GONE
        menu6.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
        sub7.visibility = View.GONE
        menu7.findViewWithTag<ImageView>("more").setImageResource(R.drawable.ico_expand_more)
    }
}
