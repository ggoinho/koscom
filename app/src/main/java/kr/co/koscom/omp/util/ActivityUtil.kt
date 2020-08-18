package kr.co.koscom.omp.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import kr.co.koscom.omp.*
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.model.NegotiationData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.enums.*
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.ui.drawerlayout.DrawerLayoutActivity
import kr.co.koscom.omp.ui.drawerlayout.quick.QuickMenuActivity
import kr.co.koscom.omp.ui.main.MainNewActivity
import kr.co.koscom.omp.ui.order.OrderDetailNewActivity
import kr.co.koscom.omp.ui.order.OrderListNewActivity
import kr.co.koscom.omp.ui.order.OrderRegistActivity
import kr.co.koscom.omp.ui.popup.OrderPopupNewActivity
import java.io.Serializable


object ActivityUtil {

    private var duplicateClickTime = 0L
    private fun isDuplicateClick(): Boolean{
        val currentClickTime = System.currentTimeMillis()
        val elapsedTime = currentClickTime - duplicateClickTime
        duplicateClickTime = currentClickTime
        // 중복 클릭인 경우s
        return elapsedTime <= 900L
    }


    private fun setTransitionAnimation(activity: Activity, transition: ActivityTransition) {
        when (transition) {
            ActivityTransition.IN_FROM_BOTTOM -> setInFromBottomTransition(activity)
            ActivityTransition.OUT_TO_BOTTOM -> setOutToBottomTransition(activity)
            ActivityTransition.IN_FROM_RIGHT -> setInFromRightTransition(activity)
            ActivityTransition.OUT_TO_RIGHT -> setOutToRightTransition(activity)
            ActivityTransition.FADE -> setFadeTransition(activity)
            else -> {
            }
        }
    }

    private fun setOutToBottomTransition(activity: Activity) {
        activity.overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)
    }

    private fun setInFromBottomTransition(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_from_bottom, android.R.anim.fade_out)
    }

    private fun setInFromRightTransition(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_from_right, android.R.anim.fade_out)
    }

    private fun setOutToRightTransition(activity: Activity) {
        activity.overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
    }

    private fun setFadeTransition(activity: Activity) {
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    private fun startRootActivity(activity: Activity, intent: Intent, transition: ActivityTransition = ActivityTransition.IN_FROM_BOTTOM) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
        setTransitionAnimation(activity, transition)
    }

    private fun startActivity(activity: Activity, intent: Intent, transition: ActivityTransition) {
        activity.startActivity(intent)
        setTransitionAnimation(activity, transition)
    }

    private fun startActivityForResult(activity: Activity, fragment: Fragment?, intent: Intent, requestCode: Int, transition: ActivityTransition) {
        fragment?.let {it
            it.startActivityForResult(intent, requestCode)
        }?:activity.startActivityForResult(intent, requestCode)
        setTransitionAnimation(activity, transition)
    }

    /**
     * MainNewActivity
     */
    fun startClearMainNewActivity(activity: Activity){
        if(isDuplicateClick()) return

        Intent(activity, MainNewActivity::class.java).apply{
            startRootActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * MainNewActivity
     */
    fun startMainNewActivity(activity: Activity){
        if(isDuplicateClick()) return

        Intent(activity, MainNewActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * LoginActivity
     */
    fun startCleanLoginActivity(activity: Activity){
        if(isDuplicateClick()) return

        Intent(activity, LoginActivity::class.java).apply{
            startRootActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * LoginActivity
     */
    fun startLoginActivity(activity: Activity, redirectRegist: Boolean? = false, isClose: Boolean? = false){
        Intent(activity, LoginActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_REDIRECT_REGIST, redirectRegist)
            putExtra(Keys.INTENT_IS_CLOSE_BTN, isClose)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * MyPageActivity
     */
    fun startMyPageActivity(activity: Activity, tab: Int? = 0){
        if(isDuplicateClick()) return

        Intent(activity, MyPageActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_TAB, tab)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * CustomerCenterActivity
     */
    fun startCustomerCenterActivity(activity: Activity, tab: Int = 0){
        if(isDuplicateClick()) return

        Intent(activity, CustomerCenterActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_TAB, tab)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * ClassForName
     */
    fun startClassForName(activity: Activity, screen: String, tab: Int? = 0, message: String? = ""){
        if(isDuplicateClick()) return

        Intent(activity, Class.forName(screen)).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_TAB, tab)
            putExtra(Keys.INTENT_MESSAGE, message)

            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * InvestmentActivity
     */
    fun startInvestmentActivity(activity: Activity, isFirstMain: Boolean = false, tab: Int = 0, entpNo: String = ""){

        Intent(activity, InvestmentActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_ISFIRSTMAIN, isFirstMain)
            putExtra(Keys.INTENT_TAB, tab)
            putExtra(Keys.INTENT_ENTP_NO_TOP, entpNo)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * WebActivity
     */
    fun startWebActivity(activity: Activity, title: String = "", url: String = ""){
        if(isDuplicateClick()) return

        Intent(activity, WebActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_TITLE, title)
            putExtra(Keys.INTENT_URL, url)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * ContractDetailActivity
     */
    fun startContractDetailActivity(activity: Activity, groupChannelTitle: String? = "", groupChannelUrl: String? = "", orderNo: String? = ""){
        if(isDuplicateClick()) return

        Intent(activity, ContractDetailActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_GROUPCHANNEL_TITLE, groupChannelTitle)
            putExtra(Keys.INTENT_GROUPCHANNEL_URL, groupChannelUrl)
            putExtra(Keys.INTENT_ORDER_NO, orderNo)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * OrderDetailActivity
     */
    fun startOrderDetailActivity(activity: Activity, data: Order.OrderItem){
        if(isDuplicateClick()) return

        Intent(activity, OrderDetailNewActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_ORDER_ITEM, data)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * OrderPopupActivity
     */
    fun startOrderPopupActivity(activity: Activity, data: Order.OrderItem){
        if(isDuplicateClick()) return

        Intent(activity, OrderPopupNewActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_ORDER_ITEM, data)
            startActivity(activity, this, ActivityTransition.FADE)
        }
    }

    /**
     * OrderListActivity
     * gubn 1: 매수, 2: 매도
     */
    fun startOrderListActivity(activity: Activity, gubn: Int = OrderListTab.ORDER_STATUS.ordinal){
        if(isDuplicateClick()) return

        Intent(activity, OrderListNewActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_GUBN, gubn)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * ContractPopupActivity
     */
    fun startContractPopupActivity(activity: Activity, data: OrderContract.OrderContractItem){
        if(isDuplicateClick()) return

        Intent(activity, ContractPopupActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_CONTRACT_ITEM, data)
            startActivity(activity, this, ActivityTransition.IN_FROM_BOTTOM)
        }
    }

    /**
     * OrderRegistActivity
     */
    fun startOrderRegistActivity(activity: Activity, stock: Serializable? = null){
        if(isDuplicateClick()) return

        Intent(activity, OrderRegistActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            stock?.let {
                putExtra(Keys.INTENT_STOCK, it)
            }
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * OrderWriteActivity
     */
    fun startOrderWriteActivity(activity: Activity, stock: Serializable? = null, isPublicYn: Boolean? = true, passwordContents: String? = ""){
        if(isDuplicateClick()) return

        Intent(activity, OrderWriteActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            stock?.let {
                putExtra(Keys.INTENT_STOCK, it)
            }
            putExtra(Keys.INTENT_IS_PUBLIC_YN, isPublicYn)
            putExtra(Keys.INTENT_PASSWORD_CONTENTS, passwordContents)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * BuyWriteActivity
     */
    fun startBuyWriteActivity(activity: Activity, stock: Serializable? = null, isPublicYn: Boolean? = true, passwordContents: String? = ""){
        if(isDuplicateClick()) return

        Intent(activity, BuyWriteActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            stock?.let {
                putExtra(Keys.INTENT_STOCK, it)
            }
            putExtra(Keys.INTENT_IS_PUBLIC_YN, isPublicYn)
            putExtra(Keys.INTENT_PASSWORD_CONTENTS, passwordContents)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * SettingNewActivity
     */
    fun startSettingNewActivity(activity: Activity, tab: Int = 0){
        if(isDuplicateClick()) return

        Intent(activity, SettingNewActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_TAB, tab)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * RegistActivity
     */
    fun startRegistActivity(activity: Activity){
        Intent(activity, RegistActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * SignLaunchActivity
     */
    fun startSignLaunchActivity(activity: Activity){
        if(isDuplicateClick()) return

        Intent(activity, SignLaunchActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * TutorialActivity
     */
    fun startTutorialActivity(activity: Activity){

        Intent(activity, TutorialActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * PersonRegist2Activity
     */
    fun startPersonRegist2Activity(activity: Activity){

        Intent(activity, PersonRegist2Activity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * PersonRegist3Activity
     */
    fun startPersonRegist3Activity(activity: Activity){

        Intent(activity, PersonRegist3Activity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * CompanyRegist2Activity
     */
    fun startCompanyRegist2Activity(activity: Activity){

        Intent(activity, CompanyRegist2Activity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }



    /**
     * SearchActivity
     */
    fun startSearchActivityResult(activity: Activity, requestCode: Int){

        Intent(activity, SearchActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(activity, null, this, requestCode, ActivityTransition.IN_FROM_BOTTOM)
        }
    }

    /**
     * Search2Activity
     */
    fun startSearch2ActivityResult(activity: Activity, requestCode: Int){

        Intent(activity, Search2Activity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(activity, null, this, requestCode, ActivityTransition.IN_FROM_BOTTOM)
        }
    }


    /**
     * NegotiationPopupActivity
     */
    fun startNegotiationPopupActivity(activity: Activity, negotiationData: NegotiationData){
        if(isDuplicateClick()) return

        Intent(activity, NegotiationPopupActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_NEGOTIATION_DATA, negotiationData)
            startActivity(activity, this, ActivityTransition.IN_FROM_BOTTOM)
        }
    }


    /**
     * ChatListActivity
     */
    fun startChatListActivity(activity: Activity, channelUrl: String = "", channelTitle: String = "", orderNo: String = "", loginId: String = "", detailMove: Boolean = false){
        if(isDuplicateClick()) return

        Intent(activity, ChatListActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_GROUPCHANNEL_URL, channelUrl)
            putExtra(Keys.INTENT_GROUPCHANNEL_TITLE, channelTitle)
            putExtra(Keys.INTENT_ORDER_NO, orderNo)
            putExtra(Keys.INTENT_LOGIN_ID, loginId)
            putExtra(Keys.INTENT_DETAIL_MOVE, detailMove)
            startActivity(activity, this, ActivityTransition.IN_FROM_BOTTOM)
        }
    }


    /**
     * OrderSearchActivity
     */
    fun startOrderSearchActivityResult(activity: Activity, requestCode: Int){
        if(isDuplicateClick()) return

        Intent(activity, OrderSearchActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(activity, null, this, requestCode, ActivityTransition.IN_FROM_BOTTOM)
        }
    }

    /**
     * DrawerLayoutActivity
     */
    fun startDrawerLayoutActivityResult(activity: Activity, requestCode: Int){
        if(isDuplicateClick()) return

        Intent(activity, DrawerLayoutActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(activity, null, this, requestCode, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * DrawerType Activity
     * 주문현황
     * 계약 및 체결현황
     * 종합현황
     * 주문등록
     *
     * 기업정보
     * 전문가진단
     * 기업알림 및 홍보자료
     * 토론게시판
     * 뉴스
     * 특허
     *
     * 가입정보관리
     * 나의 주문
     * 나의 협상
     * 나의 계약 및 체결
     * 보유잔고
     * 증빙서류관리
     * 서비스해지
     * 알림수신함
     *
     * 공지사항
     * 서비스소개
     * FAQ
     * 1:1문의
     *
     * 알림설정
     * 마케팅수신동의
     * 버전정보
     * 서비스이용약관
     * 개인정보취급처리방침
     * 저작권보호방침
     * 간편로그인
     */
    fun startDrawerType(activity: Activity, drawerTitle: String){

        LogUtil.e("drawerType : ${DrawerEventType.getType(drawerTitle)}")

        when(DrawerEventType.getType(drawerTitle)){
            DrawerEventType.ORDER_STATUS -> {
                startOrderListActivity(activity, OrderListTab.ORDER_STATUS.ordinal)
            }
            DrawerEventType.CONTRACT_CONCLUSION-> {
                startOrderListActivity(activity, OrderListTab.CONCLUSION_STATUS.ordinal)
            }
            DrawerEventType.OVERALL_STATUS-> {

            }
            DrawerEventType.ORDER_REGIST-> {
                startOrderRegistActivity(activity)
            }
            DrawerEventType.BUSINESS_INFO-> {
                startInvestmentActivity(activity, tab = InvestTabType.BUSINESS_INFO.ordinal)

            }
            DrawerEventType.EXPERT-> {
                startInvestmentActivity(activity, tab = InvestTabType.EXPERT.ordinal)
            }
            DrawerEventType.BUSINESS_ALARM-> {
                startInvestmentActivity(activity, tab = InvestTabType.CORPORATE_PROMOTION.ordinal)

            }
            DrawerEventType.DISCUSSION-> {
                startInvestmentActivity(activity, tab = InvestTabType.DISCUSSION_BOARD.ordinal)
            }
            DrawerEventType.NEWS-> {
                startInvestmentActivity(activity, tab = InvestTabType.NEWS.ordinal)
            }
            DrawerEventType.PATENT-> {
                startInvestmentActivity(activity, tab = InvestTabType.PATENT.ordinal)
            }
            DrawerEventType.SIGNINFO_MANAGEMENT->{
                startMyPageActivity(activity, MyPageTabType.SIGN_MANAGEMENT.ordinal)
            }
            DrawerEventType.MY_ORDER-> {
                startMyPageActivity(activity, MyPageTabType.MY_ORDER.ordinal)

            }
            DrawerEventType.MY_NEGO-> {
                startMyPageActivity(activity, MyPageTabType.MY_NEGO.ordinal)
            }
            DrawerEventType.MY_CONTRACT_CONCLUSION-> {
                startMyPageActivity(activity, MyPageTabType.MY_CONTRACT_CONCLUSION.ordinal)
            }
            DrawerEventType.BALANCE-> {
                startMyPageActivity(activity, MyPageTabType.BALANCE.ordinal)
            }
            DrawerEventType.DOCUMENT_MANAGEMENT-> {
                startMyPageActivity(activity, MyPageTabType.DOCUMENT_MANAGEMENT.ordinal)
            }
            DrawerEventType.SERVICE_TERMINATION-> {
                startMyPageActivity(activity, MyPageTabType.SERVICE_TERMINATION.ordinal)
            }
            DrawerEventType.ALARM-> {
                startMyPageActivity(activity, MyPageTabType.ALARM.ordinal)
            }
            DrawerEventType.GONGJI-> {
                startCustomerCenterActivity(activity, CustomerTabType.GONGJI.ordinal)
            }
            DrawerEventType.SERVICE_INTRODUCE-> {
                startCustomerCenterActivity(activity, CustomerTabType.SERVICE.ordinal)
            }
            DrawerEventType.FAQ-> {
                startCustomerCenterActivity(activity, CustomerTabType.FAQ.ordinal)
            }
            DrawerEventType.ONE_AND_ONE-> {
                startCustomerCenterActivity(activity, CustomerTabType.ONE_AND_ONE.ordinal)
            }
            DrawerEventType.ALARM_SETTING-> {
                startSettingNewActivity(activity, SettingTabType.ALARM.ordinal)
            }
            DrawerEventType.MARKETTING-> {
                startSettingNewActivity(activity, SettingTabType.ALARM.ordinal)
            }
            DrawerEventType.VERSION_INFO-> {
                startSettingNewActivity(activity, SettingTabType.VERSION.ordinal)
            }
            DrawerEventType.SERVICE_TERMS-> {
                startSettingNewActivity(activity, SettingTabType.SERVICE_USETERM.ordinal)
            }
            DrawerEventType.USERINFO_POLICY-> {
                startSettingNewActivity(activity, SettingTabType.USERINFO_POLICY.ordinal)
            }
            DrawerEventType.COPYRIGHT_PROTECTION_POLICY-> {
                startSettingNewActivity(activity, SettingTabType.COPYRIGHT.ordinal)
            }
            DrawerEventType.EASY_LOGIN-> {
                startSettingNewActivity(activity, SettingTabType.EASY_LOGIN.ordinal)
            }
        }
    }




    /**
     * AgreementActivity
     */
    fun startAgreementActivity(activity: Activity){
        if(isDuplicateClick()) return

        Intent(activity, AgreementActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }

    /**
     * GroupChannelActivity
     */
    fun startGroupChannelActivity(activity: Activity, groupChannelUrl: String = "", groupChannelTitle: String = "", orderNo: String = ""){
        if(isDuplicateClick()) return

        Intent(activity, GroupChannelActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Keys.INTENT_GROUPCHANNEL_URL, groupChannelUrl)
            putExtra(Keys.INTENT_GROUPCHANNEL_TITLE, groupChannelTitle)
            putExtra(Keys.INTENT_ORDER_NO, orderNo)
            startActivity(activity, this, ActivityTransition.IN_FROM_RIGHT)
        }
    }


    /**
     * MarketSearch
     */
    fun startMarketSearch(activity: Activity, search: String = ""){
        if(isDuplicateClick()) return
        try {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://search?q=$search"))
            )
        } catch (n: ActivityNotFoundException) {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/search?q=$search"))
            )
        }
    }


    /**
     * QuickMenuActivity
     */
    fun startQuickMenuActivity(activity: Activity){
        if(isDuplicateClick()) return

        Intent(activity, QuickMenuActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(activity, this, ActivityTransition.IN_FROM_BOTTOM)
        }
    }



}