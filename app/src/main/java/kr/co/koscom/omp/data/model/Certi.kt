package kr.co.koscom.omp.data.model

import org.apache.commons.lang3.builder.ToStringBuilder

class Certi {

    var rCode: String? = null
    var rMsg: String? = null

    var datas: CertiMap? = null

    class CertiMap{
        var certi_num: String? = null
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}