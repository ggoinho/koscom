package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Nego {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: ResultMap? = null

    class ResultMap{
        var myContList: List<ContractItem>? = null
        var myNegoList: List<NegoItem>? = null
    }

    class ContractItem: Serializable{
        var CORP_HANGL_NM: String? = null
        var STOCK_TP_CODE_NM: String? = null
        var SEC_KIND_DTL_TP_CODE: String? = null
        var DEAL_UPRC: String? = null
        var DEAL_QTY: String? = null
        var DEAL_TP: String? = null
        var STAT_FLAG: String? = null
        var NEGO_REQST_CLNT_NM: String? = null
        var ORDER_NO: String? = null
        var CHANNEL_URL: String? = null
        var CONT_FLAG: String? = null
        var ESCR_FLAG: String? = null
    }
    
    class NegoItem: Serializable{
        var CORP_HANGL_NM: String? = null
        var STOCK_TP_CODE_NM: String? = null
        var SEC_KIND_DTL_TP_CODE: String? = null
        var DEAL_UPRC: String? = null
        var DEAL_QTY: String? = null
        var DEAL_TP: String? = null
        var NEGO_SETT_STAT_CODE: String? = null
        var NEGO_REQST_CLNT_NM: String? = null
        var RQST_CODE: String? = null
        var ORDER_NO: String? = null
        var CHANNEL_URL: String? = null
    }
    

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}