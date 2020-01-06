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
import kr.co.koscom.omp.data.model.Response
import io.reactivex.Flowable
import kr.co.koscom.omp.data.ServiceFactory

class TestViewModel() : ViewModel() {

    val testService = ServiceFactory.getTestService()

    fun savePost(title: String,
                 body: String,
                 userId: Long): Flowable<Response> {

        return testService.savePost(title, body, userId)
    }
}
