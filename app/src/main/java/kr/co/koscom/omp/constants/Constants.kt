package kr.co.koscom.omp.constants

import kr.co.koscom.omp.BuildConfig

object Constants {

    const val pushMessageReceived = "pushMessageReceived"


    //서비스 이용약관
    const val URL_POLICY_USETERMS = BuildConfig.SERVER_URL + "/mobile/common/mSrvAdminPoc"
    //개인정보취급처리방침
    const val URL_POLICY_USERINFO = BuildConfig.SERVER_URL + "/mobile/common/mPrivacyPolicy"
    //저작권보호방침
    const val URL_POLICY_COPYRIGHT = BuildConfig.SERVER_URL + "/mobile/common/mCopyrightPolicy"
    //나의 협상 진행 상황
    const val URL_MY_NEGO_PROGRESS = BuildConfig.SERVER_URL + "/mobile/mypage/negoStatusPopupAPP"

    //기업정보
    const val URL_BUSINESS_INFO = BuildConfig.SERVER_URL + "/mobile/corpInfo/infoMng"
    //전문가진단
    const val URL_EXPERT_DIAGNOSIS = BuildConfig.SERVER_URL + "/mobile/corpInfo/profesnl"
    //기업알림 및 홍보자료
    const val URL_CORPORATE_PROMOTION = BuildConfig.SERVER_URL + "/mobile/corpInfo/dclsr"
    //토론계시판
    const val URL_DISCUSSION_BOARD = BuildConfig.SERVER_URL + "/mobile/corpInfo/discussionBoard"
    //뉴스
    const val URL_NEWS = BuildConfig.SERVER_URL + "/mobile/corpInfo/news"
    //특허
    const val URL_PATENT = BuildConfig.SERVER_URL + "/mobile/corpInfo/patent"
    //1:1문의
    const val URL_ONE_AND_ONE_QUESTION = BuildConfig.SERVER_URL + "/mobile/mypage/qaLst"

    //공지사항
    const val URL_GONGJI = BuildConfig.SERVER_URL + "/mobile/common/mClmtCntrLst"
    //서비스소개
    const val URL_SERVICE_GUIDE = BuildConfig.SERVER_URL + "/mobile/common/mServiceGuide"
    //FAQ
    const val URL_FAQ = BuildConfig.SERVER_URL + "/mobile/common/mClmtCntrFaqLst"

    //주문상세
    const val URL_ORDER_DETAIL = BuildConfig.SERVER_URL + "/mobile/invst/orderDetails"
    //종목체결
    const val URL_STOCK_SUC_INFO = BuildConfig.SERVER_URL + "/mobile/invst/stockSucInfo"
    //주문체결
    const val URL_ORDER_SUC_INFO = BuildConfig.SERVER_URL + "/mobile/invst/orderSucInfo"
    //전문가정보
    const val URL_EXPERT_INFO = BuildConfig.SERVER_URL + "/mobile/invst/infoDetail2"
    //주문상세 토론게시판
    const val URL_ORDERINFO_DISCUSSION_BOARD = BuildConfig.SERVER_URL + "/mobile/invst/infoDetail5"






    const val RESULT_CODE_ORDER_SEARCH = 100
    const val RESULT_CODE_DRAWER_LAYOUT = 101















}