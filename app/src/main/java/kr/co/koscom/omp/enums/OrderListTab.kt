package kr.co.koscom.omp.enums


enum class OrderListTab(val type: Int){
    ORDER_STATUS(0),               //주문상세
    CONCLUSION_STATUS(1);        //나의 체결 현황

    companion object{
        @JvmStatic
        fun getType(type: Int?): OrderListTab{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return ORDER_STATUS
        }
    }
}