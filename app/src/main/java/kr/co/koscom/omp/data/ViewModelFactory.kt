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

package kr.co.koscom.omp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.scsoft.boribori.data.viewmodel.*
import kr.co.koscom.omp.data.viewmodel.UpdateViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            return TestViewModel() as T
        }
        else if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel() as T
        }
        else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        else if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel() as T
        }
        else if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel() as T
        }
        else if (modelClass.isAssignableFrom(CodeViewModel::class.java)) {
            return CodeViewModel() as T
        }
        else if (modelClass.isAssignableFrom(NegoViewModel::class.java)) {
            return NegoViewModel() as T
        }
        else if (modelClass.isAssignableFrom(UpdateViewModel::class.java)) {
            return UpdateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
