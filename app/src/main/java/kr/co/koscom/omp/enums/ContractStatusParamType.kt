package kr.co.koscom.omp.enums


enum class ContractStatusParamType(val type: String){
    ALL(""),
    CONTRACT_ONLY("GEYYAK"),         //계약만 나옴
    CONCLUSION_ONLY("CHEGYEOL");       //체결만 나옴

    companion object{
        @JvmStatic
        fun getType(type: String?): ContractStatusParamType{
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