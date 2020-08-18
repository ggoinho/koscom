package kr.co.koscom.omp.enums


enum class NegoTiableMyTab(val type: Int){
    NONE(0),            //전체 (아무것도 선택 안함)
    NEGOTIABLE(1),      //협상가능
    MY(2);              //MY

    companion object{
        @JvmStatic
        fun getType(type: Int?): NegoTiableMyTab{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return NONE
        }
    }
}