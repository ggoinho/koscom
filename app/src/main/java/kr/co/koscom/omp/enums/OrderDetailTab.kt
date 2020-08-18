package kr.co.koscom.omp.enums


enum class OrderDetailTab(val type: Int){
    /**
     * 0: 주문상세
     * 1: 종목체결
     * 2: 주문체결
     * 3: 전문가정보
     * 4: 토론게시판
     */
    ORDER_DETAIL(0),
    STOCK_CONCLUSION(1),
    ORDER_CONCLUSION(2),
    EXPERT_INFO(3),
    DISCUSSION_BOARD(4);

    companion object{
        @JvmStatic
        fun getType(type: Int?): OrderDetailTab{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return ORDER_DETAIL
        }
    }
}