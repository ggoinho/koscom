package kr.co.koscom.omp.ui.order.contract

import kr.co.koscom.omp.base.BasePresenter
import kr.co.koscom.omp.base.BaseView
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.enums.MainOrderTab

interface OrderListNewContract {
    interface View : BaseView<Presenter>{

        fun refresh()
        fun showProgress(isShow: Boolean)
        fun alertDialog(msg: String)
        fun showErrorMsg(code: String? = "", msg: String? = "")

        fun setTradeList(order: Order)
        fun setContractList(order: OrderContract)

    }

    interface Presenter: BasePresenter{
        fun initViewModel(viewModelFactory: ViewModelFactory)

        fun getTradeList(listData: MutableList<Order.OrderItem>, dealTpCode: String = "", searchContents: String = "", isNegotiable: Boolean = false, isMyOrder: Boolean = false)
        fun getContractList(listData: MutableList<OrderContract.OrderContractItem>, dealTpCode: String = "", searchContents: String = "")

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