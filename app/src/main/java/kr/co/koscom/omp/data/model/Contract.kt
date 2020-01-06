package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Contract {

    var rCode: String? = null
    var rMsg: String? = null

    var MEMBERSLIST: List<Member>? = null

    var BLOCKDATA: String? = null
    var DEAL_UPRC: String? = null
    var DEAL_QTY: String? = null
    var SETT_FIN_DT: String? = null
    var SELLER_CLNT_HANGL_NM: String? = null
    var COMPANY_CORP_HANGL_NM: String? = null
    var SEC_KIND_TP_CODE_NM: String? = null
    var HLD_QTY: String? = null

    var RESULT_CONFIRMUNISSUEDVO: RightDocument? = null
    var RESULT_GETTRANDTLINFOVIEW: ContractDocument? = null

    var RESULT_GETUSERAUTHINFOLIST: List<CertiInfo>? = null

    class CertiInfo : Serializable{
        var CERTI_MTHD_TP: String? = null
        var CLNT_NO: String? = null
        var CERTI_DATA: String? = null

    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}