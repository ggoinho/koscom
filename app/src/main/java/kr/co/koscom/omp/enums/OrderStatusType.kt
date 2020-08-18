package kr.co.koscom.omp.enums


enum class OrderStatusType(val type: Int){
    /**
     * NONE: 기본상태
     * WAIT_SELL_NEGO: 협상대기 & 매도
     * WAIT_BUY_NEGO: 협상대기 & 매수
     * NEGOTIATING: 협상진행중
     * NEGOTIATING_NO_REMAIN: 협상진행중 & 잔량 없음
     */
    NONE(0),
    WAIT_SELL_NEGO(1),
    WAIT_BUY_NEGO(2),
    NEGOTIATING(3),
    NEGOTIATING_NO_REMAIN(4);

    companion object{
        @JvmStatic
        fun getType(type: Int?): OrderStatusType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return NONE
        }
    }
}