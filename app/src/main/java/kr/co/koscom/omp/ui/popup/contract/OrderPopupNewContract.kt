package kr.co.koscom.omp.ui.popup.contract

import kr.co.koscom.omp.base.BasePresenter
import kr.co.koscom.omp.base.BaseView
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.enums.MainOrderTab

interface OrderPopupNewContract {
    interface View : BaseView<Presenter>{

        fun showProgress(isShow: Boolean)
        fun alertDialog(msg: String)
        fun showErrorMsg(code: String? = "", msg: String? = "")
        fun setOrderData(order: OrderDetail.OrderData?)
    }

    interface Presenter: BasePresenter{
        fun initViewModel(viewModelFactory: ViewModelFactory)

        fun getOrderData(data: Order.OrderItem)

        /**
         * 협상 요청
         */
        fun requestNego(orderNo: String)
    }
}