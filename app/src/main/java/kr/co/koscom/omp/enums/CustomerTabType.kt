package kr.co.koscom.omp.enums


enum class CustomerTabType(val type: Int){
    /**
     * 0: 공지사항
     * 1: 서비스소개
     * 2: FAQ
     * 3: 1:1문
     */
    GONGJI(0),
    SERVICE(1),
    FAQ(2),
    ONE_AND_ONE(3);

    companion object{
        @JvmStatic
        fun getType(type: Int?): CustomerTabType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return GONGJI
        }
    }
}