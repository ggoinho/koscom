package kr.co.koscom.omp.ui.popup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.ui.popup.contract.OrderPopupNewContract
import kr.co.koscom.omp.util.LogUtil

class OrderPopupNewPresenter(private val view: OrderPopupNewContract.View?): OrderPopupNewContract.Presenter {
    internal val tag = this.javaClass.simpleName


    private val disposable = CompositeDisposable()

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var orderViewModel: OrderViewModel

    var orderDetail: OrderDetail.OrderData? = null

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
     * 주문현황 리스트
     */
    override fun getOrderData(data: Order.OrderItem){

        view?.showProgress(true)
        disposable.add(orderViewModel.orderDetail(data.ORDER_NO?: "", data.PBLS_CLNT_NO?: "", data.STK_CODE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                LogUtil.d(tag, "response : $it")

                if("0000" == it.rCode){
                    orderDetail = it.datas?.result
                    view?.setOrderData(orderDetail)
                }else{
                    view?.showErrorMsg(it.rCode, it.rMsg)
                }
                view?.showProgress(false)

            }, {
                view?.showProgress(false)
                it.printStackTrace()
                view?.alertDialog(R.string.network_error.toResString())
            }))
    }



    /**
     * 협상 요청
     */
    override fun requestNego(orderNo: String){
        view?.showProgress(true)
        disposable.add(chatViewModel.requestNego(PreferenceUtils.getUserId(), orderNo, "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.alertDialog(it.rMsg ?: "")
                }else{
                    view?.showErrorMsg(it.rCode, it.rMsg)
                }
                view?.showProgress(false)
            }, {
                view?.showProgress(false)
                it.printStackTrace()
                view?.alertDialog(R.string.network_error.toResString())
            }))
    }

}