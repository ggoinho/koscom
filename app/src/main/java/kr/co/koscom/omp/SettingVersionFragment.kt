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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_setting_version.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.viewmodel.UpdateViewModel
import kr.co.koscom.omp.extension.enableView
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.view.ViewUtils


/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * [androidx.viewpager.widget.ViewPager].
 */
class SettingVersionFragment : Fragment() {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var updateViewModel: UpdateViewModel
    private val disposable = CompositeDisposable()

    private var currentServerVersion = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.d(SettingVersionFragment::class.simpleName, "onCreateView()")
        return inflater.inflate(R.layout.fragment_setting_version, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SettingVersionFragment::class.simpleName, "onViewCreated()")

        viewModelFactory = Injection.provideViewModelFactory(activity!!)
        updateViewModel = ViewModelProviders.of(this, viewModelFactory).get(UpdateViewModel::class.java)


        init()
        initListener()
        checkVersion()
    }


    private fun init(){

    }

    private fun initListener(){

        tvUpdate.setOnClickListener {
            val appPackageName = requireActivity().packageName // getPackageName() from Context or Activity object
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }

    private fun checkVersion(){

        progress_bar_login.toVisible()
        disposable.add(updateViewModel.updateVersion("AOS")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_bar_login.toInvisible()

                Log.d(VersionInfoActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){
                    val currentAppVersion = getAppVersion()
                    currentServerVersion = it.datas?.resultList?.END_APP_VER ?: currentAppVersion

                    if (versionCompare(currentAppVersion,currentServerVersion)){
                        tvUpdate.enableView(false)
                        tvUpdateContents.text = R.string.versioninfo_update_contents2.toResString()
                    } else {
                        tvUpdate.enableView(true)
                        tvUpdateContents.text = R.string.versioninfo_update_contents.toResString()
                    }

                    tvVersionInfo.text = String.format(R.string.versioninfo_version_contents.toResString(),
                        Build.VERSION.RELEASE,
                        "V.${getAppVersion()}",
                        "V.${currentServerVersion}",
                        Build.MODEL)

                }else{
                    ViewUtils.showErrorMsg(requireActivity(), it.rCode, it.rMsg)
                }

            }, {
                progress_bar_login.toInvisible()
                it.printStackTrace()
                ViewUtils.alertDialog(requireActivity(), "네트워크상태를 확인해주세요."){}
            }))
    }


    private fun versionCompare(appVersion : String?, serverVersion : String?) : Boolean{
        if (appVersion != null && serverVersion != null) {
            if (serverVersion.compareTo(appVersion) > 0){
                return false
            } else {
                return true
            }
        }
        return true
    }


    private fun getAppVersion(): String{
        var appVersion = ""
        try{
            val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            appVersion = packageInfo.versionName

        }catch (e: PackageManager.NameNotFoundException){}

        return appVersion

    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }
}
