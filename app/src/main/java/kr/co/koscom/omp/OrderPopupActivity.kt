package kr.co.koscom.omp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.scsoft.boribori.data.viewmodel.OrderViewModel
import com.sendbird.syncmanager.utils.DateUtils
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_popup.*
import kotlinx.android.synthetic.main.view_order_status1.*
import kotlinx.android.synthetic.main.view_order_status2.*
import kotlinx.android.synthetic.main.view_order_status3.*
import kotlinx.android.synthetic.main.view_order_status4.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.view.ViewUtils
import java.text.DecimalFormat


/**
 * 주문정보 팝업
 */

class OrderPopupActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var chatViewModel: ChatViewModel

    private val disposable = CompositeDisposable()

    private var orderDetail: OrderDetail.OrderData? = null

    private val numberFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_popup)

        viewModelFactory = Injection.provideViewModelFactory(this)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)

        btnClose.setOnClickListener {
            finish()
        }

        btnOk.setOnClickListener {
            requestNego()
        }

        var param = intent.getSerializableExtra("orderItem") as Order.OrderItem
        search(param)

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    private fun search(param: Order.OrderItem){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(orderViewModel.orderDetail(param.ORDER_NO!!, param.PBLS_CLNT_NO!!, param.STK_CODE!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    orderDetail = it.datas?.result

                    stockName.text = it.datas?.result?.STK_NM
                    stockGubn.text = it.datas?.result?.STOCK_TP_CODE_NM
                    orderGubn.text = if(it.datas?.result?.DEAL_TP == "10"){"매도"}else if(it.datas?.result?.DEAL_TP == "20"){"매수"}else{""}
                    count.text = numberFormat.format(it.datas?.result?.DEAL_QTY ?: 0)
                    price.text = numberFormat.format(it.datas?.result?.DEAL_UPRC ?: 0)
                    left.text = numberFormat.format(it.datas?.result?.RMQTY ?: 0)
                    prePrice.text = numberFormat.format(it.datas?.result?.LST_PRC ?: 0)

                    status1.visibility = View.GONE
                    status2.visibility = View.GONE
                    status3.visibility = View.GONE
                    status4.visibility = View.GONE

                    if(it.datas?.result?.POST_ORD_STAT_CODE == "0"){
                        if(it.datas?.result?.DEAL_TP == "10"){
                            status1.visibility = View.VISIBLE
                        }
                        else{
                            status2.visibility = View.VISIBLE
                        }
                    }
                    else if(it.datas?.result?.POST_ORD_STAT_CODE == "1"){
                        if(it.datas?.result?.RMQTY ?: 0 > 0){
                            status4.visibility = View.VISIBLE
                        }
                        else{
                            status3.visibility = View.VISIBLE
                        }
                    }

                    register.text = it.datas?.result?.CLNT_HANGL_NM
                    regTime.text = it.datas?.result?.POST_TIME_FORM
                    message.text = it.datas?.result?.DEAL_CNDI

                    if(it.datas?.result?.POST_ORD_STAT_CODE == "0"){
                        btnOk.visibility = View.VISIBLE
                    }
                    else{
                        if(it.datas?.result?.POST_ORD_STAT_CODE == "1"){
                            if(it.datas?.result?.RMQTY ?: 0 > 0){
                                btnOk.visibility = View.VISIBLE
                            }
                            else{
                                btnOk.visibility = View.GONE
                            }
                        }
                        else{
                            btnOk.visibility = View.GONE
                        }

                    }

                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }

                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun requestNego(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(chatViewModel.requestNego(PreferenceUtils.getUserId(), orderDetail!!.ORDER_NO!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    ViewUtils.alertDialog(this, "성공적으로 협상요청했습니다."){
                        finish()
                    }

                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    companion object {

    }
}
