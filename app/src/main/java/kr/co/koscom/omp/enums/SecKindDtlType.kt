package kr.co.koscom.omp.enums


enum class SecKindDtlType(val type: String){
    NONE(""),
    REPAYMENT("02"),
    CONVERSION_PAYMENT("03");

    companion object{
        @JvmStatic
        fun getType(type: String?): SecKindDtlType{
            if(!type.isNullOrEmpty()){
                for(dealType in values()){
                    if(dealType.type == type){
                        return dealType
                    }
                }
            }
            return NONE
        }
    }
}