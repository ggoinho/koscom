package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class OrderContract {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: ResultMap? = null

    class ResultMap{
        var resultCount: Int? = null
        var resultList: List<OrderContractItem>? = null
    }

    class OrderContractItem : Serializable{
        var FROM_DATE: String? = null
        var REG_DTTM: String? = null
        var TO_DATE: String? = null
        var MAESU: String? = null
        var ORDER_NO: String? = null
        var MAESU_NM: String? = null
        var CHANNEL_URL: String? = null
        var CVNT_DATE: String? = null
        var startLimit: Int? = null
        var CHG_ID: String? = null
        var pageNo: String? = null
        var DEAL_UPRC: Long? = null
        var REG_ID: String? = null
        var STOCK_TP_CODE_NM: String? = null
        var negoSettStat: String? = null
        var CORP_HANGL_NM: String? = null
        var STK_CODE: String? = null
        var NEGO_SETT_STAT: String? = null
        var LOGIN_ID: String? = null
        var DEAL_QTY: Int? = null
        var MAEDO: String? = null
        var toDate: String? = null
        var SEC_KIND_DTL_TP_CODE: String? = null
        var endLimit: Int? = null
        var fromDate: String? = null
        var DEAL_TP: String? = null
        var STK_NM: String? = null
        var MAEDO_NM: String? = null
        var NEGO_REQST_CLNT_NO: String? = null
        var TOTAL_COUNT: String? = null
        var CLNT_NO: String? = null
        var NEGO_OBJ_CLNT_NO: String? = null
        var NEGO_SETT_STAT_CODE: String? = null
        var searchValue: String? = null
        var DEAL_DATE: String? = null

        var CVNT_DEAL_DATE: String? = null
        var ROWNUM: String? = null

        var PUBLIC_YN: String? = null
        var SECRET_CERTI_YN: String? = null
        var PBLS_CLNT_NO: String? = null
        var IS_MY_ORDER: String? = null


    }

    class Time : Serializable{
        var time: Long? = null
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}