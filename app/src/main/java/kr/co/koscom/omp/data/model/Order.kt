package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Order {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: DataMap? = null

    class DataMap{
        var resultCount: Int? = null
        var resultList: List<OrderItem>? = null
    }

    class OrderItem : Serializable{
        var STK_CODE: String? = null
        var PBLS_CLNT_NO: String? = null
        var ROWNUM: String? = null
        var DEAL_QTY: Int? = null
        var SEC_KIND_DTL_TP_CODE: String? = null
        var ORDER_NO: String? = null
        var SEC_KIND_TP_CODE: String? = null
        var STK_NM: String? = null
        var STOCK_TP_CODE_NM: String? = null
        var DEAL_TP: String? = null
        var POST_TIME_FORM: String? = null
        var POST_ORD_STAT_CODE: String? = null
        var NICKNAME: String? = null
        var CHG_DTTM: String? = null
        var ENTP_NO: String? = null
        var DEAL_UPRC: Long? = null
        var RMQTY: Int? = null
        var CORP_HANGL_NM: String? = null
        var FAV_CORP_NO: String? = null

        var CHART: List<Chart>? = null
    }

    class Chart: Serializable{
        var CVNT_EXEC_DTTM: String? = null
        var AVERAGE: Long? = null
    }


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}