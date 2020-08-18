package kr.co.koscom.omp.enums


enum class NegotiationEnterType(val type: Int){
    LIST(0),
    DETAIL(1);

    companion object{
        @JvmStatic
        fun getType(type: Int?): NegotiationEnterType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return LIST
        }
    }
}