package kr.co.koscom.omp.ui.order

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.*
import com.sendbird.syncmanager.utils.DateUtils
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_write.*
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainListData
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toYN
import kr.co.koscom.omp.ui.order.contract.OrderListNewContract
import kr.co.koscom.omp.ui.order.contract.OrderRegistContract
import kr.co.koscom.omp.util.LogUtil
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.StringUtils

class OrderRegistPresenter(private val view: OrderRegistContract.View?): OrderRegistContract.Presenter {
    internal val tag = this.javaClass.simpleName


    private val disposable = CompositeDisposable()

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var orderViewModel: OrderViewModel

    var stock: Stock.ResultMap? = null
    var dealType: DealType = DealType.ALL

    private val activity: AppCompatActivity
        get() = (view as AppCompatActivity)

    private val context: Context
        get() = (view as Context)


    override fun initViewModel(viewModelFactory: ViewModelFactory) {
        chatViewModel = ViewModelProviders.of(activity, viewModelFactory).get(ChatViewModel::class.java)
        orderViewModel = ViewModelProviders.of(activity, viewModelFactory).get(OrderViewModel::class.java)
    }

    override fun detachView() {
        disposable.clear()
    }


    /**
     * 인증번호 받아오는 API
     */
    override fun getCertiNum() {

        view?.showProgress(true)
        disposable.add(orderViewModel.getCertiNum(PreferenceUtils.getUserId(), Preference.getServerToken(BaseApplication.getAppContext()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.setCertiNum(it)
                }else{
                    view?.showErrorMsg(it.rCode, it.rMsg)
                    view?.publicOrderClick()
                }
                view?.showProgress(false)
            },{
                view?.showProgress(false)
                it.printStackTrace()
                view?.alertDialog(R.string.network_error.toResString())
                view?.publicOrderClick()
            }))
    }


    /**
     * 매수/매도 등록 API
     */
    override fun submitOrder(count: String, price: String, message: String, isPublic: Boolean, password: String){

        if(dealType == DealType.ALL) return


        stock?.let {stockData ->
            if (!stockData.TRSF_ABLE_QTY.isNullOrEmpty() && StringUtils.remove(count,",").toInt() > (stockData.TRSF_ABLE_QTY?:"0").toInt()) {
                view?.alertDialog(R.string.orderregist_notify_exceed_number.toResString())
            } else {
                view?.showProgress(true)
                disposable.add(orderViewModel.submitOrder(
                    StringUtils.remove(price, ","),
                    dealType.type,
                    message,
                    PreferenceUtils.getUserName(),
                    PreferenceUtils.getUserId(),
                    StringUtils.remove(count, ","),
                    stockData.STK_CODE ?: "",
                    DateUtils.format3Date(System.currentTimeMillis()),
                    if(isPublic) "Y" else "N",
                    password
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LogUtil.d(tag, "response : $it")
                        if("0000" == it.rCode){
                            view?.submitComplete(it.rMsg ?: "")
                        }else{
                            view?.showErrorMsg(it.rCode, it.rMsg)
                        }
                        view?.showProgress(false)
                    },{
                        view?.showProgress(false)
                        it.printStackTrace()
                        view?.alertDialog(R.string.network_error.toResString())
                    }))
            }
        }
    }


}