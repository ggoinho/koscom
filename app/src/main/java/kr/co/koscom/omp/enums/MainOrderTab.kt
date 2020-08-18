package kr.co.koscom.omp.enums


enum class MainOrderTab(val type: Int){
    ORDER_STATUS(0),               //주문 현황
    NEGO_PROGRESS(1),            //나의 협상 진행상황
    CONCLUSION_STATUS(2);        //나의 체결 현황

    companion object{
        @JvmStatic
        fun getType(type: Int?): MainOrderTab{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return ORDER_STATUS
        }
    }
}