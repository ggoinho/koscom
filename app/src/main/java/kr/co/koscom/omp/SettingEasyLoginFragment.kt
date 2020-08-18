/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.koscom.omp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sendbird.syncmanager.utils.ComUtil
import com.signkorea.openpass.interfacelib.SKCallback
import com.signkorea.openpass.interfacelib.SKCertManager
import com.signkorea.openpass.interfacelib.SKConstant
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_launch.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.view.MyDialog
import kr.co.koscom.omp.view.ViewUtils


/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * [androidx.viewpager.widget.ViewPager].
 */
class SettingEasyLoginFragment : Fragment() {

    private lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    var mainProgress: MyDialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.d(SettingEasyLoginFragment::class.simpleName, "onCreateView()")
        return inflater.inflate(R.layout.fragment_setting_easylogin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SettingEasyLoginFragment::class.simpleName, "onViewCreated()")

        viewModelFactory = Injection.provideViewModelFactory(activity!!)


        btnFingerRegist.setOnClickListener {
            SKCertManager.callMyPassPage(120, ComUtil.policyMode,null, SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->
            })
        }
        btnPinRegist.setOnClickListener {
            SKCertManager.callMyPassPage(117,
                ComUtil.policyMode,null, SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->
                })
        }
        btnPinChange.setOnClickListener {
            SKCertManager.callMyPassPage(118,
                ComUtil.policyMode,null, SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->
                })
        }
        btnPatternRegist.setOnClickListener {
            SKCertManager.callMyPassPage(141,
                ComUtil.policyMode,null, SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->
                })
        }
        btnPatternChange.setOnClickListener {
            SKCertManager.callMyPassPage(142,
                ComUtil.policyMode,null, SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->
                })
        }

        initializeMyPass()
    }

    private fun showProgressDialog(mTitle: String, mMessage: String){

        if(mainProgress == null){
            mainProgress = ViewUtils.showProgressDialog(requireContext(), mTitle, mMessage, true)
        }
        else{
            mainProgress!!.title?.setText(mTitle)
            mainProgress!!.message?.setText(mMessage)
            mainProgress!!.show()
        }

        if(mTitle.isNullOrEmpty()){
            mainProgress!!.title?.visibility = View.GONE
        }
    }

    private fun initializeMyPass() {
        showProgressDialog("", "초기화 중입니다.")

        // 초기화 함수 호출.
        val nResult = SKCertManager.initMyPass(requireContext(),
            LoginActivity.MY_LICENSE,
            LoginActivity.MY_LAUNCHMODE,
            SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->

                mainProgress?.dismiss()

                if (resultCode == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                    resultCode == SKConstant.RESULT_CODE_ERROR_APP_DISABLED ||
                    resultCode == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE){
                    // MyPass 설치 등 상태 관련 오류
                    SKCertManager.showErrorPopup(resultCode)
                }
                else if (resultCode == SKConstant.RESULT_CODE_OK){
                    // MyPass 초기화 성공
                }
                else {
                    // MyPass 초기화 실패
                    Toast.makeText(requireContext(), resultMessage, Toast.LENGTH_LONG).show()
                    mainProgress?.dismiss()
                }
            })

        if (SKConstant.RESULT_CODE_OK != nResult) {
            Toast.makeText(requireContext(), "MyPass 초기화 중 오류가 발생하였습니다.($nResult)", Toast.LENGTH_SHORT)
                .show()
            mainProgress?.dismiss()

            if (nResult == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                nResult == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE ||
                nResult == SKConstant.RESULT_CODE_ERROR_APP_DISABLED
            ) {
                SKCertManager.showErrorPopup(nResult)
            } else
                requireActivity().finish()
        }
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }
}
