package kr.co.koscom.omp.data.model

import kr.co.koscom.omp.enums.NegotiationEnterType
import kr.co.koscom.omp.enums.TransactionTargetType
import java.io.Serializable


class NegotiationData: Serializable {

    var orderNo = ""
    var enterType: NegotiationEnterType = NegotiationEnterType.LIST
    var transactionTargetType: TransactionTargetType = TransactionTargetType.SPECIFIC
    var registNickName = ""
    var stockTpCodeNm = ""

}