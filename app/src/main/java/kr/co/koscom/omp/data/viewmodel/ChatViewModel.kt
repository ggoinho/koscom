/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scsoft.boribori.data.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import kr.co.koscom.omp.data.ServiceFactory
import kr.co.koscom.omp.data.model.*
import okhttp3.RequestBody
import org.json.JSONObject



class ChatViewModel() : ViewModel() {

    val chatService = ServiceFactory.getChatService()

    fun registPushToken(userId: String, deviceToken: String, isNotificationAgreement: Boolean, isAdAgreement: Boolean): Flowable<Response> {

        var request = Request()
        request.MTYPE = "regUserPUSH"
        request.userId = userId
        request.deviceType = "GCM"
        request.deviceToken = deviceToken
        request.isNotificationAgreement = isNotificationAgreement
        request.isAdAgreement = isAdAgreement
        request.isNightAdAgreement = true

        return chatService.registPushToken(request)
    }

    fun sendPush(to: String, sendTagCd: String, content: String, reserveTime: String? = null, scheduleCode: String? = null, messageType: String? = null): Flowable<Response> {

        var request = Request()
        request.MTYPE = "sendPush"

        request.target = Request.PushTarget()
        request.target!!.type = "USER"
        request.target!!.to = listOf(to)

        request.sendTagCd = sendTagCd
        request.sendTagCd2 = sendTagCd
        request.content = content
        request.reserveTime = reserveTime
        request.scheduleCode = scheduleCode
        request.deviceType = null
        request.messageType = messageType

        return chatService.sendPush(request)
    }

    fun login(loginId: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId

        return chatService.login(request)
    }
    fun requestNego(loginId: String, orderNo: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo

        return chatService.login(request)
    }
    fun acceptNego(loginId: String, orderNo: String, channelUrl: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        return chatService.chatAccept(request)
    }
    fun denyNego(loginId: String, orderNo: String, channelUrl: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        return chatService.chatDeny(request)
    }

    fun openChannel(loginId: String, orderNo: String, channelUrl: String, channelTitle: String): Flowable<Channel> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl
        request.CHANNEL_TITLE = channelTitle

        return chatService.openChannel(request)
    }

    fun getContract(loginId: String, orderNo: String, channelUrl: String, channelTitle: String): Flowable<Contract> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl
        request.CHANNEL_TITLE = channelTitle

        return chatService.getContract(request)
    }

    fun getEscrow(loginId: String, orderNo: String, channelUrl: String): Flowable<Escrow> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        return chatService.getEscro(request)
    }

    fun saveNegotiation(loginId: String, orderNo: String, channelUrl: String, count: String, price: String, payEndDate: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl
        request.DEAL_QTY = count
        request.DEAL_UPRC = price
        request.SETT_FIN_DT = payEndDate

        return chatService.saveNegotiation(request)
    }

    fun acceptNegotiation(loginId: String, orderNo: String, channelUrl: String, number: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl
        request.NEGO_INFO_CNT = number

        return chatService.acceptNegotiation(request)
    }

    fun denyNegotiation(loginId: String, orderNo: String, channelUrl: String, number: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl
        request.NEGO_INFO_CNT = number

        return chatService.denyNegotiation(request)
    }

    fun requestPaperOfBuyer(loginId: String, orderNo: String, channelUrl: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        return chatService.requestPaperOfBuyer(request)
    }

    fun requestPaperOfSeller(loginId: String, orderNo: String, channelUrl: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        return chatService.requestPaperOfSeller(request)
    }

    fun signContract(loginId: String, orderNo: String, channelUrl: String, dn: String, publicKey: String, signature: String, certiTp: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        request.DN = dn
        request.PUBLIC_KEY = publicKey
        request.SIGNATURE = signature
        request.CERTI_MTHD_TP = certiTp

        return chatService.signContract(request)
    }

    fun exitChannel(loginId: String, orderNo: String, channelUrl: String): Flowable<Response> {

        var request = Request()
        request.LOGIN_ID = loginId
        request.ORDER_NO = orderNo
        request.CHANNEL_URL = channelUrl

        return chatService.exitChannel(request)
    }

    fun logintest(passwd: String): Flowable<Response> {

        var request = Request()
        request.PASSWD = passwd

        return chatService.logintest(request)
    }
}
