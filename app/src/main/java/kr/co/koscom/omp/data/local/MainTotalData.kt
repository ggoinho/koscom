package kr.co.koscom.omp.data.local

import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.enums.MainOrderTab
import kr.co.koscom.omp.enums.NegoTiableMyTab

class MainTotalData {

    var tabType: MainOrderTab = MainOrderTab.ORDER_STATUS
    var typeNegotiableMy: NegoTiableMyTab = NegoTiableMyTab.NONE
    var totalCount: Int = 0
    var main: Main? = null

    var listMain: MutableList<MainListData> = arrayListOf()

}