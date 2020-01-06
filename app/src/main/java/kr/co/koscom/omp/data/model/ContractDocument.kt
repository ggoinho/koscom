package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class ContractDocument {


    var BNK_CODE_NAME: String? = null
    var TKOV_WRT_SMSN_AMT: String? = null
    var TRD_AMT: String? = null
    var CHG_DTTM: String? = null
    var CHG_ID: String? = null
    var REG_ID: String? = null
    var TRSF_PSN_RMNNO: String? = null
    var BNS_CMSN_AMT: String? = null
    var TKOV_PSN_RMNNO: String? = null
    var TKOV_PSN_ADDR: String? = null
    var BNK_CODE: String? = null
    var TRSF_WRT_SMSN_AMT: String? = null
    var PSTK_TRD_AMT: String? = null
    var TRSF_FRTN_CMSN_AMT: String? = null
    var TKOV_PSN_HP_NO: String? = null
    var CONFIRM_UNISSUED_SEQNO: String? = null
    var CVNT_NO: String? = null
    var TRSF_PSN_ATGRP: String? = null
    var TRSF_PSN_HP_NO: String? = null
    var ENTP_NO: String? = null
    var TKOV_SYS_UTLFEE_RT: String? = null
    var TRSF_PSN_DOB: String? = null
    var TKOV_PSN_CLNT_NO: String? = null
    var REG_DTTM: String? = null
    var TKOV_ESCRO_SMSN_AMT: String? = null
    var TRSF_PSN_ADDR: String? = null
    var VR_ACCNT_NO: String? = null
    var ORDER_NO: String? = null
    var TRSF_PSN_ACNT_NO: String? = null
    var CHANNEL_URL: String? = null
    var TKOV_PSN_ATGRP: String? = null
    var CVNT_DATE: String? = null
    var TRSF_SYS_UTLFEE_RT: String? = null
    var CANC_YN: String? = null
    var ENTP_HANGL_NM: String? = null
    var STK_CODE: String? = null
    var LOGIN_ID: String? = null
    var CORP_ADDR: String? = null
    var DEAL_QTY: String? = null
    var TKOV_PSN_NM: String? = null
    var PROGR_STEP_CODE: String? = null
    var TRSF_PSN_CLNT_NO: String? = null
    var TKOV_PSN_DOB: String? = null
    var BNK_NM: String? = null
    var TRSF_PSN_BNK_NM: String? = null
    var CORP_ENG_NM: String? = null
    var TRNS_ID: String? = null
    var TRSF_PSN_NM: String? = null
    var DEAL_DATE: String? = null

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}