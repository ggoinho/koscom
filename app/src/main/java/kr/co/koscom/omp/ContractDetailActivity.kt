package kr.co.koscom.omp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.LoginViewModel
import com.sendbird.syncmanager.utils.Base64Utils
import com.sendbird.syncmanager.utils.ComUtil
import com.sendbird.syncmanager.utils.PreferenceUtils
import com.signkorea.openpass.interfacelib.SKCertManager
import com.signkorea.openpass.interfacelib.SKConstant
import com.signkorea.openpass.sksystemcrypto.SKSystemCertInfo
import com.signkorea.openpass.sksystemcrypto.SKUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contract_detail.*
import kotlinx.android.synthetic.main.activity_drawer_contract.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Contract
import kr.co.koscom.omp.data.model.Response
import kr.co.koscom.omp.view.ViewUtils


/**
 * 계약서확인
 */

class ContractDetailActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private var viewModelFactory: ViewModelFactory? = null

    var chatViewModel: ChatViewModel? = null
    private lateinit var loginViewModel: LoginViewModel

    private val disposable = CompositeDisposable()

    var channelTitle: String? = null
    var channelUrl: String? = null
    var orderNo: String? = null
    var contract: Contract? = null
    var myDealTp: String? = null
    var apiListeners = mutableListOf<Runnable>()

    var myTabLayout: TabLayout? = null
    var myContractFragment: ContractFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contractDetailActivity = this

        setContentView(R.layout.activity_drawer_contract)

        channelTitle = intent.getStringExtra("groupChannelTitle")
        channelUrl = intent.getStringExtra("groupChannelUrl")
        orderNo = intent.getStringExtra("orderNo")


        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        btnClose.setOnClickListener {
            finish()
        }

        btnOpenContract.setOnClickListener {
            drawer_layout!!.openDrawer(GravityCompat.START)
        }

        myTabLayout = tabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                vpContract.setCurrentItem(tab!!.position)

                myContractFragment?.switchZone(tab.position)
            }

        })

        vpContract.adapter = ContractPagerAdapter()
        vpContract.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                tabLayout.setScrollPosition(position, positionOffset, true, true)
            }

            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }

        })

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            myContractFragment = ContractFragment()
            transaction.replace(R.id.nav_view, myContractFragment!!)
            transaction.commitAllowingStateLoss()
        }


        nav_view.postDelayed({

            disposable.add(chatViewModel!!.getContract(PreferenceUtils.getUserId(), orderNo!!, channelUrl!!, channelTitle!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ contractResponse ->
                    Log.d(GroupChannelActivity::class.java.simpleName, "contract : $contractResponse")
                    Log.d(GroupChannelActivity::class.java.simpleName, "apiListeners : $apiListeners")

                    if("0000".equals(contractResponse.rCode)){
                        contract = contractResponse

                        for (listener in apiListeners){
                            listener.run()
                        }
                    }else{
                        ViewUtils.showErrorMsg(
                            this@ContractDetailActivity,
                            contractResponse.rCode,
                            contractResponse.rMsg
                        )
                    }
                    apiListeners.clear()
                },
                    { throwable ->
                        apiListeners.clear()
                        throwable.printStackTrace()
                        ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
                    }))
        }, 500)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d(ContractDetailActivity::class.java.simpleName, "onNewIntent()")

        var pairwiseDID = intent?.getStringExtra("pairwiseDID")
        var publicKey = intent?.getStringExtra("publicKey")
        var clientName = intent?.getStringExtra("clientName")
        var signature = intent?.getStringExtra("signature")
        Log.d(ContractDetailActivity::class.java.simpleName, "pairwiseDID : $pairwiseDID")
        Log.d(ContractDetailActivity::class.java.simpleName, "publicKey : $publicKey")
        Log.d(ContractDetailActivity::class.java.simpleName, "clientName : $clientName")
        Log.d(ContractDetailActivity::class.java.simpleName, "signature : $signature")


        ComUtil.signHandler(pairwiseDID,publicKey,signature)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }


    private fun showProgressDialog(mTitle: String, mMessage: String){

        progress_bar_login?.visibility = View.VISIBLE
    }

    private fun initializeOpenPass(listener: () -> Unit) {
        showProgressDialog("", "초기화 중입니다.")

        // 초기화 함수 호출.
        val nResult = SKCertManager.initOpenPass(this,
            LoginActivity.OPENPASS_LICENSE,
            LoginActivity.OPENPASS_LAUNCHMODE
        ) { requestCode, resultCode, resultMessage ->

            progress_bar_login?.visibility = View.INVISIBLE

            if (resultCode == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                resultCode == SKConstant.RESULT_CODE_ERROR_APP_DISABLED ||
                resultCode == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE){
                // OpenPass 설치 등 상태 관련 오류
                SKCertManager.showErrorPopup(resultCode)
            }
            else if (resultCode == SKConstant.RESULT_CODE_OK){
                Log.d(WebFragment::class.simpleName, "initializeOpenPass success.")
                listener.invoke()
            }
            else {
                // OpenPass 초기화 실패
                Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show()
                progress_bar_login?.visibility = View.INVISIBLE
            }
        }

        if (SKConstant.RESULT_CODE_OK != nResult) {
            Toast.makeText(this, "MyPass 초기화 중 오류가 발생하였습니다.($nResult)", Toast.LENGTH_SHORT)
                .show()
            progress_bar_login?.visibility = View.INVISIBLE

            if (nResult == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                nResult == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE ||
                nResult == SKConstant.RESULT_CODE_ERROR_APP_DISABLED
            ) {
                SKCertManager.showErrorPopup(nResult)
            }
        }
    }

    fun signContract(listener: Runnable){

        initializeOpenPass {
            if (PreferenceUtils.getLoginType().equals("openpass")) {
                Log.d(WebFragment::class.simpleName, "SKCertManager.sign invoke")
                SKCertManager.sign(
                    SKConstant.REQUEST_CODE_KOSCOM_FULL_SIGN,
                    null,
                    "ServerRandom".toByteArray(),
                    SKConstant.AUTH_TYPE_ALL,
                    false,
                    ComUtil.policyMode,
                    null
                ) { requestCode, resultCode, resultMessage, binSignData, b64Cert, isTrustZone ->

                    Log.e(
                        LoginActivity::class.simpleName,
                        "SKCertManager.sign result : " + resultCode
                    )

                    if (resultCode == SKConstant.RESULT_CODE_OK) {
                        //Log.v("OpenPassClient", "binSignData : " + String(binSignData))
                        //Log.v("OpenPassClient", "Sign result(hex) : " + SKUtil.bin2hex(binSignData))

                        resultCertify(
                            "1",
                            binSignData,
                            "",
                            { securityNum: String?, dn: String, signature: String, publicKey: String, name: String ->

                                disposable.add(
                                    chatViewModel!!.signContract(
                                        PreferenceUtils.getUserId(),
                                        orderNo!!,
                                        channelUrl!!,
                                        dn,
                                        publicKey,
                                        signature,
                                        contract!!.RESULT_GETUSERAUTHINFOLIST!!.get(0).CERTI_MTHD_TP
                                            ?: "1"
                                    )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ response ->
                                            Log.d(
                                                GroupChannelActivity::class.java.simpleName,
                                                "response : $response"
                                            )

                                            if ("0000".equals(response.rCode)) {

                                                if (listener != null) {
                                                    ViewUtils.alertDialog(this, response.rMsg){}
                                                    listener.run()
                                                }
                                            } else {
                                                ViewUtils.showErrorMsg(
                                                    this,
                                                    response.rCode,
                                                    response.rMsg
                                                )
                                            }
                                            apiListeners.clear()
                                        },
                                            { throwable ->
                                                apiListeners.clear()
                                                throwable.printStackTrace()
                                                ViewUtils.alertDialog(
                                                    this,
                                                    "네트워크상태를 확인해주세요."
                                                ) {}
                                            })
                                )
                            })
                    }  else if (resultCode == SKConstant.RESULT_CODE_ERROR_SESSION_EXPIRED) {
                        Log.e(LoginActivity::class.simpleName, "RESULT_CODE_ERROR_SESSION_EXPIRED")
                    } else {
                        // 에러 처리
                        // 만들어진 단축서명용 전자서명 생성키 삭제
                        // SKCertManager.clear();

                        Toast.makeText(
                            this,
                            "[SKCertManager.sign()] ERR($resultCode\n$resultMessage)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {

                getSkdid("S",contract!!.RESULT_GETTRANDTLINFOVIEW!!.CVNT_NO) {didNonce: String, didSvcPublic: String ->
                    getIntentData(didNonce,didSvcPublic) {pairwise: String?, publicKey: String?, signature : String? ->

                        disposable.add(chatViewModel!!.signContract(PreferenceUtils.getUserId(), orderNo!!, channelUrl!!, pairwise!!, publicKey!!, signature!!, contract!!.RESULT_GETUSERAUTHINFOLIST!!.get(0).CERTI_MTHD_TP ?: "1")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ response ->
                                Log.d(GroupChannelActivity::class.java.simpleName, "response : $response")

                                if("0000".equals(response.rCode)){

                                    if(listener != null){
                                        listener.run()
                                    }
                                }else{
                                    ViewUtils.showErrorMsg(this, response.rCode, response.rMsg)
                                }
                                apiListeners.clear()
                            },
                                { throwable ->
                                    apiListeners.clear()
                                    throwable.printStackTrace()
                                    ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
                                }))

                    }
                }
            }

        }
    }

    private fun resultCertify(opCode: String, signData: String, snData: String, listener: (securityNum: String?, dn: String, signature: String, publicKey: String, name: String) -> Unit){
        disposable.add(loginViewModel.resultCertOpenPass(opCode, signData, snData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    Log.d(LoginActivity::class.java.simpleName,"resultCertify : success")

                    if(listener != null){
                        listener.invoke(it.SECURITY_NUM, it.DN!!, it.SIGNATURE!!, it.PUBLIC_KEY!!, it.NAME!!)
                    }
                }else{
                    ViewUtils.alertDialog(this, "${it.result} : ${it.ERR_MSG}"){}
                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun getSkdid(type: String, cvntNo : String?, listener: (didNonce: String, didSvcPublic: String) -> Unit){

        disposable.add(loginViewModel.getSkdid(type,cvntNo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    Log.d(LoginActivity::class.java.simpleName,"resultCertify : success")

                    if(listener != null){
                        listener.invoke(it.DID_NONCE!!, it.DID_SVC_PUBLIC!!)
                    }
                }else{
                    ViewUtils.alertDialog(this, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))

    }

    private fun getIntentData(didNonce: String, didSvcPublic: String,  listener: (pairwise: String?, publicKey: String?, signature : String?) -> Unit) {

        ComUtil.signHandler = listener

        val url = "initial://reqService?orgName=Koscom&vcType=sign&svcPublicDID=${Base64Utils.getBase64encode(didSvcPublic)}&nonce=${Base64Utils.getBase64encode(didNonce)}"
        Log.d("getIntentData ", "Contract did scheme url : " + url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun requestPaper(listener: (msg: String?) -> Unit){
        if ("10" == myDealTp) {
            disposable.add(chatViewModel!!.requestPaperOfSeller(PreferenceUtils.getUserId(),orderNo!!, channelUrl!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d(GroupChannelActivity::class.java.simpleName, "response : $response")

                    if ("0000" == response.rCode) {
                        listener.invoke(response.rMsg)
                    } else {

                        ViewUtils.showErrorMsg(
                            this@ContractDetailActivity,
                            response.rCode,
                            response.rMsg
                        )
                    }
                }, { throwable ->
                    throwable.printStackTrace()
                    ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
                })
            )
        } else {
            disposable.add(chatViewModel!!.requestPaperOfBuyer(PreferenceUtils.getUserId(),orderNo!!,channelUrl!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d(GroupChannelActivity::class.java.simpleName, "response : $response")

                    if ("0000" == response.rCode) {

                        listener.invoke(response.rMsg)
                    } else {

                        ViewUtils.showErrorMsg(
                            this@ContractDetailActivity,
                            response.rCode,
                            response.rMsg
                        )
                    }
                }, { throwable ->
                    throwable.printStackTrace()
                    ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
                })
            )
        }
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    override fun onBackPressed() {

        if(drawer_layout!!.isDrawerOpen(GravityCompat.START)){
            drawer_layout?.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    inner class ContractPagerAdapter : PagerAdapter(){
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var inflater = this@ContractDetailActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(if(position == 0){R.layout.fragment_contract_document}else{R.layout.fragment_contract_right_document}, container, false)

            var param = "LOGIN_ID=${PreferenceUtils.getUserId()}&ORDER_NO=${orderNo}&CHANNEL_URL=${channelUrl}"
            var url = BuildConfig.SERVER_URL + "/mobile/openChatModalContract?$param"
            if(position == 1){
                url = BuildConfig.SERVER_URL + "/mobile/openChatModalConfirm?$param"
            }

            Log.d(ContractDetailActivity::class.simpleName, "web fragment laod url : " + url)
            if(position == 0){
                (supportFragmentManager.findFragmentById(R.id.webFragment1) as WebFragment).loadUrl(url)
            }
            else if(position == 1){
                (supportFragmentManager.findFragmentById(R.id.webFragment2) as WebFragment).loadUrl(url)
            }

            container.addView(view)

            return view

        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }

        override fun getCount(): Int {
            return 2
        }

    }

    fun hideNavigation(){
        drawer_layout?.closeDrawer(GravityCompat.START)
    }

    companion object {

        var contractDetailActivity: ContractDetailActivity? = null
    }
}
