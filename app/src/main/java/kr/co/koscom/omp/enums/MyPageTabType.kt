package kr.co.koscom.omp.enums


enum class MyPageTabType(val type: Int){
    /**
     * 0: 가입정보관리
     * 1: 나의 주문
     * 2: 나의 협상
     * 3: 나의 계약 및 체결
     * 4: 보유잔고
     * 5: 증빙서류관리
     * 6: 서비스해지
     * 7: 알림수신
     */
    SIGN_MANAGEMENT(0),
    MY_ORDER(1),
    MY_NEGO(2),
    MY_CONTRACT_CONCLUSION(3),
    BALANCE(4),
    DOCUMENT_MANAGEMENT(5),
    SERVICE_TERMINATION(6),
    ALARM(7);

    companion object{
        @JvmStatic
        fun getType(type: Int?): MyPageTabType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return SIGN_MANAGEMENT
        }
    }
}