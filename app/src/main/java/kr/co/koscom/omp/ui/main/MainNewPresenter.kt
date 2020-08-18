package kr.co.koscom.omp.ui.main

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
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import kr.co.koscom.omp.enums.ContractStatusParamType
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toYN
import kr.co.koscom.omp.ui.main.contract.MainNewContract
import kr.co.koscom.omp.util.LogUtil

class MainNewPresenter(private val view: MainNewContract.View?): MainNewContract.Presenter {
    internal val tag = this.javaClass.simpleName


    private val disposable = CompositeDisposable()

    private lateinit var testViewModel: TestViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var negoViewModel: NegoViewModel
    private lateinit var orderViewModel: OrderViewModel


    var mainTotalData: MainTotalData = MainTotalData()
    val PAGE_SIZE = 10
    var isLoading = false

    private val activity: AppCompatActivity
        get() = (view as AppCompatActivity)

    private val context: Context
        get() = (view as Context)

    override fun initViewModel(viewModelFactory: ViewModelFactory) {
        testViewModel = ViewModelProviders.of(activity, viewModelFactory).get(TestViewModel::class.java)
        chatViewModel = ViewModelProviders.of(activity, viewModelFactory).get(ChatViewModel::class.java)
        loginViewModel = ViewModelProviders.of(activity, viewModelFactory).get(LoginViewModel::class.java)
        alarmViewModel = ViewModelProviders.of(activity, viewModelFactory).get(AlarmViewModel::class.java)
        negoViewModel = ViewModelProviders.of(activity, viewModelFactory).get(NegoViewModel::class.java)
        orderViewModel = ViewModelProviders.of(activity, viewModelFactory).get(OrderViewModel::class.java)
    }

    override fun detachView() {
        disposable.clear()
    }

    override fun getMainData() {

        view?.showProgress(true)
        disposable.add(negoViewModel.searchMain(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                LogUtil.d(tag, "response : $it")

                if("0000" == it.rCode){
                    mainTotalData.main = it
                    view?.setMainData(mainTotalData)
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
     * 주문현황 리스트
     */
    fun getTradeList(listData: MutableList<MainListData>, isNegotiable: Boolean = false, isMyOrder: Boolean = false){
        if(isLoading) return
        isLoading = true

        view?.showProgress(true)
        disposable.add(orderViewModel.tradeList(listData.size, PAGE_SIZE, "", "", isNegotiable = isNegotiable.toYN(), isMyOrder = isMyOrder.toYN())
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
     * 나의협상 진행상황 리스트
     */
    fun getNegoList(listData: MutableList<MainListData>){
        view?.showProgress(true)
        disposable.add(orderViewModel.myNegoList(PreferenceUtils.getUserId(), listData.size, PAGE_SIZE, "", "", "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                LogUtil.d(tag, "response : $it")

                if("0000" == it.rCode){
                    view?.setNegoList(it)
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
     * 체결현황 리스트
     */
    fun getContractList(listData: MutableList<MainListData>){
        view?.showProgress(true)
        disposable.add(orderViewModel.contractList(listData.size, PAGE_SIZE, "", contractStatusParam = ContractStatusParamType.CONCLUSION_ONLY.type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                LogUtil.d(tag, "response : $it")
                if("0000" == it.rCode){
                    view?.setContractList(it)
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
     * 알람 개수 가져오기
     */
    override fun searchAlarm() {

        disposable.add(alarmViewModel.searchNotReadCount(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.d(tag, "response : $it")

                if("0000" == it.rCode){
                    view?.setAlarmCount(it)
                }
            },{
                it.printStackTrace()
            })
        )
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


    /**
     * 로그아웃
     */
    override fun logOut() {
        disposable.add(loginViewModel.logout(PreferenceUtils.getUserId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.d(tag, "response : $it")
            },{
                it.printStackTrace()
            })
        )
    }

}