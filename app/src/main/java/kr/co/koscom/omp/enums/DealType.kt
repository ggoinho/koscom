package kr.co.koscom.omp.enums


enum class DealType(val type: String){
    ALL(""),
    SELL("10"),         //매도
    BUYING("20");       //매수

    companion object{
        @JvmStatic
        fun getType(type: String?): DealType{
            if(!type.isNullOrEmpty()){
                for(dealType in values()){
                    if(dealType.type == type){
                        return dealType
                    }
                }
            }
            return ALL
        }
    }
}