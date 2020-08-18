package kr.co.koscom.omp.enums


enum class NegoStatusType(val type: String){
    NULL(""),
    NONE("none"),
    NEGO_WAIT("100"),                //협상대기
    BREAKDOWN("105"),                //결렬
    AUTO_BREAKDOWN("106"),           //자동결렬
    REQUEST_CANCEL("107"),           //요청취소
    CONTRACT_CANCEL("207"),          //계약취소
    COMPLETE("406");                 //완료


    companion object{
        @JvmStatic
        fun getType(type: String?): NegoStatusType{
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