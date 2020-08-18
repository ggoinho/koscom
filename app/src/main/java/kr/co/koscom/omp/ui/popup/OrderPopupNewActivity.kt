package kr.co.koscom.omp.ui.popup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import isMe
import kotlinx.android.synthetic.main.activity_order_detail_new.progress_bar_login
import kotlinx.android.synthetic.main.activity_order_popup_new.*
import kotlinx.android.synthetic.main.activity_order_popup_new.tvStockGubun
import kotlinx.android.synthetic.main.activity_order_popup_new.tvStockName
import kr.co.koscom.omp.R
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.NegotiationData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.enums.NegotiationEnterType
import kr.co.koscom.omp.enums.TransactionTargetType
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.ui.popup.contract.OrderPopupNewContract
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.ViewUtils
import orderStatus
import toDealType
import toDealTypeBackground

class OrderPopupNewActivity : AppCompatActivity(), OrderPopupNewContract.View {
    internal val tag = this.javaClass.simpleName

    private lateinit var viewModelFactory: ViewModelFactory

    private val presenter: OrderPopupNewPresenter by lazy {
        OrderPopupNewPresenter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_popup_new)

        viewModelFactory = Injection.provideViewModelFactory(this)

        init()
        initListener()

        presenter.initViewModel(viewModelFactory)

        val orderData = intent.getSerializableExtra(Keys.INTENT_ORDER_ITEM) as Order.OrderItem
        presenter.getOrderData(orderData)

    }

    private fun init(){

        customOrderStatusView.isClickableBtn = false


    }

    private fun initListener(){

        ivClose.setOnClickListener {
            onBackPressed()
        }

        layoutNegoRequest.setOnClickListener {
            presenter.orderDetail?.let {
                if(!it.PUBLIC_YN.isY()){
                    //비공개
                    if(it.PBLS_CLNT_NO.isMe()){
                        ViewUtils.alertDialog(this, R.string.orderpopup_popup_contents1.toResString()){}
                        return@let
                    }

                    ActivityUtil.startNegotiationPopupActivity(this, NegotiationData().apply {
                        orderNo = it.ORDER_NO ?:""
                        enterType = NegotiationEnterType.DETAIL
                        transactionTargetType = if(it.SECRET_CERTI_YN == "Y") TransactionTargetType.SPECIFIC else TransactionTargetType.UNSPECIFIC
                        registNickName = it.NICKNAME ?: ""
                        stockTpCodeNm = it.STOCK_TP_CODE_NM?: ""
                    })
                    finish()

                }else{
                    //공개
                    presenter.requestNego(it.ORDER_NO?: "")
                }
            }

        }

    }


    /**
     * 주문 데이터 셋팅
     */
    override fun setOrderData(order: OrderDetail.OrderData?) {

        order?.let {

            tvStockName.text = it.STK_NM
            tvStockGubun.text = it.STOCK_TP_CODE_NM

            //매수, 매도
            tvDealType.text = it.DEAL_TP.toDealType()
            it.DEAL_TP.toDealTypeBackground(tvDealType)
            tvCountContents.toDealType(it.DEAL_TP?:"")
            tvPriceContents.toDealType(it.DEAL_TP?:"")

            if(it.PUBLIC_YN == "Y"){
                ivSecret.toGone()
                tvTotalCountContents.toTextfromHtml(R.string.bold_regular_contents.toResString(), it.FLTN_QTY.toStringNumberFormat(), R.string.unit_stock.toResString())
                tvCountContents.toTextfromHtml(R.string.bold_regular_contents.toResString(), (it.DEAL_QTY ?: 0).toStringNumberFormat(), R.string.unit_stock.toResString())
                tvPriceContents.toTextfromHtml(R.string.bold_regular_contents.toResString(), (it.DEAL_UPRC ?: 0).toStringNumberFormat(), R.string.unit_won.toResString())
                tvRemainContents.toTextfromHtml(R.string.bold_regular_contents.toResString(), (it.RMQTY ?: 0).toStringNumberFormat(), R.string.unit_stock.toResString())
                tvDirectPriceContents.toTextfromHtml(R.string.bold_regular_contents.toResString(), (it.LST_PRC ?: 0).toStringNumberFormat(), R.string.unit_won.toResString())
            }else{
                ivSecret.toVisible()
                tvCountContents.text = R.string.star6.toResString()
                tvTotalCountContents.text = R.string.star6.toResString()
                tvPriceContents.text = R.string.star6.toResString()
                tvRemainContents.text = R.string.star6.toResString()
                tvDirectPriceContents.text = R.string.star6.toResString()
            }

            //협상대기
            customOrderStatusView.setOrderStatus(it.orderStatus())

            tvUserName.text = it.CLNT_HANGL_NM
            tvRegistDateContents.text = it.POST_TIME_FORM
            if(it.DEAL_CNDI.isNullOrEmpty()){
                layoutBottomContents.toGone()
            }else{
                layoutBottomContents.toVisible()
                tvTradeConditionContents.text = it.DEAL_CNDI
            }
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


    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
