package kr.co.koscom.omp.data.model

import com.sendbird.syncmanager.utils.PreferenceUtils
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import org.apache.commons.lang3.builder.ToStringBuilder

class Request {

    var TYPE: String? = null

    var CERTI_MTHD_TP: String? = null
    var CERTI_DATA: String? = null

    var CLNT_NO: String? = PreferenceUtils.getUserId()
    var clnt_no: String? = PreferenceUtils.getUserId()
    var MOBILE_TOCKEN: String? = Preference.getServerToken(BaseApplication.getAppContext())

    var LOGIN_ID: String? = null
    var PASSWD: String? = null

    var ORDER_NO: String? = null
    var CHANNEL_URL: String? = null
    var channelUrl: String? = null
    var CHANNEL_TITLE: String? = null

    var DEAL_QTY: String? = null
    var DEAL_UPRC: String? = null
    var SETT_FIN_DT: String? = null

    var NEGO_INFO_CNT: String? = null

    var MTYPE: String? = null
    var userId: String? = null
    var deviceType: String? = null
    var deviceToken: String? = null
    var isNotificationAgreement: Boolean = true
    var isAdAgreement: Boolean = true
    var isNightAdAgreement: Boolean = true

    var messageType: String? = null
    var target: PushTarget? = null
    var content: String? = null
    var scheduleCode: String? = null
    var reserveTime: String? = null
    var sendTagCd: String? = null
    var sendTagCd2: String? = null

    var clientNo: String? = null
    var view_type: String? = null
    var idx: String? = null
    var word: String? = null

    var orderNo: String? = null
    var rqtClientNo: String? = null
    var orderStkCode: String? = null

    var unitPrice: String? = null
    var orderType: String? = null
    var condition: String? = null
    var clientName: String? = null
    var orderQty: String? = null
    var stockCode: String? = null
    var startDate: String? = null

    var ancTpCode: String? = null
    var seqno: String? = null
    var start: Int? = null
    var end: Int? = null
    var keyword: String? = null

    var meta_code: String? = null

    var opCode: String? = null
    var signData: String? = null
    var snData: String? = null

    var entpNo: String? = null
    var type: String? = null

    var DN: String? = null
    var PUBLIC_KEY: String? = null
    var SIGNATURE: String? = null

    var NONCE: String? = null
    var DID_REQ_DATE: String? = null
    var DID_REQ_SEQ: String? = null

    var CVNT_NO: String? = null

    var DEVICE: String? = null

    var dealTpCode : String? = null


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }

    class PushTarget{
        var type: String? = null
        var deviceType: String? = null
        var to: List<String>? = null

        override fun toString(): String {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}