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

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.LoginViewModel
import com.sendbird.syncmanager.utils.Base64Utils
import com.sendbird.syncmanager.utils.ComUtil
import com.sendbird.syncmanager.utils.PreferenceUtils
import com.signkorea.openpass.interfacelib.SKCallback
import com.signkorea.openpass.interfacelib.SKCertManager
import com.signkorea.openpass.interfacelib.SKConstant
import com.signkorea.openpass.sksystemcrypto.SKSystemCertInfo
import com.signkorea.openpass.sksystemcrypto.SKUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_web.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.ViewUtils
import kr.co.koscom.omp.view.WebUtil
import org.json.JSONObject


/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * [androidx.viewpager.widget.ViewPager].
 */
class WebFragment : Fragment() {
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var chatViewModel: ChatViewModel

    val mHandler = Handler()

    private val disposable = CompositeDisposable()

    var webView: WebView? = null

    var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    var urlLast : String? = null

    var getIntent : Intent?= null

    var isCleanHistory = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.d(WebFragment::class.simpleName, "onCreateView()")
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(WebFragment::class.simpleName, "onViewCreated()")

        viewModelFactory = Injection.provideViewModelFactory(context!!)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)

//        if(activity!!.window.decorView.getRootView().findViewById<View>(R.id.bottom_navigation_view) == null){
//            Log.d(WebFragment::class.simpleName, "bottom_navigation_view is null")
//            margin.visibility = View.GONE
//        }

        arguments?.run {this
            if(getBoolean("isBottomHideView", false)){
                margin.toGone()
            }else{
                margin.toVisible()
            }
        }



        webView = wbWebView
        isCleanHistory = true
        webView?.apply {
            this.clearCache(true)
        }

        wbWebView.settings.textZoom = 100
        /*scrollView.post {
            Log.d(WebFragment::class.simpleName, "scrollView.height : ${scrollView.height}")
            var layoutParams = wbWebView.layoutParams as FrameLayout.LayoutParams
            layoutParams.height = scrollView.height
            wbWebView.layoutParams = layoutParams
            wbWebView.requestLayout()
            wbWebView.invalidate()
        }*/

        if(arguments != null) {
            loadUrl(arguments!!.getString("url")!!)
        }
    }

    /**
     * 웹뷰 히스토리 클리어 변수 true
     */
    fun clearHistory(){
        isCleanHistory = true
    }

    fun loadUrl(url: String){
        Log.d(WebFragment::class.simpleName, "loadUrl("+url+") start")

        initWebViewSettings(wbWebView)
        webView!!.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                mFilePathCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("*/*")

                startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_CODE_FILE_CHOOSE);
//                startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSE)

                return true
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                Log.d(WebFragment::class.simpleName, view?.toString() + "("+view.tag+") onProgressChanged : " + newProgress)
                super.onProgressChanged(view, newProgress)

                if(newProgress > 80){
                    hideProgress()
                }

            }

            override fun onGeolocationPermissionsShowPrompt(origin: String,callback: GeolocationPermissions.Callback) {
                callback.invoke(origin, true, false)
            }
        }
        webView!!.webViewClient = object : WebViewClient() {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                val url = request?.url.toString() ?: ""
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return super.shouldOverrideUrlLoading(view, request)
                }
                else if (url.toLowerCase().startsWith("mailto:")) {
                    val mt = MailTo.parse(url)
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(mt.to))
                        startActivity(this)
                    }
                }else if (url.toLowerCase().startsWith("browser:")) {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url.replace("browser:", ""))
                        startActivity(this)
                    }
                }
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {urlData->
                    if (urlData.startsWith("http:") || urlData.startsWith("https:")) {
                        return super.shouldOverrideUrlLoading(view, urlData)
                    }
                    else if (urlData.toLowerCase().startsWith("mailto:")) {
                        val mt = MailTo.parse(urlData)
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(mt.to))
                            startActivity(this)
                        }
                    }
                    else if (url.toLowerCase().startsWith("browser:")) {
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(url.replace("browser:", ""))
                            startActivity(this)
                        }
                    }else{

                    }
                }
                return true
            }

            /*override fun shouldOverrideUrlLoading(webView: WebView?,request: WebResourceRequest?): Boolean {
                Log.d(WebFragment::class.simpleName, "shouldOverrideUrlLoading1 : " + webView?.url)

                if(!webView!!.url.contains(querystringCertifyToken())){
                    webView?.loadUrl(webView.url +if(webView.url.contains("?")){"&"}else{"?"}+ querystringCertifyToken())
                }
                *//*try{
                    webView?.postUrl(webView.url, querystringCertifyToken().toByteArray(Charset.forName("utf-8")))
                }catch(e: Exception){e.printStackTrace()}*//*

                return true
            }

            override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
                Log.d(WebFragment::class.simpleName, "shouldOverrideUrlLoading2 : " + url)

                if(!url!!.contains(querystringCertifyToken())){
                    webView?.loadUrl(url + if(url!!.contains("?")){"&"}else{"?"}+ querystringCertifyToken())
                }
                *//*try{
                    webView?.postUrl(url, querystringCertifyToken().toByteArray(Charset.forName("utf-8")))
                }catch(e: Exception){e.printStackTrace()}*//*

                return true
            }*/

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                Log.d(WebFragment::class.simpleName, "onLoadResource : " + url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(WebFragment::class.simpleName, "history page start : " + url)

                showProgress()

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(WebFragment::class.simpleName, "history onPageFinished : " + url)

                if(isCleanHistory){
                    isCleanHistory = false
                    webView?.clearHistory()
                }

                urlLast = url

                hideProgress()
            }

            override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
                super.onReceivedClientCertRequest(view, request)
                //forcing my private key
                Toast.makeText(activity, "onReceivedClientCertRequest call", Toast.LENGTH_SHORT).show()


//                X509Certificate cert = CertificateKey.getCertificate();
//                X509Certificate[] mCertificates = new X509Certificate[1];
//                mCertificates[0] = (X509Certificate)cert;
//
//                request.proceed(CertificateKey.getKey(), mCertificates);

            }



            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)

                hideProgress()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.w(WebFragment::class.simpleName, "onReceivedError("+view.url+") : " + error.description)
                }
                else{
                    Log.w(WebFragment::class.simpleName, "onReceivedError("+view.url+") : " + error.toString())
                }
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Log.w(WebFragment::class.simpleName, "onReceivedSslError("+view.url+") : " + error.toString())
//                }
//                else{
//                    Log.w(WebFragment::class.simpleName, "onReceivedSslError("+view.url+") : " + error.toString())
//                }

                var msg = ""
                if (error.primaryError == SslError.SSL_DATE_INVALID || error.primaryError == SslError.SSL_EXPIRED || error.primaryError == SslError.SSL_IDMISMATCH || error.primaryError == SslError.SSL_INVALID || error.primaryError == SslError.SSL_NOTYETVALID || error.primaryError == SslError.SSL_UNTRUSTED
                ) {
                    when (error.primaryError) {
                        SslError.SSL_DATE_INVALID -> {
                            msg = "The date of the certificate is invalid"
                        }
                        SslError.SSL_INVALID -> {
                            msg = "A generic error occurred"
                        }
                        SslError.SSL_EXPIRED -> {
                            msg = "The certificate has expired"
                        }
                        SslError.SSL_IDMISMATCH -> {
                            msg = "Hostname mismatch"
                        }
                        SslError.SSL_NOTYETVALID -> {
                            msg = "The certificate is not yet valid"
                        }
                        SslError.SSL_UNTRUSTED -> {
                            msg = "The certificate authority is not trusted"
                        }
                    }
                }

                val builder = AlertDialog.Builder(activity)
                builder.setMessage(msg)
                builder.setPositiveButton("continue",
                    DialogInterface.OnClickListener { dialog, which -> handler.proceed() })
                builder.setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
                val dialog = builder.create()
                dialog.show()


//                handler.proceed()
            }
        }
        webView!!.addJavascriptInterface(AndroidBridge(), "HybridApp")
        webView!!.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            try{
                Log.d(WebFragment::class.simpleName, "download($url, $userAgent, $contentDisposition, $mimetype, $contentLength)")

                /*var fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                Log.d(WebFragment::class.simpleName, "fileName : " + fileName)

                activity!!.startService(DownloadService.getDownloadService(
                    activity!!,
                    "https://cloudup.com/files/inYVmLryD4p/download",
                    Environment.DIRECTORY_DOWNLOADS + "/",
                    fileName
                ))
                Toast.makeText(activity!!.applicationContext, "downloading file...", Toast.LENGTH_LONG).show()*/

                val request = DownloadManager.Request(Uri.parse("https://t1.daumcdn.net/cfile/tistory/244BAD4D52BBC3A429"))

                var directoryDownloads = Environment.DIRECTORY_DOWNLOADS + "/"
                Log.d(WebFragment::class.simpleName, "directoryDownloads : " + directoryDownloads)
                var fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                Log.d(WebFragment::class.simpleName, "fileName : " + fileName)

                request.setDescription(fileName)
                request.setTitle(fileName)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE + DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(directoryDownloads, fileName)
                val dm = activity!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
                dm!!.enqueue(request)
                Toast.makeText(activity!!.applicationContext, "downloading file...", Toast.LENGTH_LONG).show()
            }catch(e: Exception){e.printStackTrace()}
        }

        webView!!.loadUrl(url +if(url.contains("?")){"&"}else{"?"}+ querystringCertifyToken())
    }



    private fun querystringCertifyToken(): String{

        val CLNT_NO = PreferenceUtils.getUserId()
        val MOBILE_TOCKEN = Preference.getServerToken(BaseApplication.getAppContext())

        var url2 = "CLNT_NO=$CLNT_NO"
        url2 = url2 + "&MOBILE_TOCKEN=$MOBILE_TOCKEN"
        Log.d(WebFragment::class.simpleName, "querystringCertifyToken : " + url2)

        return url2
    }

    fun showProgress(){
        progress_bar_login?.visibility = View.VISIBLE
    }
    fun hideProgress(){
        progress_bar_login?.visibility = View.INVISIBLE
    }

    fun evaluateJavascript(method: String, json: String?){
        Log.d(WebFragment::class.simpleName, "evaluateJavascript($method, $json)")
        var json2 = json
        if(json.isNullOrEmpty()){
            json2 = "{}"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView?.evaluateJavascript("$method('$json2');", null);
        } else {
            webView?.loadUrl("javascript:$method('$json2');");
        }

        //webView?.loadUrl("javascript:console.log('evaluateJavascript test');");
    }

    fun goPageJavascript(url: String, json: String?){
        Log.d(WebFragment::class.simpleName, "goPageJavascript($url, $json)")
        var json2 = json
        if(json.isNullOrEmpty()){
            json2 = "{}"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //webView?.loadUrl("javascript:goPage('$url', '$json2');");
            webView?.evaluateJavascript("goPage('$url', '$json2');", null);
        } else {
            webView?.loadUrl("javascript:goPage('$url', '$json2');");
        }

        //webView?.loadUrl("javascript:console.log('evaluateJavascript test');");
    }

    private fun initWebViewSettings(webView: WebView) {
        webView.setInitialScale(0)
        webView.isVerticalScrollBarEnabled = true
        // Enable JavaScript
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        val manufacturer = android.os.Build.MANUFACTURER

        //We don't save any form data in the application
        settings.saveFormData = false
        settings.savePassword = false

        // Jellybean rightfully tried to lock this down. Too bad they didn't give us a whitelist
        // while we do this
        settings.allowUniversalAccessFromFileURLs = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.mediaPlaybackRequiresUserGesture = false
        }

        // Enable database
        // We keep this disabled because we use or shim to get around DOM_EXCEPTION_ERROR_16
        val databasePath = webView.context.applicationContext.getDir("database", Context.MODE_PRIVATE).path
        settings.databaseEnabled = true
        settings.databasePath = databasePath


        //Determine whether we're in debug or release mode, and turn on Debugging!
        val appInfo = webView.context.applicationContext.applicationInfo
        if (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }

        settings.setGeolocationDatabasePath(databasePath)

        // Enable DOM storage
        settings.domStorageEnabled = true

        // Enable built-in geolocation
        settings.setGeolocationEnabled(true)

        // Enable AppCache
        // Fix for CB-2282
        settings.setAppCacheMaxSize((5 * 1048576).toLong())
        settings.setAppCachePath(databasePath)
        settings.setAppCacheEnabled(true)

        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Fix for CB-1405
        // Google issue 4641
        val defaultUserAgent = settings.userAgentString
        settings.userAgentString = defaultUserAgent

        settings.allowFileAccess = true
        settings.setSupportMultipleWindows(false)


        //settings.setBuiltInZoomControls(true)
        //settings.setLoadWithOverviewMode(true)
        //settings.setUseWideViewPort(true)

        // Fix for CB-3360
        /*String overrideUserAgent = preferences.getString("OverrideUserAgent", null);
        if (overrideUserAgent != null) {
            settings.setUserAgentString(overrideUserAgent);
        } else {
            String appendUserAgent = preferences.getString("AppendUserAgent", null);
            if (appendUserAgent != null) {
                settings.setUserAgentString(defaultUserAgent + " " + appendUserAgent);
            }
        }*/
        // End CB-3360
    }

    override fun onDestroy() {

        webView?.let {
            it.clearCache(true)
            it.destroy()
            hideProgress()
        }
        super.onDestroy()
    }

    internal inner class AndroidBridge {

        @JavascriptInterface
        fun openChatModalChannelList(arg: String) {
            Log.d(WebFragment::class.simpleName, "openChatModalChannelList("+arg+")")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    ActivityUtil.startChatListActivity(requireActivity(), loginId = json.getString("LOGIN_ID"))

//                    var intent = Intent(context, ChatListActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    intent.putExtra("loginId", json.getString("LOGIN_ID"))
//                    context?.startActivity(intent)
                }
            });

        }

        @JavascriptInterface
        fun openChatModalChannel(arg: String) {
            Log.d(WebFragment::class.simpleName, "openChatModalChannel("+arg+")")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)


                    ActivityUtil.startChatListActivity(requireActivity(), json.getString("CHANNEL_URL"), json.getString("CHANNEL_TITLE"), json.getString("ORDER_NO"), json.getString("LOGIN_ID"), true)

//                    Intent(context, ChatListActivity::class.java).apply {
//                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        putExtra("groupChannelUrl", json.getString("CHANNEL_URL"))
//                        putExtra("groupChannelTitle", try{json.getString("CHANNEL_TITLE")}catch(e:Exception){""})
//                        putExtra("orderNo", json.getString("ORDER_NO"))
//                        putExtra("loginId", json.getString("LOGIN_ID"))
//                        putExtra("detailMove", true)
//                        context?.startActivity(this)
//                    }

                }
            });

        }

        @JavascriptInterface
        fun openNltdAllSearch(arg: String?) {
            Log.d(WebFragment::class.simpleName, "openNltdAllSearch($arg)")

            mHandler.post(object: Runnable{
                override fun run() {

                    ActivityUtil.startSearchActivityResult(requireActivity(), OrderWriteActivity.STOCK_SEARCH)

//                    startActivityForResult(Intent(activity, SearchActivity::class.java),
//                        OrderWriteActivity.STOCK_SEARCH
//                    )
                }
            });

        }

        @JavascriptInterface
        fun fnOpenPassRemote(arg: String?) {
            Log.d(WebFragment::class.simpleName, "fnOpenPassRemote($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    initializeMyPass {
                        Log.d(WebFragment::class.simpleName, "SKCertManager.sign invoke")
                        SKCertManager.sign(SKConstant.REQUEST_CODE_RANDOM_KOSCOM, null, "ServerRandom".toByteArray(), SKConstant.AUTH_TYPE_ALL, false, ComUtil.policyMode, null,
                            SKCallback.SignCallback { requestCode, resultCode, resultMessage, binSignData, b64Cert, isTrustZone ->

                                Log.d(
                                    LoginActivity::class.simpleName,
                                    "SKCertManager.sign result : " + resultCode
                                )

                                if (resultCode == SKConstant.RESULT_CODE_OK) {
                                    //Log.v("MyPassClient", "binSignData : " + String(binSignData))
                                    //Log.v("MyPassClient", "Sign result(hex) : " + SKUtil.bin2hex(binSignData))

                                    // 샘플 앱에서는 서명 검증 구현이 생략되어 있습니다.
                                    // 서명 검증은 서버 모듈의 기능을 참조하여 주십시오.

                                    val certInfo = SKSystemCertInfo(SKUtil.b642bin(b64Cert))

                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.subjectDN : " + certInfo.subjectDN
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.issuerDN : " + certInfo.issuerDN
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.b64PublicKeyInfo : " + certInfo.b64PublicKeyInfo
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.serialNumber : " + certInfo.serialNumber
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.certPolicyIdString : " + certInfo.certPolicyIdString
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.issuerName : " + certInfo.issuerName
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.signAlgorithmOidString : " + certInfo.signAlgorithmOidString
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.signAlgorithmName : " + certInfo.signAlgorithmName
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.subjectCommonName : " + certInfo.subjectCommonName
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.version : " + certInfo.version
                                    )

                                    // 서명 검증, 비식별 아이디 전달 완료 후 로그인 완료 페이지로 이동
                                    val strSubjectDN =
                                        if ((certInfo == null)) "" else certInfo.subjectDN

                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.subjectDN : " + strSubjectDN
                                    )

                                    resultCertify(
                                        "4",
                                        binSignData,
                                        json.getString("CLNT_CNF_NO"),
                                        { securityNum: String?, dn: String, signature: String, publicKey: String, name: String ->

                                            evaluateJavascript(
                                                "getOpenPassData",
                                                "{\"DN\":\"" + dn + "\", \"PUBLIC_KEY\":\"" + publicKey + "\", \"NAME\":\"" + name + "\"}"
                                            )
                                        })

                                } else if (resultCode == SKConstant.RESULT_CODE_ERROR_SESSION_EXPIRED) {
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "RESULT_CODE_ERROR_SESSION_EXPIRED"
                                    )
                                } else {
                                    // 에러 처리
                                    // 만들어진 단축서명용 전자서명 생성키 삭제
                                    // SKCertManager.clear();

                                    Toast.makeText(
                                        activity!!.getApplicationContext(),
                                        "[SKCertManager.sign()] ERR($resultCode\n$resultMessage)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
            });

        }

        @JavascriptInterface
        fun fnOpenPassRemoteL(arg: String?) {
            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    initializeMyPass {
                        Log.d(WebFragment::class.simpleName, "SKCertManager.sign invoke")
                        SKCertManager.sign(SKConstant.REQUEST_CODE_KOSCOM_FULL_SIGN, null, "ServerRandom".toByteArray(), SKConstant.AUTH_TYPE_ALL, false,ComUtil.policyMode,null,
                            SKCallback.SignCallback { requestCode, resultCode, resultMessage, binSignData, b64Cert, isTrustZone ->

                                Log.d(
                                    LoginActivity::class.simpleName,
                                    "SKCertManager.sign result : " + resultCode
                                )

                                if (resultCode == SKConstant.RESULT_CODE_OK) {
                                    //Log.v("MyPassClient", "binSignData : " + String(binSignData))
                                    //Log.v("OpenPassClient", "Sign result(hex) : " + SKUtil.bin2hex(binSignData))

                                    // 샘플 앱에서는 서명 검증 구현이 생략되어 있습니다.
                                    // 서명 검증은 서버 모듈의 기능을 참조하여 주십시오.

                                    val certInfo = SKSystemCertInfo(SKUtil.b642bin(b64Cert))

                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.subjectDN : " + certInfo.subjectDN
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.issuerDN : " + certInfo.issuerDN
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.b64PublicKeyInfo : " + certInfo.b64PublicKeyInfo
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.serialNumber : " + certInfo.serialNumber
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.certPolicyIdString : " + certInfo.certPolicyIdString
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.issuerName : " + certInfo.issuerName
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.signAlgorithmOidString : " + certInfo.signAlgorithmOidString
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.signAlgorithmName : " + certInfo.signAlgorithmName
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.subjectCommonName : " + certInfo.subjectCommonName
                                    )
                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.version : " + certInfo.version
                                    )

                                    // 서명 검증, 비식별 아이디 전달 완료 후 로그인 완료 페이지로 이동
                                    val strSubjectDN =
                                        if ((certInfo == null)) "" else certInfo.subjectDN

                                    Log.d(
                                        LoginActivity::class.simpleName,
                                        "certInfo.subjectDN : " + strSubjectDN
                                    )

                                    resultCertify(
                                        "1",
                                        binSignData,
                                        "",
                                        { securityNum: String?, dn: String, signature: String, publicKey: String, name: String ->

                                            evaluateJavascript(
                                                "getOpenPassData",
                                                "{\"DN\":\"" + dn + "\", \"PUBLIC_KEY\":\"" + publicKey + "\", \"NAME\":\"" + name + "\"}"
                                            )
                                        })

                                } else if (resultCode == SKConstant.RESULT_CODE_ERROR_SESSION_EXPIRED) {
                                    Log.e(
                                        LoginActivity::class.simpleName,
                                        "RESULT_CODE_ERROR_SESSION_EXPIRED"
                                    )
                                } else {
                                    // 에러 처리
                                    // 만들어진 단축서명용 전자서명 생성키 삭제
                                    // SKCertManager.clear();

                                    Toast.makeText(
                                        activity!!.getApplicationContext(),
                                        "[SKCertManager.sign()] ERR($resultCode\n$resultMessage)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
            });
        }

        @JavascriptInterface
        fun fnOpenPassRemoteJ(arg: String?) {
            fnOpenPassRemote(arg)
        }

        @JavascriptInterface
        fun fnDidRemote(arg: String?){
            Log.d(WebFragment::class.simpleName, "fnDidRemote($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    initializeMyPass {
                        Log.d(WebFragment::class.simpleName, "SKCertManager.sign invoke")
                        SKCertManager.sign(SKConstant.REQUEST_CODE_RANDOM_KOSCOM, null, "ServerRandom".toByteArray(), SKConstant.AUTH_TYPE_ALL, false,ComUtil.policyMode,null,
                            SKCallback.SignCallback { requestCode, resultCode, resultMessage, binSignData, b64Cert, isTrustZone ->

                                Log.d(
                                    RegistActivity::class.simpleName,
                                    "SKCertManager.sign result : " + resultCode
                                )

                                if (resultCode == SKConstant.RESULT_CODE_OK) {
                                    //Log.v("DidClient", "binSignData : " + String(binSignData))
                                    //Log.v("DidClient", "Sign result(hex) : " + SKUtil.bin2hex(binSignData))

                                    // 샘플 앱에서는 서명 검증 구현이 생략되어 있습니다.
                                    // 서명 검증은 서버 모듈의 기능을 참조하여 주십시오.

                                    val certInfo = SKSystemCertInfo(SKUtil.b642bin(b64Cert))

                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.subjectDN : " + certInfo.subjectDN
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.issuerDN : " + certInfo.issuerDN
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.b64PublicKeyInfo : " + certInfo.b64PublicKeyInfo
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.serialNumber : " + certInfo.serialNumber
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.certPolicyIdString : " + certInfo.certPolicyIdString
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.issuerName : " + certInfo.issuerName
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.signAlgorithmOidString : " + certInfo.signAlgorithmOidString
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.signAlgorithmName : " + certInfo.signAlgorithmName
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.subjectCommonName : " + certInfo.subjectCommonName
                                    )
                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.version : " + certInfo.version
                                    )

                                    // 서명 검증, 비식별 아이디 전달 완료 후 로그인 완료 페이지로 이동
                                    val strSubjectDN =
                                        if ((certInfo == null)) "" else certInfo.subjectDN

                                    Log.d(
                                        RegistActivity::class.simpleName,
                                        "certInfo.subjectDN : " + strSubjectDN
                                    )

                                    resultCertifyDID(
                                        "4",
                                        binSignData,
                                        json.getString("CLNT_CNF_NO"),
                                        { securityNum: String?, dn: String, signature: String, publicKey: String, name: String ->

                                            //evaluateJavascript("getOpenPassData", "{\"DN\":\""+dn+"\", \"PUBLIC_KEY\":\""+publicKey+"\", \"NAME\":\""+name+"\"}")
                                        })

                                } else if (resultCode == SKConstant.RESULT_CODE_ERROR_SESSION_EXPIRED) {
                                    Log.e(
                                        RegistActivity::class.simpleName,
                                        "RESULT_CODE_ERROR_SESSION_EXPIRED"
                                    )
                                } else {
                                    // 에러 처리
                                    // 만들어진 단축서명용 전자서명 생성키 삭제
                                    // SKCertManager.clear();

                                    Toast.makeText(
                                        activity!!.getApplicationContext(),
                                        "[SKCertManager.sign()] ERR($resultCode\n$resultMessage)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
            });
        }

        @JavascriptInterface
        fun fnDidRemoteL(arg: String?){
            Log.d(WebFragment::class.simpleName, "fnDidRemote($arg)")

            ComUtil.fromWebView = true

            mHandler.post(object: Runnable{
                override fun run() {

                    getSkdid("L"){ didNonce, didSvcPublic, date, seq, type->

                        getIntentData(didNonce,didSvcPublic,type) {pairwise: String?, publicKey: String? ->

                            evaluateJavascript("getOpenPassData", "{\"DN\":\""+pairwise+"\", \"PUBLIC_KEY\":\""+""+"\", \"NAME\":\""+""+"\"}")

                        }

                    }

                }
            })
        }

        @JavascriptInterface
        fun fnDidRemoteJ(arg: String?){
            Log.d(WebFragment::class.simpleName, "fnDidRemoteJ($arg)")

            ComUtil.fromWebView = true
            ComUtil.stringUrl = urlLast

            mHandler.post(object: Runnable{
                override fun run() {

                    getSkdid("J"){ didNonce, didSvcPublic, date, seq, type ->

                        getIntentData(didNonce,didSvcPublic,type) {pairwise: String?, publicKey: String? ->

                            getSkDidInfo(didNonce,"J",date,seq) {name: String, xpirDate : String ->
                                evaluateJavascript("getOpenPassData", "{\"DN\":\""+pairwise+"\", \"PUBLIC_KEY\":\""+publicKey+"\", \"NAME\":\""+name+"\", \"XPIR_DATE\" : \""+xpirDate+"\"}")
                            }

                        }

                    }

                }
            })

        }


        @JavascriptInterface
        fun file_download(arg: String?) {
            Log.d(WebFragment::class.simpleName, "file_download($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    kr.co.koscom.omp.view.DownloadManager().download(json.getString("file_path"), json.getString("org_file_name"), json.getString("sys_file_name"),
                        PreferenceUtils.getUserId(), Preference.getServerToken(activity!!.applicationContext)!!) { file ->

                        try {
                            val data = FileProvider.getUriForFile(activity!!.applicationContext, BuildConfig.APPLICATION_ID+".fileProvider", file)
                            val type = WebUtil.getMimeType(data.toString())


                            val intent = Intent(android.content.Intent.ACTION_VIEW)
                            intent.setDataAndType(data, type)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            startActivity(intent)

                        } catch (e: ActivityNotFoundException) {
                            val position = file.name.lastIndexOf(".")
                            val searchWord = file.name.substring(position+1, file.name.length)
                            ViewUtils.alertCustomDialog(activity!!, R.string.web_not_form_file_contents.toResString(),null, R.string.search.toResString(),{

                            },{
                                ActivityUtil.startMarketSearch(activity!!, searchWord)
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

//                        var snackbar = Snackbar.make(activity!!.window.decorView.getRootView(), StringUtils.substringAfterLast(file.parent,"/") + "에 다운로드를 완료했습니다.\n" +
//                                "파일을 열겠습니까?", Snackbar.LENGTH_LONG)
//
//                        snackbar.setAction("확인", object : View.OnClickListener{
//                            override fun onClick(v: View?) {
//                                snackbar.dismiss()
//
//                                try {
//                                    val data = FileProvider.getUriForFile(activity!!.applicationContext, BuildConfig.APPLICATION_ID+".fileProvider", file)
//                                    val type = WebUtil.getMimeType(data.toString())
//
//                                    val intent = Intent(android.content.Intent.ACTION_VIEW)
//                                    intent.setDataAndType(data, type)
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                                    startActivity(intent)
//
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        })
//                        snackbar.show()
                    }

                    Toast.makeText(activity!!.applicationContext, "downloading file...", Toast.LENGTH_SHORT).show()

                    //webView!!.postUrl(BuildConfig.SERVER_URL + "/common/fileDownLoad", "file_path=${json.getString("file_path")}&org_file_name=${json.getString("org_file_name")}&sys_file_name=${json.getString("sys_file_name")}".toByteArray(Charset.forName("utf-8")))
                    //webView!!.loadUrl(url +if(url.contains("?")){"&"}else{"?"}+ querystringCertifyToken())
                }
            });

        }

        @JavascriptInterface
        fun excel_download_mobile(arg: String?) {
            Log.d(WebFragment::class.simpleName, "excel_download_mobile($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    //var json = JSONObject(arg)

                    kr.co.koscom.omp.view.DownloadManager().downloadExcel(PreferenceUtils.getUserId(), Preference.getServerToken(activity!!.applicationContext)!!) { file ->

                        try {
                            val data = FileProvider.getUriForFile(activity!!.applicationContext, BuildConfig.APPLICATION_ID+".fileProvider", file)
                            val type = WebUtil.getMimeType(data.toString())

                            val intent = Intent(android.content.Intent.ACTION_VIEW)
                            intent.setDataAndType(data, type)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            startActivity(intent)

                        } catch (e: ActivityNotFoundException) {
                            val position = file.name.lastIndexOf(".")
                            val searchWord = file.name.substring(position+1, file.name.length)
                            ViewUtils.alertCustomDialog(activity!!, R.string.web_not_form_file_contents.toResString(),null, R.string.search.toResString(),{

                            },{
                                ActivityUtil.startMarketSearch(activity!!, searchWord)
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


//                        var snackbar = Snackbar.make(activity!!.window.decorView.rootView, StringUtils.substringAfterLast(file.parent,"/") + "에 ${file.name}를 다운로드했습니다.\n" +
//                                "파일을 열겠습니까?", Snackbar.LENGTH_LONG)
//
//                        snackbar.setAction("확인", object : View.OnClickListener{
//                            override fun onClick(v: View?) {
//                                snackbar.dismiss()
//
//                                try {
//                                    val data = FileProvider.getUriForFile(activity!!.applicationContext, BuildConfig.APPLICATION_ID+".fileProvider", file)
//                                    val type = WebUtil.getMimeType(data.toString())
//
//                                    val intent = Intent(android.content.Intent.ACTION_VIEW)
//                                    intent.setDataAndType(data, type)
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                                    startActivity(intent)
//
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        })
//                        snackbar.show()
                    }

                    Toast.makeText(activity!!.applicationContext, "downloading file...", Toast.LENGTH_SHORT).show()

                    //webView!!.postUrl(BuildConfig.SERVER_URL + "/common/fileDownLoad", "file_path=${json.getString("file_path")}&org_file_name=${json.getString("org_file_name")}&sys_file_name=${json.getString("sys_file_name")}".toByteArray(Charset.forName("utf-8")))
                    //webView!!.loadUrl(url +if(url.contains("?")){"&"}else{"?"}+ querystringCertifyToken())
                }
            });

        }

        @JavascriptInterface
        fun fnConfirmReqMaeSU(arg: String?) {
            Log.d(WebFragment::class.simpleName, "fnConfirmReqMaeSU($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    requestPaperOfBuyer(json.getString("ORDER_NO"), json.getString("CHANNEL_URL")){
//                        ViewUtils.alertDialog(activity!!, "성공적으로 발급요청했습니다."){
//
//                        }
                    }
                }
            });

        }

        @JavascriptInterface
        fun fnConfirmReqMaedo(arg: String?) {
            Log.d(WebFragment::class.simpleName, "fnConfirmReqMaedo($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    requestPaperOfSeller(json.getString("ORDER_NO"), json.getString("CHANNEL_URL")){
                        ViewUtils.alertDialog(activity!!, it){}
                    }
                }
            });

        }

        @JavascriptInterface
        fun fnChannelsLeave(arg: String?) {
            Log.d(WebFragment::class.simpleName, "fnChannelsLeave($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    exitChannel(json.getString("ORDER_NO"), json.getString("CHANNEL_URL")){

                        ActivityUtil.startChatListActivity(requireActivity())

//                        var intent = Intent(context, ChatListActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        context!!.startActivity(intent)
                    }
                }
            });
        }

        @JavascriptInterface
        fun openAppScheme(arg: String?) {
            Log.d(WebFragment::class.simpleName, "openAppScheme($arg)")

            mHandler.post(object: Runnable{
                override fun run() {
                    var json = JSONObject(arg)

                    var open = json.getString("APPSCHEME")
                    if(open == "LOGIN"){
                        ActivityUtil.startLoginActivity(requireActivity())

//                        var intent = Intent(context, LoginActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        context!!.startActivity(intent)
                    }
                    else if(open == "MAIN"){
                        ActivityUtil.startMainNewActivity(activity!!)
//                        var intent = Intent(context, MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                        context!!.startActivity(intent)
                    }
                    else if(open == "LOGIN_TOKEN"){

                        ViewUtils.alertDialog(activity!!, "세션이 만료되었습니다.\n로그인 페이지로 이동합니다.") {
                            ActivityUtil.startLoginActivity(requireActivity())
                    //                            var intent = Intent(context, LoginActivity::class.java)
                    //                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    //                            context!!.startActivity(intent)

                            activity?.finish()
                            activity?.overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
                        }
                    }
                    else if(open == "LOGOUT"){
                        ActivityUtil.startCleanLoginActivity(requireActivity())
//                        val intent = Intent(activity, LoginActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
                        activity?.finish()
                        activity?.overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
                    }
                }
            });

        }

        @JavascriptInterface
        fun fnOpenXkeypad(arg: String?){
            Log.d(WebFragment::class.java.simpleName, "fnOpenXkeypad 호출 $arg")
            webView?.scrollTo(0,99999999)
        }

        @JavascriptInterface
        fun fnJoinInfoMng(arg: String?){
            Log.d(WebFragment::class.simpleName, "fnJoinInfoMng(arg: String)")
            isCleanHistory = true
            webView?.post {
                loadUrl(BuildConfig.SERVER_URL + "/mobile/mypage/joinInfoMng?LOGIN_ID="+PreferenceUtils.getUserId()+"&AUTH_TYPE="+PreferenceUtils.getLoginType())
            }
        }

        @JavascriptInterface
        fun fnReload(arg: String?){
            Log.d(WebFragment::class.simpleName, "fnReload(arg: String)")
            webView?.post {
                webView?.evaluateJavascript("javascript:location.reload()") {

                }
            }

        }

    }

    private fun getSkdid(type: String, listener: (didNonce: String, didSvcPublic: String, date : String, seq : String, type : String) -> Unit){

        disposable.add(loginViewModel.getSkdid(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    if(listener != null){
                        listener.invoke(it.DID_NONCE!!, it.DID_SVC_PUBLIC!!, it.DATE!!, it.SEQ!!, type)
                    }
                }else{
                    ViewUtils.alertDialog(webView!!.context.applicationContext, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(webView!!.context.applicationContext, "네트워크상태를 확인해주세요."){}
            }))

    }

    private fun getIntentData(didNonce: String, didSvcPublic: String, type : String,  listener: (pairwise: String?, publicKey: String?) -> Unit) {

        ComUtil.handler = listener

        Log.d("LoginActivity", "didNonce : $didNonce, didSvcPublic : $didSvcPublic")
        var typeName : String
        if (type.equals("L")){
            typeName = "login"
        } else {
            typeName = "verify"
        }

        val url = "initial://reqService?orgName=Koscom&vcType=$typeName&svcPublicDID=${Base64Utils.getBase64encode(didSvcPublic)}&nonce=${Base64Utils.getBase64encode(didNonce)}"
        Log.d("WebFragment", "getIntentData did scheme url : " + url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun getSkDidInfo(nonce : String?, type: String?, date : String?, seq : String?, listener: (name: String, xpirDate : String) -> Unit){

        disposable.add(loginViewModel.getSkDidInfo(nonce,type,date,seq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    if(listener != null){
                        listener.invoke(it.CLNT_HANGL_NM!!, it.XPIR_DATE!!)
                    }
                }else{
                    ViewUtils.alertDialog(webView!!.context.applicationContext, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(webView!!.context.applicationContext, "네트워크상태를 확인해주세요."){}
            }))

    }


    private fun showProgressDialog(mTitle: String, mMessage: String){

        progress_bar_login?.visibility = View.VISIBLE
    }

    private fun initializeMyPass(listener: () -> Unit) {
        showProgressDialog("", "초기화 중입니다.")

        // 초기화 함수 호출.
        val nResult = SKCertManager.initMyPass(webView!!.context,
            LoginActivity.MY_LICENSE,
            LoginActivity.MY_LAUNCHMODE,
            SKCallback.MessageCallback { requestCode, resultCode, resultMessage ->

            progress_bar_login?.visibility = View.INVISIBLE

            if (resultCode == SKConstant.RESULT_CODE_ERROR_NOT_INSTALL ||
                resultCode == SKConstant.RESULT_CODE_ERROR_APP_DISABLED ||
                resultCode == SKConstant.RESULT_CODE_ERROR_NEED_UPDATE){
                // MyPass 설치 등 상태 관련 오류
                SKCertManager.showErrorPopup(resultCode)
            }
            else if (resultCode == SKConstant.RESULT_CODE_OK){
                Log.d(WebFragment::class.simpleName, "initializeMyPass success.")
                listener.invoke()
            }
            else {
                // MyPass 초기화 실패
                Toast.makeText(activity!!.applicationContext, resultMessage, Toast.LENGTH_LONG).show()
                progress_bar_login?.visibility = View.INVISIBLE
            }
        })

        if (SKConstant.RESULT_CODE_OK != nResult) {
            Toast.makeText(activity!!.applicationContext, "MyPass 초기화 중 오류가 발생하였습니다.($nResult)", Toast.LENGTH_SHORT)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(WebFragment::class.simpleName, "onActivityResult($requestCode, $resultCode, $data)")

        if(requestCode == OrderWriteActivity.STOCK_SEARCH){
            if(resultCode != Activity.RESULT_OK) return

            if(data != null){
                var stock = data.getSerializableExtra("stock") as Stock.ResultMap
                //stockName.text = stock?.STK_NM
                //ableCount.text = stock?.RSTRCT_QTY
                evaluateJavascript("returnData", "{\"STK_NM\":\"${stock?.STK_NM}\",\"STK_CODE\":\"${stock?.STK_CODE}\"}")
            }
        }
        else if(requestCode == REQUEST_CODE_FILE_CHOOSE){
            data?.let {
                if(it.data != null){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        mFilePathCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, it))
                    }
                    else{
                        mFilePathCallback?.onReceiveValue(arrayOf(it.data!!))
                    }
                }
            }?: run {
                mFilePathCallback?.onReceiveValue(null)
            }

        }

    }

    private fun resultCertify(opCode: String, signData: String, snData: String, listener: (securityNum: String?, dn: String, signature: String, publicKey: String, name: String) -> Unit){
        disposable.add(loginViewModel.resultCertMyPass(opCode, signData, snData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    Log.d(LoginActivity::class.java.simpleName,"resultCertify : success")

                    if(listener != null){
                        listener.invoke(it.SECURITY_NUM, it.DN!!, it.SIGNATURE!!, it.PUBLIC_KEY!!, it.NAME!!)
                    }
                }else{
                    ViewUtils.alertDialog(activity!!, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun resultCertifyDID(opCode: String, signData: String, snData: String, listener: (securityNum: String?, dn: String, signature: String, publicKey: String, name: String) -> Unit){
        disposable.add(loginViewModel.resultCertMyPass(opCode, signData, snData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("SUCCESS".equals(it.result)){
                    Log.d(LoginActivity::class.java.simpleName,"resultCertify : success")

                    if(listener != null){
                        listener.invoke(it.SECURITY_NUM, it.DN!!, it.SIGNATURE!!, it.PUBLIC_KEY!!, it.NAME!!)
                    }

                    getSkdid("J"){ didNonce, didSvcPublic, date, seq, type ->

                        getIntentData(didNonce,didSvcPublic,type) {pairwise: String?, publicKey: String? ->

                            getSkDidInfo(didNonce,"J",date,seq) {name: String, xpirDate: String ->
                                evaluateJavascript("getOpenPassData", "{\"DN\":\""+pairwise+"\", \"PUBLIC_KEY\":\""+publicKey+"\", \"NAME\":\""+name+"\"}")
                            }

                        }

                    }

                }else{
                    ViewUtils.alertDialog(activity!!, "${it.result} : ${it.ERR_MSG}"){}

                }
            }, {
                it.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            }))
    }

    fun requestPaperOfSeller(orderNo: String, channelUrl: String, listener: (msg: String?) -> Unit) {

        disposable.add(chatViewModel.requestPaperOfSeller(
            PreferenceUtils.getUserId(),
            orderNo,
            channelUrl
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(GroupChannelActivity::class.java.simpleName, "response : $response")

                if ("0000" == response.rCode) {

                    listener.invoke(response.rMsg)
                } else {
                    ViewUtils.showErrorMsg(
                        activity!!,
                        response.rCode,
                        response.rMsg
                    )
                }
            }, { throwable ->
                throwable.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            })
        )
    }

    fun requestPaperOfBuyer(orderNo: String, channelUrl: String, listener: () -> Unit) {

        disposable.add(chatViewModel.requestPaperOfBuyer(
            PreferenceUtils.getUserId(),
            orderNo,
            channelUrl
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(GroupChannelActivity::class.java.simpleName, "response : $response")

                if ("0000" == response.rCode) {
                    ViewUtils.alertDialog(activity!!, response.rMsg){}
                    listener.invoke()
                } else {
                    ViewUtils.showErrorMsg(
                        activity!!,
                        response.rCode,
                        response.rMsg
                    )
                }
            }, { throwable ->
                throwable.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            })
        )
    }

    fun exitChannel(orderNo: String, channelUrl: String, listener: () -> Unit) {

        disposable.add(chatViewModel.exitChannel(PreferenceUtils.getUserId(), orderNo, channelUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(GroupChannelActivity::class.java.simpleName, "response : $response")

                if ("0000" == response.rCode) {

                    listener.invoke()
                } else {
                    ViewUtils.showErrorMsg(
                        activity!!,
                        response.rCode,
                        response.rMsg
                    )
                }
            }, { throwable ->
                throwable.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            })
        )
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    companion object{
        val REQUEST_CODE_FILE_CHOOSE = 2222
    }
}
