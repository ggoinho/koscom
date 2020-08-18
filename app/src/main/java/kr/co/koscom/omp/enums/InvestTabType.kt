package kr.co.koscom.omp.enums


enum class InvestTabType(val type: Int){
    /**
     * 0: 기업정보
     * 1: 전문가진단
     * 2: 기업알림/홍보/평가자료
     * 3: 토론게시판
     * 4: 뉴스
     * 5: 특허
     */
    BUSINESS_INFO(0),
    EXPERT(1),
    CORPORATE_PROMOTION(2),
    DISCUSSION_BOARD(3),
    NEWS(4),
    PATENT(5);

    companion object{
        @JvmStatic
        fun getType(type: Int?): InvestTabType{
            for(dealType in values()){
                if(dealType.type == type){
                    return dealType
                }
            }
            return BUSINESS_INFO
        }
    }
}