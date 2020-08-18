package kr.co.koscom.omp.ui.order

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.*
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainListData
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toYN
import kr.co.koscom.omp.ui.order.contract.OrderListNewContract
import kr.co.koscom.omp.util.LogUtil

class OrderListNewPresenter(private val view: OrderListNewContract.View?): OrderListNewContract.Presenter {
    internal val tag = this.javaClass.simpleName


    private val disposable = CompositeDisposable()

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var orderViewModel: OrderViewModel


    var mainTotalData: MainTotalData = MainTotalData()
    val PAGE_SIZE = 20
    var isLoading = false

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
    override fun getTradeList(listData: MutableList<Order.OrderItem>, dealTpCode: String, searchContents: String, isNegotiable: Boolean, isMyOrder: Boolean){
        if(isLoading) return
        isLoading = true

        view?.showProgress(true)
        disposable.add(orderViewModel.tradeList(listData.size, PAGE_SIZE, searchContents, dealTpCode, isNegotiable.toYN(), isMyOrder.toYN())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                LogUtil.d(tag, "response : $it")

                if("0000" == it.rCode){
                    view?.setTradeList(it)
                }else{
                    view?.showErrorMsg(it.rCode, it.rMsg)
                }
                isLoading = false
                view?.showProgress(false)

            }, {
                isLoading = false
                view?.showProgress(false)
                it.printStackTrace()
                view?.alertDialog(R.string.network_error.toResString())
            }))
    }



    /**
     * 체결현황 리스트
     */
    override fun getContractList(listData: MutableList<OrderContract.OrderContractItem>, dealTpCode: String, searchContents: String){
        if(isLoading) return
        isLoading = true

        view?.showProgress(true)
        disposable.add(orderViewModel.contractList(listData.size, PAGE_SIZE, searchContents, dealTpCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.setContractList(it)
                }else{
                    view?.showErrorMsg(it.rCode, it.rMsg)
                }
                isLoading = false
                view?.showProgress(false)

            }, {
                isLoading = false
                view?.showProgress(false)
                it.printStackTrace()
                view?.alertDialog(R.string.network_error.toResString())
            }))
    }


    /**
     * 협상 거절
     */
    override fun denyNego(data: Main.Nego) {
        view?.showProgress(true)
        disposable.add(chatViewModel.denyNego(PreferenceUtils.getUserId(), data.ORDER_NO?: "", data.CHANNEL_URL?: "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.alertDialog(it.rMsg ?: "")
                    view?.refresh()
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

    /**
     * 협상 수락
     */
    override fun acceptNego(data: Main.Nego) {
        view?.showProgress(true)
        disposable.add(chatViewModel.acceptNego(PreferenceUtils.getUserId(), data.ORDER_NO?: "", data.CHANNEL_URL?: "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.alertDialog(it.rMsg ?: "")
                    view?.refresh()
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

    /**
     * 요청 취소
     */
    override fun yocheongCancel(data: OrderNego.OrderNegoItem) {
        view?.showProgress(true)
        disposable.add(chatViewModel.yocheongCancel(PreferenceUtils.getUserId(), data.ORDER_NO, data.CHANNEL_URL)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.alertDialog(it.rMsg ?: "")
                    view?.refresh()
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