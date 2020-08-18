package kr.co.koscom.omp.enums


enum class MenuLocationType(val type: Int){
    /**
     * 0: 왼쪽
     * 1: 오른쪽
     */
    MENU_LEFT(0),
    MENU_RIGHT(1);

    companion object{
        @JvmStatic
        fun getType(type: Int?): MenuLocationType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return MENU_LEFT
        }
    }
}