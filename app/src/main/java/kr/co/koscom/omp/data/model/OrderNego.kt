package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class OrderNego {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: ResultMap? = null

    class ResultMap{
        var resultCount: Int? = null
        var resultList: List<OrderNegoItem>? = null
    }

    class OrderNegoItem : Serializable{
        var ORDER_NO: String = ""
        var CHANNEL_URL: String = ""
        var POST_TIME: String = ""
        var STK_CODE: String = ""
        var STK_NM: String = ""
        var STOCK_TP_CODE_NM: String = ""
        var STK_NM_FULL: String = ""
        var DEAL_QTY: String = ""
        var DEAL_UPRC: String = ""
        var DEAL_TP: String = ""
        var NEGO_REQST_CLNT_NM: String = ""
        var NEGO_REQST_CLNT_NICKNAME: String = ""
        var NEGO_SETT_STAT_CODE: String = ""
        var RQST_CODE: String = ""
        var REG_DTTM: String = ""
        var CHG_DTTM: String = ""
        var SEC_KIND_DTL_TP_CODE: String = ""
        var CORP_HANGL_NM: String = ""
        var RMQTY: String = ""
        var PBLS_CLNT_NO: String = ""
        var CHANNEL_DEL_YN: String = ""
        var PUBLIC_YN: String = ""
        var CERTI_NUM: String = ""

        var IS_MY_ORDER: String = ""

    }


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}