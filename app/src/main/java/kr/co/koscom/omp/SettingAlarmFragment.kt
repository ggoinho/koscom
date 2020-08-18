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

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scsoft.boribori.data.viewmodel.AlarmViewModel
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.CodeViewModel
import com.scsoft.boribori.data.viewmodel.LoginViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chatlist.view.*
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_alarm.btnTop
import kotlinx.android.synthetic.main.fragment_alarm.progress_bar_login
import kotlinx.android.synthetic.main.fragment_setting_alarm.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Alarm
import kr.co.koscom.omp.data.model.Code
import kr.co.koscom.omp.extension.enableView
import kr.co.koscom.omp.view.PaginationListener
import kr.co.koscom.omp.view.ViewUtils
import java.util.*


/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * [androidx.viewpager.widget.ViewPager].
 */
class SettingAlarmFragment : Fragment() {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var chatViewModel: ChatViewModel

    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.d(SettingAlarmFragment::class.simpleName, "onCreateView()")
        return inflater.inflate(R.layout.fragment_setting_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SettingAlarmFragment::class.simpleName, "onViewCreated()")

        viewModelFactory = Injection.provideViewModelFactory(activity!!)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)


        getUserPush()
    }


    private fun getUserPush(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(loginViewModel.getUserPush(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    if(it.datas!!.notificationAgreement!!){
                        alarmYn.isChecked = true

                        Preference.setPushYn(requireActivity(), "Y")
                    }else{
                        alarmYn.isChecked = false

                        Preference.setPushYn(requireActivity(), "N")
                    }

                    if(it.datas!!.adAgreement!!){
                        adYn.isChecked = true

                        Preference.setAdYn(requireActivity(), "Y")
                    }else{
                        adYn.isChecked = false

                        Preference.setAdYn(requireActivity(), "N")
                    }

                    bindAlarmToggle(alarmYn)
                    bindAdToggle(adYn)

                }else{

                    ViewUtils.showErrorMsg(requireActivity(), it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(requireActivity(), "네트워크상태를 확인해주세요."){}
            }))
    }


    private fun registToken(listener: Runnable?){
        progress_bar_login.visibility = View.VISIBLE
        disposable.add(chatViewModel.registPushToken(PreferenceUtils.getUserId(), Preference.getPushToken(requireActivity())!!, Preference.getPushYn(requireActivity()) == "Y", Preference.getAdYn(requireActivity()) == "Y")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(LoginActivity::class.java.simpleName,"registPushToken : success")

                    if(listener != null){
                        listener.run()
                    }
                }else{
                    ViewUtils.showErrorMsg(requireActivity(), it.rCode, it.rMsg)
                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(requireActivity(), "네트워크상태를 확인해주세요."){}
            }))
    }


    private fun bindAlarmToggle(toggleButton: ToggleButton){

        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            var dlg = Dialog(requireActivity());

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var btnClose = dlg.findViewById<LinearLayout>(R.id.btnClose)
            btnClose.setOnClickListener {
                dlg.cancel()
            }
            var confirmZone = dlg.findViewById<LinearLayout>(R.id.confirmZone)
            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var btnOk = dlg.findViewById<TextView>(R.id.btnOk)
            var message = dlg.findViewById<TextView>(R.id.message)
            if(b){
                Preference.setPushYn(requireActivity(), "Y")

                registToken(null)

                message.text = "Push 수신동의 처리를 완료하였습니다."
                btnOk.setOnClickListener { dlg.dismiss() }

            }
            else{
                btnOk.visibility = View.GONE
                confirmZone.visibility = View.VISIBLE

                message.text = "알림설정을 해제하시면 비상장주식거래에 필요한 Push 서비스가 중단됩니다.\n해제하시겠습니까?"
                btnConfirm.setOnClickListener {
                    Preference.setPushYn(requireActivity(), "N")

                    registToken(null)

                    dlg.dismiss()
                }
                btnCancel.setOnClickListener {
                    dlg.cancel()
                }
                dlg.setOnCancelListener {
                    toggleButton.setOnCheckedChangeListener(null)
                    toggleButton.isChecked = true
                    bindAlarmToggle(toggleButton)
                }
            }
        }

    }

    private fun bindAdToggle(toggleButton: ToggleButton){

        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            var dlg = Dialog(requireActivity());

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var btnClose = dlg.findViewById<LinearLayout>(R.id.btnClose)
            btnClose.setOnClickListener {
                dlg.cancel()
            }
            var confirmZone = dlg.findViewById<LinearLayout>(R.id.confirmZone)
            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var btnOk = dlg.findViewById<TextView>(R.id.btnOk)
            var message = dlg.findViewById<TextView>(R.id.message)
            if(b){
                Preference.setAdYn(requireActivity(), "Y")

                registToken(null)

                message.text = "마케팅 수신동의 처리를 완료하였습니다."
                btnOk.setOnClickListener { dlg.dismiss() }

            }
            else{
                btnOk.visibility = View.GONE
                confirmZone.visibility = View.VISIBLE

                message.text = "마케팅 수신동의를 해제하시면 비상장주식거래에 필요한 정보를 받아보실 수 없습니다.\n해제하시겠습니까?"
                btnConfirm.setOnClickListener {
                    Preference.setAdYn(requireActivity(), "N")

                    registToken(null)

                    dlg.dismiss()
                }
                btnCancel.setOnClickListener {
                    dlg.cancel()
                }
                dlg.setOnCancelListener {
                    toggleButton.setOnCheckedChangeListener(null)
                    toggleButton.isChecked = true
                    bindAdToggle(toggleButton)
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }
}
