package kr.co.koscom.omp.enums

import kr.co.koscom.omp.R
import kr.co.koscom.omp.extension.toResString


enum class DrawerEventType(val type: String){
    NONE(""),
    ORDER_STATUS(R.string.drawer_contents_order_status.toResString()),
    CONTRACT_CONCLUSION(R.string.drawer_contents_contract_conclusion.toResString()),
    OVERALL_STATUS(R.string.drawer_contents_overall_status.toResString()),
    ORDER_REGIST(R.string.drawer_contents_order_regist.toResString()),

    BUSINESS_INFO(R.string.drawer_contents_business_info.toResString()),
    EXPERT(R.string.drawer_contents_expert.toResString()),
    BUSINESS_ALARM(R.string.drawer_contents_business_alarm.toResString()),
    DISCUSSION(R.string.drawer_contents_discussion.toResString()),
    NEWS(R.string.drawer_contents_news.toResString()),
    PATENT(R.string.drawer_contents_patent.toResString()),

    SIGNINFO_MANAGEMENT(R.string.drawer_contents_sign_info_management.toResString()),
    MY_ORDER(R.string.drawer_contents_my_order.toResString()),
    MY_NEGO(R.string.drawer_contents_my_nego.toResString()),
    MY_CONTRACT_CONCLUSION(R.string.drawer_contents_my_contract_conclusion.toResString()),
    BALANCE(R.string.drawer_contents_balance.toResString()),
    DOCUMENT_MANAGEMENT(R.string.drawer_contents_document_management.toResString()),
    SERVICE_TERMINATION(R.string.drawer_contents_service_termination.toResString()),
    ALARM(R.string.drawer_contents_alarm.toResString()),

    GONGJI(R.string.drawer_contents_gongji.toResString()),
    SERVICE_INTRODUCE(R.string.drawer_contents_service.toResString()),
    FAQ(R.string.drawer_contents_faq.toResString()),
    ONE_AND_ONE(R.string.drawer_contents_one_and_one.toResString()),

    ALARM_SETTING(R.string.drawer_contents_alarm_setting.toResString()),
    MARKETTING(R.string.drawer_contents_marketting.toResString()),
    VERSION_INFO(R.string.drawer_contents_version_info.toResString()),
    SERVICE_TERMS(R.string.drawer_contents_service_terms.toResString()),
    USERINFO_POLICY(R.string.drawer_contents_userinfo_policy.toResString()),
    COPYRIGHT_PROTECTION_POLICY(R.string.drawer_contents_copyright_protrection_policy.toResString()),
    EASY_LOGIN(R.string.drawer_contents_easy_login.toResString());


    companion object{
        @JvmStatic
        fun getType(type: String?): DrawerEventType{
            if(!type.isNullOrEmpty()){
                for(dealType in values()){
                    if(dealType.type == type){
                        return dealType
                    }
                }
            }
            return NONE
        }
    }
}