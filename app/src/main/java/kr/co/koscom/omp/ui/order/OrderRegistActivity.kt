package kr.co.koscom.omp.ui.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_order_regist.*
import kr.co.koscom.omp.NavigationFragment
import kr.co.koscom.omp.R
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Certi
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.dialog.TransactionTargetDialog
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.enums.TransactionTargetType
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.ui.order.contract.OrderRegistContract
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.NumberTextWatcher
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.StringUtils
import toDealType
import java.nio.charset.Charset

class OrderRegistActivity : AppCompatActivity(), OrderRegistContract.View {
    internal val tag = this.javaClass.simpleName

    private lateinit var viewModelFactory: ViewModelFactory

    private val presenter: OrderRegistPresenter by lazy {
        OrderRegistPresenter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_regist)

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = NavigationFragment()
            transaction.replace(R.id.nav_view, fragment)
            transaction.commitAllowingStateLoss()
        }

        viewModelFactory = Injection.provideViewModelFactory(this)

        init()
        initListener()

        presenter.initViewModel(viewModelFactory)
        presenter.stock = intent.getSerializableExtra(Keys.INTENT_STOCK) as? Stock.ResultMap

        setStockData(presenter.dealType, presenter.stock)
        tvPublicOrder.callOnClick()

    }

    private fun init(){

        toolbar.initTitle(R.string.orderregist_title.toResString())
        toolbar.initData(this)
    }

    private fun initListener(){

        tvEventName.setOnClickListener {
            ivSearch.callOnClick()
        }

        ivSearch.setOnClickListener {
            ActivityUtil.startOrderSearchActivityResult(this, Constants.RESULT_CODE_ORDER_SEARCH)
        }

        tvCancel.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
        }

        ivCountClose.setOnClickListener {
            etCountContents.setText("")
        }

        ivPriceClose.setOnClickListener {
            etPriceContents.setText("")
        }

        ivTotalPriceClose.setOnClickListener {
            tvTotalPriceContents.text = ""
        }

        ivDealQuestion.setOnClickListener {
            if(layoutQuestion.visibility == View.VISIBLE){
                layoutQuestion.toGone()
            }else{
                layoutQuestion.toVisible()
            }
        }
        ivQuestionClose.setOnClickListener {
            layoutQuestion.toGone()
        }

        tvRegist.setOnClickListener {
            presenter.submitOrder(etCountContents.text.toString(), etPriceContents.text.toString(), etTradeConditionContents.text.toString(), tvPublicOrder.isSelected, tvPasswordContents.text.toString())
        }


        etCountContents.addTextChangedListener(NumberTextWatcher(etCountContents))
        etCountContents.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                calculateAmount()
                if(etCountContents.text.toString().trim().isNullOrEmpty()){
                    ivCountClose.toInvisible()
                }else{
                    ivCountClose.toVisible()
                }

            }

        })
        etPriceContents.addTextChangedListener(NumberTextWatcher(etPriceContents))
        etPriceContents.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                calculateAmount()
                if(etPriceContents.text.toString().trim().isNullOrEmpty()){
                    ivPriceClose.toInvisible()
                }else{
                    ivPriceClose.toVisible()
                }
            }
        })

        etTradeConditionContents.addTextChangedListener(object: TextWatcher {
            var str = ""

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val length = s.toString().toByteArray(Charset.forName("UTF-8")).size

//                messageLength.text = length.toString()
                tvTradeConditionCount.toTextfromHtml(R.string.orderregist_tradecount_contents.toResString(), length)

                if (length > 300) {
                    etTradeConditionContents.setText(str)
                    etTradeConditionContents.setSelection(str.length)

//                    messageLength.text = str.toByteArray(Charset.forName("UTF-8")).size.toString()

                    tvTradeConditionCount.toTextfromHtml(R.string.orderregist_tradecount_contents.toResString(), str.toByteArray(Charset.forName("UTF-8")).size)


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
                            presenter.getCertiNum()
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

    }


    /**
     * 주식 셋팅 (매수, 매도)
     */
    private fun setStockData(dealType: DealType? = DealType.ALL, stock: Stock.ResultMap?){
        stock?.let {
            tvEventName.text = it.STK_NM
            tvDealTypeTop.text = dealType?.type.toDealType()

            when(dealType){
                DealType.SELL ->{
                    //매도
                    tvDealTypeTop.setAppearance(this, R.style.F15B_blue_3348ae)
                    etPriceContents.setAppearance(this, R.style.F20B_blue_3348ae)
                    tvTotalPriceContents.setAppearance(this, R.style.F17R_blue_3348ae)
                    tvDealTypeBottom.toVisible()
                    ivDealQuestion.toVisible()
                    tvDealTypeBottom.toTextfromHtml(R.string.orderregist_sellcount_contents.toResString(), stock?.TRSF_ABLE_QTY.toNumberFormat())
                    tvRegist.background = R.drawable.shape_rect_fill5.toDrawable()
                }
                DealType.BUYING ->{
                    //매수
                    tvDealTypeTop.setAppearance(this, R.style.F15B_e8055a)
                    etPriceContents.setAppearance(this, R.style.F20B_e8055a)
                    tvTotalPriceContents.setAppearance(this, R.style.F17R_e8055a)
                    tvDealTypeBottom.toVisible()
                    ivDealQuestion.toInvisible()
                    tvDealTypeBottom.toTextfromHtml(R.string.orderregist_totalcount_contents.toResString(), stock?.FLTN_QTY.toNumberFormat())
                    tvRegist.background = R.drawable.shape_rect_fill27.toDrawable()
                }
                else ->{
                    tvDealTypeTop.setAppearance(this, R.style.F15B_blue_3348ae)
                    etPriceContents.setAppearance(this, R.style.F20B_blue_3348ae)
                    tvTotalPriceContents.setAppearance(this, R.style.F17R_blue_3348ae)
                    tvDealTypeBottom.text = ""
                    tvDealTypeBottom.toInvisible()
                    ivDealQuestion.toInvisible()
                    tvRegist.background = R.drawable.shape_rect_fill5.toDrawable()
                }
            }
        }?: run{
            tvDealTypeTop.setAppearance(this, R.style.F15B_blue_3348ae)
            etPriceContents.setAppearance(this, R.style.F20B_blue_3348ae)
            tvTotalPriceContents.setAppearance(this, R.style.F17R_blue_3348ae)
            tvDealTypeBottom.text = ""
            tvDealTypeBottom.toInvisible()
            ivDealQuestion.toInvisible()
            tvRegist.background = R.drawable.shape_rect_fill5.toDrawable()
        }
    }

    /**
     * 금액 계산
     */
    private fun calculateAmount(){

        if(!etCountContents.text.toString().trim().isNullOrEmpty() && !etPriceContents.text.toString().trim().isNullOrEmpty()){
            tvTotalPriceContents.text = (StringUtils.removeAll(etCountContents.text.toString(), ",").toLong() *
                    StringUtils.removeAll(etPriceContents.text.toString(), ",").toLong()).toString()
            tvTotalPriceContents.text = tvTotalPriceContents.text.toString().toNumberFormat()

        }
        else{
            tvTotalPriceContents.text = "0"
        }
    }

    /**
     * 인증번호 셋팅
     */
    override fun setCertiNum(certi: Certi) {
        tvPasswordContents.text = certi.datas?.certi_num ?: ""
        layoutPassword.toVisible()
    }

    override fun publicOrderClick(){
        tvPublicOrder.callOnClick()
    }

    /**
     * 등록 완료 후 페이지 종료
     */
    override fun submitComplete(msg: String){
        ViewUtils.alertDialog(this, msg) {
            OrderListNewActivity.REQUIRE_REFRESH = true
            finish()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }

    private fun loadingClose(){
    }


    /**
     * Loading Progress
     */
    override fun showProgress(isShow: Boolean){
        if(isShow){
            progress_bar_login.toVisible()
        }else{
            progress_bar_login.toInvisible()
        }
    }

    /**
     * Alert 다이얼로그
     */
    override fun alertDialog(msg: String){
        ViewUtils.alertDialog(this, msg){
        }
    }

    /**
     * Error Alert
     */
    override fun showErrorMsg(code: String?, msg: String?) {
        ViewUtils.showErrorMsg(this, code, msg)
    }

    override fun refresh(){
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            Constants.RESULT_CODE_ORDER_SEARCH ->{
                if(resultCode != Activity.RESULT_OK) return

                data?.let {
                    presenter.stock = it.getSerializableExtra(Keys.INTENT_STOCK) as? Stock.ResultMap
                    presenter.dealType = it.getSerializableExtra(Keys.INTENT_DEAL_TYPE) as? DealType ?: DealType.ALL

                    etCountContents.setText("")
                    etPriceContents.setText("")
                    calculateAmount()
                    etTradeConditionContents.setText("")

                    tvPublicOrder.callOnClick()

                    setStockData(presenter.dealType, presenter.stock)
                }
            }
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        toolbar.dispose()

        super.onDestroy()
    }


    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.END)){
            drawer_layout.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }




}
