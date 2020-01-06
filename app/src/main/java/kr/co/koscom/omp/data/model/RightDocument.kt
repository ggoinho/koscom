package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class RightDocument {

    var CLNT_CNF_NO: String? = null
    var REG_DTTM: String? = null
    var ISS_DTTM: String? = null
    var STHL_REG_DATE: String? = null
    var ORDER_NO: String? = null
    var HLD_QTY: String? = null
    var CHANNEL_URL: String? = null
    var APPROVAL_DTTM: String? = null
    var UNISSUED_STAT_CODE: String? = null
    var SEC_KIND_TP_CODE: String? = null
    var CHG_DTTM: String? = null
    var CHG_ID: String? = null
    var CLNT_ADDR: String? = null
    var SNAPSHOT_NO: String? = null
    var REG_ID: String? = null
    var PARPRC: String? = null
    var CORP_HANGL_NM: String? = null
    var STK_CODE: String? = null
    var DLG_PSN_NM: String? = null
    var LOGIN_ID: String? = null
    var SEC_KIND_TP_CODE_NM: String? = null
    var CLNT_NM: String? = null
    var FLTN_QTY: String? = null
    var CONFIRM_UNISSUED_SEQNO: String? = null
    var CLNT_NO: String? = null
    var ENTP_NO: String? = null
    var RTN_RSN: String? = null
    var CORP_ENG_NM: String? = null

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}