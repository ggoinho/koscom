package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Stock {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: DataMap? = null

    class DataMap{
        var resultList: List<ResultMap>? = null
    }

    class ResultMap : Serializable{
        var STK_CODE: String? = null
        var SEC_KIND_TP_CODE: String? = null
        var STK_NM: String? = null
        var ENTP_NO: String? = null
        var SEC_KIND_DTL_TP_CODE: String? = null
        var FLTN_QTY: String? = null
        var CORP_HANGL_NM: String? = null
        var TRSF_ABLE_QTY: String? = null
        var FAV_CORP_NO: String? = null
    }


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}