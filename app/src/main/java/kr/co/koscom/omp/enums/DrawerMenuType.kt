package kr.co.koscom.omp.enums


enum class DrawerMenuType(val type: Int){
    /**
     * 0: 주문
     * 1: 종목정보
     * 2: My Page
     * 3: 고객센터
     * 4: 설정
     */
    ORDER(0),
    STOCK_INFO(1),
    MYPAGE(2),
    CUSTOMER_CENTER(3),
    SETTING(4);

    companion object{
        @JvmStatic
        fun getType(type: Int?): DrawerMenuType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return ORDER
        }
    }
}