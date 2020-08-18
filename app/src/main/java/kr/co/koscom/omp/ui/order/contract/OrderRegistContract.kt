package kr.co.koscom.omp.ui.order.contract

import kr.co.koscom.omp.base.BasePresenter
import kr.co.koscom.omp.base.BaseView
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.enums.MainOrderTab

interface OrderRegistContract {
    interface View : BaseView<Presenter>{

        fun refresh()
        fun showProgress(isShow: Boolean)
        fun alertDialog(msg: String)
        fun showErrorMsg(code: String? = "", msg: String? = "")
        fun setCertiNum(certi: Certi)
        fun publicOrderClick()
        fun submitComplete(msg: String)
    }

    interface Presenter: BasePresenter{
        fun initViewModel(viewModelFactory: ViewModelFactory)

        /**
         * 인증번호 받아오는 API
         */
        fun getCertiNum()

        /**
         * 매수/매도 등록
         */
        fun submitOrder(count: String, price: String, message: String, isPublic: Boolean, password: String)

    }
}