package com.sendbird.syncmanager.utils

import com.signkorea.openpass.interfacelib.SKConstant

class ComUtil {

    companion object {

        @kotlin.jvm.JvmField
        var cookie: HashSet<String>? = null

        lateinit var handler: ((didNonce: String?, didSvcPublic: String?) -> Unit)
        lateinit var signHandler : ((didNonce: String?, didSvcPublic: String?, signature : String?) -> Unit)

        var fromWebView : Boolean = false

        var stringUrl : String? = null

        //var policyMode : Int = SKConstant.POLICY_MODE_GENERAL_PRIVATE + SKConstant.POLICY_MODE_GENERAL_BUSINESS + SKConstant.POLICY_MODE_SECURITIES_ONLY
        var policyMode : Int = SKConstant.POLICY_MODE_ALL

    }

}