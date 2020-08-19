import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sendbird.syncmanager.utils.PreferenceUtils
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.R
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.data.model.OrderDetail
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.enums.OrderStatusType
import kr.co.koscom.omp.enums.SecKindDtlType
import kr.co.koscom.omp.extension.toResColor
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.util.KosSharedPreferences
import java.util.ArrayList

/**
 * 매수 or 매도 타입별 이름
 */
fun String?.toDealType(): String {
    this?.let {
        return when (it) {
            DealType.SELL.type -> {
                //매도
                R.string.sell.toResString()
            }
            DealType.BUYING.type -> {
                //매수
                R.string.buying.toResString()
            }
            else -> {
                ""
            }
        }
    }?: run {
        return ""
    }
}

/**
 * 매수 or 매도 타입별 색칠된 사각 background
 */
fun String?.toDealTypeBackground(view: View){
    this?.let {
        if(it == DealType.SELL.type){
            //매도
            view.setBackgroundColor(R.color.sell_background.toResColor())
        }else{
            //매수
            view.setBackgroundColor(R.color.buy_background.toResColor())
        }
    }
}


/**
 * 매수 or 매도 타입별 텍스트 색상
 */
fun TextView?.toDealType(dealType: String) {
    this?.let {
        return if (dealType == DealType.SELL.type) {
            //매도
            it.setTextColor(R.color.blue_3348ae.toResColor())
        } else {
            //매수
            it.setTextColor(R.color.red_e8055a.toResColor())
        }
    }
}



/**
 * PBLS_CLNT_NO 내 아이디와 비교하여 자기 자신인지 체크
 */
fun String?.isMe(): Boolean{
    return this?.let {
        it == PreferenceUtils.getUserId()
    }?: run {
        false
    }
}

/**
 * 주문 협상 상태 가져오기
 */
fun Order.OrderItem?.orderStatus(): OrderStatusType{

    return this?.let {
        when{
            it.POST_ORD_STAT_CODE == "0" && DealType.getType(it.DEAL_TP) == DealType.SELL ->{
                OrderStatusType.WAIT_SELL_NEGO
            }
            it.POST_ORD_STAT_CODE == "0" && DealType.getType(it.DEAL_TP) == DealType.BUYING ->{
                OrderStatusType.WAIT_BUY_NEGO
            }
            it.POST_ORD_STAT_CODE == "1" -> {
                if(it.RMQTY ?: 0 > 0){
                    OrderStatusType.NEGOTIATING
                }
                else{
                    OrderStatusType.NEGOTIATING_NO_REMAIN
                }
            }
            else ->{
                OrderStatusType.NONE
            }
        }
    }?: run {
        OrderStatusType.NONE
    }
}

/**
 * 주문 협상 상태 가져오기
 */
fun OrderDetail.OrderData?.orderStatus(): OrderStatusType{

    return this?.let {
        when{
            it.POST_ORD_STAT_CODE == "0" && DealType.getType(it.DEAL_TP) == DealType.SELL ->{
                OrderStatusType.WAIT_SELL_NEGO
            }
            it.POST_ORD_STAT_CODE == "0" && DealType.getType(it.DEAL_TP) == DealType.BUYING ->{
                OrderStatusType.WAIT_BUY_NEGO
            }
            it.POST_ORD_STAT_CODE == "1" -> {
                if(it.RMQTY ?: 0 > 0){
                    OrderStatusType.NEGOTIATING
                }
                else{
                    OrderStatusType.NEGOTIATING_NO_REMAIN
                }
            }
            else ->{
                OrderStatusType.NONE
            }
        }
    }?: run {
        OrderStatusType.NONE
    }
}


fun String?.toStockTypeCodeName(secKindCode: String): String{
    return this?.let {
        this + when {
            (SecKindDtlType.getType(secKindCode) == SecKindDtlType.REPAYMENT) -> {R.string.unit_repayment.toResString()}
            (SecKindDtlType.getType(secKindCode) == SecKindDtlType.CONVERSION_PAYMENT) -> {R.string.unit_conversion_payment.toResString()}
            else -> {""}
        }
    }?: run{
        ""
    }
}

/**
 * 최근 본 메뉴 추가
 */
fun DrawerMenuData.addRecentlyMenu(){
    val strRecentlyMenu = Preference.getRecentlyDrawerMenu(BaseApplication.getAppContext()) ?: ""
    var listStr = ""
    listStr = if(strRecentlyMenu.isNullOrEmpty()){
        var listRecently = arrayListOf<DrawerMenuData>()
        listRecently.add(this)
        Gson().toJson(listRecently)
    }else{
        val listRecently = Gson().fromJson(strRecentlyMenu, object : TypeToken<ArrayList<DrawerMenuData>>() {}.type) as ArrayList<DrawerMenuData>
        var samePosition = -1
        for((index, recentlyItem) in listRecently.withIndex()){
            if(recentlyItem == this){
                samePosition = index
            }
        }
        if(-1 < samePosition){
            listRecently.removeAt(samePosition)
        }

        if(listRecently.size == 5){
            listRecently.removeAt(listRecently.size-1)
        }

        listRecently.add(0, this)
        Gson().toJson(listRecently)
    }
    Preference.setRecentlyDrawerMenu(BaseApplication.getAppContext(), listStr)
}

/**
 * 최근 본 메뉴 제거
 */
fun DrawerMenuData.removeRecently(){
    val strRecentlyMenu = Preference.getRecentlyDrawerMenu(BaseApplication.getAppContext()) ?: ""
    var listStr = ""
    if(!strRecentlyMenu.isNullOrEmpty()){
        val listRecently = Gson().fromJson(strRecentlyMenu, object : TypeToken<ArrayList<DrawerMenuData>>() {}.type) as ArrayList<DrawerMenuData>
        var samePosition = -1
        for((index, recentlyItem) in listRecently.withIndex()){
            if(recentlyItem == this){
                samePosition = index
            }
        }
        if(-1 < samePosition){
            listRecently.removeAt(samePosition)
        }
        listStr = Gson().toJson(listRecently)
    }
    Preference.setRecentlyDrawerMenu(BaseApplication.getAppContext(), listStr)
}





