package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class Escrow {

    var rCode: String? = null
    var rMsg: String? = null

    var resultMap: ResultMap? = null

    class ResultMap{
        var DEPOSIT_CLOSE_DT: String? = null
        var TKOV_ESCRO_SMSN_AMT: String? = null
        var VR_BNK_CODE_NM: String? = null
        var TKOV_SYS_UTLFEE_RT: String? = null
        var VR_ACCNT_NO: String? = null
        var TRSF_BNK_AMT: String? = null
        var DEPOSIT_AMT: String? = null
        var TRSF_FRTN_CMSN_AMT: String? = null
        var TRD_AMT: String? = null
        var TKOV_WRT_SMSN_AMT: String? = null
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}