package kr.co.koscom.omp.enums


enum class SettingTabType(val type: Int){
    /**
     * 0: 알림 설정 및 마케팅 수신동의
     * 1: 버전정보
     * 2: 서비스 이용약관
     * 3: 개인정보 취급처리방침
     * 4: 저작권 보호방침
     * 5: 간편 로그인
     */
    ALARM(0),
    VERSION(1),
    SERVICE_USETERM(2),
    USERINFO_POLICY(3),
    COPYRIGHT(4),
    EASY_LOGIN(5);

    companion object{
        @JvmStatic
        fun getType(type: Int?): SettingTabType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return ALARM
        }
    }
}