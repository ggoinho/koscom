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


class CodeViewModel() : ViewModel() {

    val codeService = ServiceFactory.getCodeService()

    fun searchCode(metaCode: String): Flowable<Code> {

        var request = Request()
        request.meta_code = metaCode

        return codeService.searchCode(request)
    }

}
