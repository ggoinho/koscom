package kr.co.koscom.omp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.sendbird.syncmanager.utils.DateUtils
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_buy_write.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import kr.co.koscom.omp.dialog.TransactionTargetDialog
import kr.co.koscom.omp.enums.TransactionTargetType
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toNumberFormat
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.ui.order.OrderListNewActivity
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.NumberTextWatcher
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.text.DecimalFormat

/**
 * 매수주문 등록
 */

class BuyWriteActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private var stock: Stock.ResultMap? = null

    private val numberFormat = DecimalFormat("#,###")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_buy_write)

        toolbar.initTitle("비상장주식거래")
        toolbar.initData(this)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        btnSearch.setOnClickListener {
            ActivityUtil.startOrderSearchActivityResult(this@BuyWriteActivity, STOCK_SEARCH)
//            startActivityForResult(Intent(this@BuyWriteActivity, OrderSearchActivity::class.java), STOCK_SEARCH)
        }
        stockName.setOnClickListener {
            btnSearch.callOnClick()
        }

        btnCancel.setOnClickListener {
            finish()
        }
        btnSave.setOnClickListener {
            submit()
        }

        btnCloseCountBuy.setOnClickListener {
            countBuy.setText("")
        }

        btnClosePriceBuy.setOnClickListener {
            priceBuy.setText("")
        }

        countBuy.addTextChangedListener(NumberTextWatcher(countBuy))
        countBuy.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                caculateAmount()
                if(countBuy.text.toString().trim().isNullOrEmpty()){
                    btnCloseCountBuy.visibility=View.GONE
                }else{
                    btnCloseCountBuy.visibility=View.VISIBLE
                }

            }

        })
        priceBuy.addTextChangedListener(NumberTextWatcher(priceBuy))
        priceBuy.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                caculateAmount()
                if(priceBuy.text.toString().trim().isNullOrEmpty()){
                    btnClosePriceBuy.visibility=View.GONE
                }else{
                    btnClosePriceBuy.visibility=View.VISIBLE
                }

            }

        })
        message.addTextChangedListener(object: TextWatcher {
            var str = ""

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val length = s.toString().toByteArray(Charset.forName("UTF-8")).size

                messageLength.text = length.toString()

                if (length > 300) {
                    message.setText(str)
                    message.setSelection(str.length)

                    messageLength.text = str.toByteArray(Charset.forName("UTF-8")).size.toString()
                }
                else{
                    str = s.toString()
                }
            }

        })

        tvPublicOrder.setOnClickListener {
            tvPasswordContents.text = ""
            layoutPassword.toGone()

            tvPublicOrder.isSelected = true
            tvPrivateOrder.isSelected = false
        }

        tvPrivateOrder.setOnClickListener {

            if(tvPrivateOrder.isSelected) return@setOnClickListener


            tvPublicOrder.isSelected = false
            tvPrivateOrder.isSelected = true

            TransactionTargetDialog.Builder(this)
                .setPositiveListener { transactionTarget ->
                    when(transactionTarget){
                        TransactionTargetType.SPECIFIC ->{
                            getCertiNum()
                        }
                        TransactionTargetType.UNSPECIFIC ->{
                            tvPasswordContents.text = ""
                            layoutPassword.toGone()
                        }
                        else ->{
                            tvPublicOrder.callOnClick()
                        }
                    }
                }
                .show()
        }


        stock = intent.getSerializableExtra("stock") as? Stock.ResultMap
        if(stock != null){
            stockName.text = stock?.STK_NM
            totalCount.text = stock?.FLTN_QTY?.toNumberFormat()
       }
        tvPublicOrder.isSelected = intent.getBooleanExtra("isPublicYn", true)
        tvPasswordContents.text  = intent.getStringExtra("passwordContents")
        if(tvPublicOrder.isSelected){
            tvPublicOrder.callOnClick()
        }else{
            tvPublicOrder.isSelected = false
            tvPrivateOrder.isSelected = true

            if(tvPasswordContents.text.isNullOrEmpty()){
                layoutPassword.toGone()
            }else{
                layoutPassword.toVisible()
            }
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0)
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun caculateAmount(){

        if(!countBuy.text.toString().trim().isNullOrEmpty() && !priceBuy.text.toString().trim().isNullOrEmpty()){
            amountBuy.setText((StringUtils.removeAll(countBuy.text.toString(),",").toLong() *
                    StringUtils.removeAll(priceBuy.text.toString(), ",").toLong()).toString())
            amountBuy.setText(numberFormat.format(amountBuy.getText().toString().toLong()))
        }
        else{
            amountBuy.setText("0")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == STOCK_SEARCH){
                if(data != null){

                    if(data.getStringExtra("medoYn") == "N"){
                        stock = data.getSerializableExtra("stock") as Stock.ResultMap
                        stockName.text = stock?.STK_NM
                        totalCount.text = stock?.FLTN_QTY?.toNumberFormat()

                    }
                    else{

                        ActivityUtil.startOrderWriteActivity(this, data.getSerializableExtra("stock"), tvPublicOrder.isSelected, tvPasswordContents.text.toString())

//                        var intent = Intent(this, OrderWriteActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        intent.putExtra("stock", data.getSerializableExtra("stock"))
//                        intent.putExtra("isPublicYn", tvPublicOrder.isSelected)
//                        intent.putExtra("passwordContents", tvPasswordContents.text.toString())
//                        startActivity(intent)

                        finish()
                    }
                }
            }
        }
    }

    private fun submit(){

        if(stock != null){

            disposable.add(orderViewModel.submitOrder(StringUtils.remove(priceBuy.text.toString(),","), "20", message.text.toString(),
                PreferenceUtils.getUserName(), PreferenceUtils.getUserId(), StringUtils.remove(countBuy.text.toString(),","),
                stock!!.STK_CODE!!, DateUtils.format3Date(System.currentTimeMillis()),
                if(tvPublicOrder.isSelected) "Y" else "N",
                tvPasswordContents.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if("0000".equals(it.rCode)){
                        ViewUtils.alertDialog(this@BuyWriteActivity, it.rMsg){
                            OrderListNewActivity.REQUIRE_REFRESH = true

                            finish()
                        }
                    }else{
                        ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                    }
                }, {
                    it.printStackTrace()
                    ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
                }))
        }
        else{
            ViewUtils.alertDialog(this, "종목을 선택하세요."){}

        }

    }

    /**
     * 인증번호 받아오는 API
     */
    private fun getCertiNum(){
        disposable.add(
            orderViewModel.getCertiNum(
                PreferenceUtils.getUserId(),
                Preference.getServerToken(BaseApplication.getAppContext()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if ("0000" == it.rCode) {
                        tvPasswordContents.text = it.datas?.certi_num ?: ""
                        layoutPassword.toVisible()
                    }else{
                        ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)
                        tvPublicOrder.callOnClick()
                    }
                }, {
                    it.printStackTrace()
                    ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요.") {}
                    tvPublicOrder.callOnClick()
                })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
        toolbar.dispose()
    }

    override fun onBackPressed() {

        if(drawer_layout!!.isDrawerOpen(GravityCompat.END)){
            drawer_layout?.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }

    companion object {
        val STOCK_SEARCH = 1111
    }
}
