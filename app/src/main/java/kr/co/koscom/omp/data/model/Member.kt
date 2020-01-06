package kr.co.koscom.omp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class Member {

    var state: String? = null
    var user_id: String? = null
    var is_online: Boolean? = null
    var is_active: Boolean? = null
    var last_seen_at: Long = 0
    var nickname: String? = null
    var profile_url: String? = null
    var order_req: String? = null
    var deal_tp: String? = null
    var sign_yn: String? = null

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this);
    }
}