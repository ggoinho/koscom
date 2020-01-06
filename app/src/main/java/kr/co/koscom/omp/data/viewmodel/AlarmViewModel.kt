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


class AlarmViewModel() : ViewModel() {

    val alarmService = ServiceFactory.getAlarmService()

    fun searchNotReadCount(clientNo: String): Flowable<Alarm> {

        var request = Request()
        request.clnt_no = clientNo

        return alarmService.searchNotReadCount(request)
    }

    fun searchList(clientNo: String, start: Int, end: Int, code: String? = null): Flowable<Alarm> {

        var request = Request()
        request.clientNo = clientNo
        request.start = start
        request.end = end
        request.ancTpCode = code

        return alarmService.searchList(request)
    }

    fun updateRead(clientNo: String, seqno: String): Flowable<Response> {

        var request = Request()
        request.clientNo = clientNo
        request.seqno = seqno

        return alarmService.updateRead(request)
    }

}
