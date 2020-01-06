package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Main {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: DataMap? = null

    class DataMap{
        var myNegoList: List<Nego>? = null
        var myContList: List<NegoContract>? = null
        var orderList: List<Order.OrderItem>? = null
        var trdMgmtList: List<kr.co.koscom.omp.data.model.OrderContract.OrderContractItem>? = null
        var upBoardList: Board? = null
    }

    class Nego: Serializable{
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

    class NegoContract: Serializable{

        var CORP_HANGL_NM: String? = null
        var STK_NM: String? = null
        var STOCK_TP_CODE_NM: String? = null
        var SEC_KIND_DTL_TP_CODE: String? = null
        var DEAL_UPRC: String? = null
        var DEAL_QTY: String? = null
        var DEAL_TP: String? = null
        var STAT_FLAG: String? = null
        var NEGO_SETT_STAT_CODE: String? = null
        var NEGO_REQST_CLNT_NM: String? = null
        var RQST_CODE: String? = null
        var ORDER_NO: String? = null
        var CHANNEL_URL: String? = null
        var CONT_FLAG: String? = null
        var ESCR_FLAG: String? = null
    }

    class OrderContract: Serializable{
        var FROM_DATE: String? = null
        var REG_DTTM: String? = null
        var TO_DATE: String? = null
        var MAESU: String? = null
        var ORDER_NO: String? = null
        var MAESU_NM: String? = null
        var CHANNEL_URL: String? = null
        var CVNT_DATE: String? = null
        var startLimit: Int? = null
        var CHG_DTTM: String? = null
        var CHG_ID: String? = null
        var pageNo: String? = null
        var DEAL_UPRC: String? = null
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
    }

    class Board: Serializable{
        var SEQNO: Int? = null
        var CLNT_TTL: String? = null
        var REG_DTTM: String? = null
    }


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}