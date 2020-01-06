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
import com.scsoft.boribori.data.viewmodel.OrderViewModel
import com.sendbird.syncmanager.utils.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contract_popup.*
import kotlinx.android.synthetic.main.view_contract_status2.*
import kotlinx.android.synthetic.main.view_contract_status3.*
import kotlinx.android.synthetic.main.view_contract_status4.*
import kotlinx.android.synthetic.main.view_contract_status5.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.view.ViewUtils
import java.text.DecimalFormat


/**
 * 계약 및 체결정보
 */

class ContractPopupActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private val numberFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_contract_popup)

        viewModelFactory = Injection.provideViewModelFactory(this)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        btnClose.setOnClickListener {
            finish()
        }

        var param = intent.getSerializableExtra("contractItem") as OrderContract.OrderContractItem
        search(param)

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    private fun search(param: OrderContract.OrderContractItem){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(orderViewModel.contractDetail(param.ORDER_NO!!, param.CHANNEL_URL)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    stockName.text = it.datas?.result?.STK_NM
                    seller.text = it.datas?.result?.MAEDO_NM
                    buyer.text = it.datas?.result?.MAESU_NM
                    count.text = numberFormat.format(it.datas?.result?.DEAL_QTY ?: 0)
                    price.text = numberFormat.format(it.datas?.result?.DEAL_UPRC ?: 0)
                    amount.text = numberFormat.format(it.datas?.result?.DEAL_QTY!! * it.datas?.result?.DEAL_UPRC!!)

                    dealTime.text = it.datas?.result?.CHG_DTTM_FORM

                    contract_status2.visibility = View.GONE
                    contract_status3.visibility = View.GONE
                    contract_status4.visibility = View.GONE
                    contract_status5.visibility = View.GONE
                    if(it.datas?.result?.NEGO_SETT_STAT_CODE == "204"){
                        contract_status4.visibility = View.VISIBLE
                    }
                    if(it.datas?.result?.NEGO_SETT_STAT_CODE == "206"){
                        contract_status5.visibility = View.VISIBLE
                    }
                    if(it.datas?.result?.NEGO_SETT_STAT_CODE == "400"
                        || it.datas?.result?.NEGO_SETT_STAT_CODE == "405"){
                        contract_status2.visibility = View.VISIBLE
                    }
                    if(it.datas?.result?.NEGO_SETT_STAT_CODE == "406"){
                        contract_status3.visibility = View.VISIBLE
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
