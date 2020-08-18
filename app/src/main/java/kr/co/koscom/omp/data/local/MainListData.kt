package kr.co.koscom.omp.data.local

import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderContract
import kr.co.koscom.omp.data.model.OrderNego
import kr.co.koscom.omp.enums.MainOrderTab
import java.io.Serializable

class MainListData: Serializable {


    var tabType: MainOrderTab = MainOrderTab.ORDER_STATUS

    var orderItem: Order.OrderItem? = null
    var orderContractItem: OrderContract.OrderContractItem? = null
    var orderNego: OrderNego.OrderNegoItem? = null


}