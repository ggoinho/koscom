package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

class Response {

    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("body")
    @Expose
    var body: String? = null
    @SerializedName("userId")
    @Expose
    var userId: Int? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null

    var MOBILE_TOCKEN: String? = null
    var CLNT_NO: String? = null
    var CNLT_NM: String? = null

    var SECURITY_NUM: String? = null
    var DN: String? = null
    var SIGNATURE: String? = null
    var PUBLIC_KEY: String? = null
    var result: String? = null
    var NAME: String? = null

    var DID_NONCE: String? = null
    var DID_SVC_PUBLIC: String? = null

    var rCode: String? = null
    var rMsg: String? = null
    var ERR_MSG: String? = null

    var datas: UserSetting? = null

    var DATE : String? = null
    var SEQ : String? = null
    var CLNT_HANGL_NM : String? = null
    var ACCESS_TOKEN : String? = null
    var XPIR_DATE : String? = null

    class UserSetting : Serializable{
        var adAgreement: Boolean? = null
        var notificationAgreement: Boolean? = null
        var nightAdAgreement: Boolean? = null
        var naverStatCode: String? = null

        var resultList : ResultList? = null
    }

    class ResultList : Serializable {
        var END_APP_VER : String? = null
        var ARM_MSG_CD: String? = null
        var ARM_MSG_TEXT : String? = null
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}