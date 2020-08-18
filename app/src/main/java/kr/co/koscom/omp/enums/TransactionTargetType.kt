package kr.co.koscom.omp.enums


enum class TransactionTargetType(val type: Int){
    NONE(0),
    SPECIFIC(1),
    UNSPECIFIC(2);

    companion object{
        @JvmStatic
        fun getType(type: Int?): TransactionTargetType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return NONE
        }
    }
}