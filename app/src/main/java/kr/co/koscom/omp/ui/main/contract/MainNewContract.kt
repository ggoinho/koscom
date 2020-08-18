package kr.co.koscom.omp.ui.main.contract

import kr.co.koscom.omp.base.BasePresenter
import kr.co.koscom.omp.base.BaseView
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.enums.MainOrderTab

interface MainNewContract {
    interface View : BaseView<Presenter>{

        fun refresh()
        fun showProgress(isShow: Boolean)
        fun alertDialog(msg: String)
        fun showErrorMsg(code: String? = "", msg: String? = "")

        fun setMainData(main: MainTotalData)
        fun setTradeList(order: Order)
        fun setContractList(order: OrderContract)
        fun setNegoList(order: OrderNego)
        fun setAlarmCount(alarm: Alarm)

    }

    interface Presenter: BasePresenter{
        fun initViewModel(viewModelFactory: ViewModelFactory)

        fun getMainData()
        fun searchAlarm()
        fun logOut()

        /**
         * 협상 거절
         */
        fun denyNego(data: Main.Nego)
        /**
         * 협상 수락
         */
        fun acceptNego(data: Main.Nego)
        /**
         * 요청 취소
         */
        fun yocheongCancel(data: OrderNego.OrderNegoItem)



    }
}