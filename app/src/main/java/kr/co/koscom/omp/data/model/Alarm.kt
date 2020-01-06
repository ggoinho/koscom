package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Alarm {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: ResultMap? = null

    class ResultMap{

        var result: Int? = null

        var resultCount: Int? = null
        var resultList: List<AlarmItem>? = null
    }

    class AlarmItem : Serializable{
        var SEQNO: String? = null
        var ANC_TP_CODE: String? = null
        var ANC_TP_NAME: String? = null
        var ANC_CNTS: String? = null
        var ANC_DATE: String? = null
        var ANC_YN: String? = null
        var LANDING_URL: String? = null

        override fun toString(): String {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}