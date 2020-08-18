package kr.co.koscom.omp.enums


enum class HomeContentsType(val type: Int){
    /**
     * 0: 주문현황
     * 1: 기업정보
     */
    ORDER_SATUS(0),
    BUSINESS_INFO(1);

    companion object{
        @JvmStatic
        fun getType(type: Int?): HomeContentsType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return ORDER_SATUS
        }
    }
}