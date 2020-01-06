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


class LoginViewModel() : ViewModel() {

    val loginService = ServiceFactory.getLoginService()

    fun getSkDidInfo(nonce: String?, type: String?, date : String?, seq : String?): Flowable<Response> {

        var request = Request()
        request.NONCE = nonce
        request.TYPE = type
        request.DID_REQ_DATE = date
        request.DID_REQ_SEQ = seq

        return loginService.getSkDidInfo(request)
    }

    fun getSkdid(type: String, cvnt_no : String? = null): Flowable<Response> {

        var request = Request()
        request.TYPE = type
        request.CVNT_NO = cvnt_no

        return loginService.getSkdid(request)
    }

    fun resultCertOpenPass(opCode: String, signData: String, snData: String): Flowable<Response> {

        var request = Request()
        request.opCode = opCode
        request.signData = signData
        request.snData = snData

        return loginService.resultCertOpenPass(request)
    }

    fun login(gubn: String, certified: String): Flowable<Response> {

        var request = Request()
        request.CERTI_MTHD_TP = gubn
        request.CERTI_DATA = certified

        return loginService.login(request)
    }

    fun login(gubn: String, certified: String, signData: String, opCode: String): Flowable<Response> {

        var request = Request()
        request.CERTI_MTHD_TP = gubn
        request.CERTI_DATA = certified
        request.signData = signData
        request.opCode = opCode

        return loginService.login(request)
    }

    fun logout(userId: String): Flowable<Response> {

        var request = Request()
        request.CLNT_NO = userId

        return loginService.logout(request)
    }

    fun getUserPush(userId: String): Flowable<Response> {

        var request = Request()
        request.userId = userId

        return loginService.getUserPush(request)
    }

}
