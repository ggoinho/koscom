package kr.co.koscom.omp

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.sendbird.syncmanager.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_navigation.*
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.enums.*
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.util.ActivityUtil
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                activity?.finish()
            }

        }

        btnClose.setOnClickListener {
            activity?.window?.decorView?.getRootView()?.findViewById<DrawerLayout>(R.id.drawer_layout)?.closeDrawer(GravityCompat.END)
        }

        btnHome.setOnClickListener {
            ActivityUtil.startMainNewActivity(activity!!)
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
        btnServiceInfo!!.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(requireActivity(), CustomerTabType.SERVICE.ordinal)
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
            ActivityUtil.startOrderListActivity(requireActivity(), OrderListTab.ORDER_STATUS.ordinal)
            btnClose.callOnClick()
        }
        btnTrading2.setOnClickListener {
            ActivityUtil.startOrderListActivity(requireActivity(), OrderListTab.CONCLUSION_STATUS.ordinal)
            btnClose.callOnClick()
        }
        btnTrading3.setOnClickListener {
            ActivityUtil.startOrderWriteActivity(requireActivity())
            btnClose.callOnClick()
        }
        btnInvestment1.setOnClickListener {
            ActivityUtil.startInvestmentActivity(requireActivity(), tab = InvestTabType.BUSINESS_INFO.ordinal)
            btnClose.callOnClick()
        }
        btnInvestment2.setOnClickListener {
            ActivityUtil.startInvestmentActivity(requireActivity(), tab = InvestTabType.CORPORATE_PROMOTION.ordinal)
            btnClose.callOnClick()
        }

        btnExpertDiagnosis.setOnClickListener {
            ActivityUtil.startInvestmentActivity(requireActivity(), tab = InvestTabType.EXPERT.ordinal)
            btnClose.callOnClick()
        }

        btnDiscussion.setOnClickListener {
            ActivityUtil.startInvestmentActivity(requireActivity(), tab = InvestTabType.DISCUSSION_BOARD.ordinal)
            btnClose.callOnClick()
        }

        btnNews.setOnClickListener {
            ActivityUtil.startInvestmentActivity(requireActivity(), tab = InvestTabType.NEWS.ordinal)
            btnClose.callOnClick()
        }

        btnPatent.setOnClickListener {
            ActivityUtil.startInvestmentActivity(requireActivity(), tab = InvestTabType.PATENT.ordinal)
            btnClose.callOnClick()
        }

        btnAlarmList.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.ALARM.ordinal)

            btnClose.callOnClick()
        }
        btnRegist.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.SIGN_MANAGEMENT.ordinal)

            btnClose.callOnClick()
        }
        btnMyOrder.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.MY_ORDER.ordinal)

            btnClose.callOnClick()

        }
        btnMyNego.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.MY_NEGO.ordinal)

            btnClose.callOnClick()

        }
        btnMyContractAndConclusion.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.MY_CONTRACT_CONCLUSION.ordinal)

            btnClose.callOnClick()
        }
        btnBalance.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.BALANCE.ordinal)

            btnClose.callOnClick()
        }
        btnFile.setOnClickListener {
            ActivityUtil.startMyPageActivity(activity!!, MyPageTabType.DOCUMENT_MANAGEMENT.ordinal)

            btnClose.callOnClick()

        }
        btnQna.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(requireActivity(), CustomerTabType.ONE_AND_ONE.ordinal)
            btnClose.callOnClick()
        }
        btnOut.setOnClickListener {
            ActivityUtil.startMyPageActivity(requireActivity(), MyPageTabType.SERVICE_TERMINATION.ordinal)
            btnClose.callOnClick()
        }

        btnNotice.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(requireActivity(), CustomerTabType.GONGJI.ordinal)
            btnClose.callOnClick()
        }
        btnFaq.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(requireActivity(), CustomerTabType.FAQ.ordinal)
            btnClose.callOnClick()
        }

        btnAlarm.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.ALARM.ordinal)
            btnClose.callOnClick()
        }
        btnAgree.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.ALARM.ordinal)
            btnClose.callOnClick()
        }
        btnVersion.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.VERSION.ordinal)
            btnClose.callOnClick()
        }
        btnUseAgreement.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.SERVICE_USETERM.ordinal)
            btnClose.callOnClick()

        }
        btnInfoAgreement.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.USERINFO_POLICY.ordinal)
            btnClose.callOnClick()

        }
        btnRightAgreement.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.COPYRIGHT.ordinal)
            btnClose.callOnClick()
        }

        btnEasyLogin.setOnClickListener {
            ActivityUtil.startSettingNewActivity(requireActivity(), SettingTabType.EASY_LOGIN.ordinal)
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
