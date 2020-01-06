package kr.co.koscom.omp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_escrow_seller.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Escrow
import kr.co.koscom.omp.view.ViewUtils
import java.text.DecimalFormat


/**
 * 매도자 에스크로 정보 안내
 */

class EscrowSellerActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var chatViewModel: ChatViewModel

    private val disposable = CompositeDisposable()

    var channelTitle: String? = null
    var channelUrl: String? = null
    var orderNo: String? = null
    var escrow: Escrow? = null
    var escrowListeners = mutableListOf<Runnable>()

    private val numberFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_escrow_seller)

        channelTitle = intent.getStringExtra("groupChannelTitle")
        channelUrl = intent.getStringExtra("groupChannelUrl")
        orderNo = intent.getStringExtra("orderNo")

        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)

        btnClose.setOnClickListener {
            finish()
        }
        btnOk.setOnClickListener {
            finish()
        }

        escrowListeners.add(object: Runnable{
            override fun run() {
                account.text = escrow!!.resultMap!!.VR_BNK_CODE_NM + "\n" + escrow!!.resultMap!!.VR_ACCNT_NO

                dealAmount.text = numberFormat.format(escrow!!.resultMap!!.TRSF_BNK_AMT!!.toFloat())
                fee.text = numberFormat.format(escrow!!.resultMap!!.TRD_AMT!!.toFloat())
                fee1.text = numberFormat.format(escrow!!.resultMap!!.TKOV_SYS_UTLFEE_RT!!.toFloat())
                fee2.text = numberFormat.format(escrow!!.resultMap!!.TKOV_WRT_SMSN_AMT!!.toFloat())
                totalAmount.text = numberFormat.format(escrow!!.resultMap!!.TRSF_FRTN_CMSN_AMT!!.toFloat())
            }
        })

        disposable.add(chatViewModel.getEscrow(
            PreferenceUtils.getUserId(),
            orderNo!!,
            channelUrl!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ escorwResponse ->
                Log.d(GroupChannelActivity::class.java.simpleName, "channel : $escorwResponse")

                if ("0000" == escorwResponse.rCode) {
                    escrow = escorwResponse

                    for (listener in escrowListeners) {
                        listener.run()
                    }
                } else {
                    ViewUtils.showErrorMsg(
                        this@EscrowSellerActivity,
                        escorwResponse.rCode,
                        escorwResponse.rMsg
                    )
                }
            }, { throwable ->
                throwable.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            })
        )

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    companion object {

    }
}
