package kr.co.koscom.omp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.syncmanager.utils.Base64Utils
import com.sendbird.syncmanager.utils.ComUtil

/**
 * DID 리턴값 리시버
 */

class DidReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uriData = intent.data
        var pairwiseDID = uriData!!.getQueryParameter("pairwiseDID")
        var publicKey = uriData!!.getQueryParameter("publicKey")
        val clientName = uriData!!.getQueryParameter("clientName")
        var signature = uriData!!.getQueryParameter("signature")

        if (pairwiseDID != null){
            pairwiseDID = Base64Utils.getBase64decode(uriData!!.getQueryParameter("pairwiseDID"))
        }

        if (publicKey != null){
            publicKey = Base64Utils.getBase64decode(uriData!!.getQueryParameter("publicKey"))
        }

        if (signature != null){
            signature = Base64Utils.getBase64decode(uriData!!.getQueryParameter("signature"))
        }

        Log.d("DidReceiverActivity", "uriData : " + uriData.toString())
        Log.d("DidReceiverActivity", "pairwiseDID : $pairwiseDID")
        Log.d("DidReceiverActivity", "publicKey : $publicKey")
        Log.d("DidReceiverActivity", "clientName : $clientName")
        Log.d("DidReceiverActivity", "signature : $signature")

        var goIntent : Intent? = null
        goIntent = if (uriData.toString().contains("verify")){
            if (ComUtil.fromWebView) {

                if (ComUtil.stringUrl?.contains("renew/invstSvJoinRenew")!!){
                    Intent(this, WebActivity::class.java)
                } else {
                    Intent(this, MyPageActivity::class.java)
                }

            } else {
                Intent(this, RegistActivity::class.java)
            }
        } else if (uriData.toString().contains("sign")){
            Intent(this, ContractDetailActivity::class.java)
        } else {
            if (ComUtil.fromWebView) {
                Intent(this, MyPageActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
        }

        //val goIntent : Intent? = Intent(this, LoginActivity::class.java)
        goIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        goIntent?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        goIntent?.putExtra("pairwiseDID", pairwiseDID)
        goIntent?.putExtra("publicKey", publicKey)
        goIntent?.putExtra("clientName", clientName)
        goIntent?.putExtra("signature", signature)

        startActivity(goIntent)

        finish()

    }
}
