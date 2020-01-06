package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Code {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: ResultMap? = null

    class ResultMap{
        var resultList: List<CodeItem>? = null
    }

    class CodeItem : Serializable{
        var META_CODE: String? = null
        var USE_YN: String? = null
        var CHG_DTTM: String? = null
        var CHG_ID: String? = null
        var REG_DTTM: String? = null
        var LOGIN_ID: String? = null
        var ORDER_SEQ: String? = null
        var META_CODE_NM: String? = null
        var ATTR_CODE: String? = null
        var REG_ID: String? = null
        var ATTR_NM: String? = null
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}