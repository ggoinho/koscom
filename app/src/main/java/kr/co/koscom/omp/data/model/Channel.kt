package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class Channel {

    var rCode: String? = null
    var rMsg: String? = null

    var membersList: List<Member>? = null

    var CHANNEL_TITLE: String? = null
    var DEAL_UPRC: String? = null
    var DEAL_QTY: String? = null
    var R_DEAL_UPRC: String? = null
    var R_FLTN_QTY: String? = null
    var PUBLIC_YN: String? = null
    var NEGO_REQST_CLNT_STAT_CODE: String? = null


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}