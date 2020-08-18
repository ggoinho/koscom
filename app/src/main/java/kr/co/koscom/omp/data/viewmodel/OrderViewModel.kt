/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.koscom.omp.data.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import kr.co.koscom.omp.data.ServiceFactory
import kr.co.koscom.omp.data.model.*


class OrderViewModel() : ViewModel() {

    val orderService = ServiceFactory.getOrderService()

    fun searchStock(clientNo: String, viewType: String, idx: String? = null, word: String? = null): Flowable<Stock> {

        var request = Request()
        request.clientNo = clientNo
        request.view_type = viewType
        request.idx = idx
        request.word = word

        return orderService.searchStock(request)
    }

    fun tradeList(start: Int?, end: Int?, keyword: String?, dealTpCode : String?): Flowable<Order> {

        var request = Request()
        request.start = start
        request.end = end
        request.keyword = keyword
        request.dealTpCode = dealTpCode

        return orderService.tradeList(request)
    }

    fun tradeList(start: Int?, end: Int?, keyword: String?, dealTpCode: String = "", isNegotiable : String = "", isMyOrder: String = ""): Flowable<Order> {

        var request = Request()
        request.start = start
        request.end = end
        request.keyword = keyword
        request.isNegotiable = isNegotiable
        request.isMyOrder = isMyOrder
        request.dealTpCode = dealTpCode

        return orderService.tradeList(request)
    }


    fun orderDetail(orderNo: String, rqtClientNo: String, orderStkCode: String): Flowable<OrderDetail> {

        var request = Request()
        request.orderNo = orderNo
        request.rqtClientNo = rqtClientNo
        request.orderStkCode = orderStkCode

        return orderService.orderDetail(request)
    }

    fun contractList(start: Int?, end: Int?, keyword: String = "", dealTpCode: String = "", contractStatusParam: String = ""): Flowable<OrderContract> {

        var request = Request()
        request.start = start
        request.end = end
        request.keyword = keyword
        request.dealTpCode = dealTpCode
        //todo 9월에 작업
//        request.negoSettStat = contractStatusParam

        return orderService.contractList(request)
    }

    fun contractDetail(orderNo: String, channelUrl: String? = "1"): Flowable<OrderContractDetail> {

        var request = Request()
        request.orderNo = orderNo
        request.channelUrl = channelUrl

        return orderService.contractDetail(request)
    }

    fun submitOrder(unitPrice: String, orderType: String, condition: String,
                    clientName: String, clientNo: String, orderQty: String,
                    stockCode: String, startDate: String, publicYn: String, certiNum: String): Flowable<Response> {

        var request = Request()
        request.unitPrice = unitPrice
        request.orderType = orderType
        request.condition = condition
        request.clientName = clientName
        request.clientNo = clientNo
        request.orderQty = orderQty
        request.stockCode = stockCode
        request.startDate = startDate
        request.publicYn = publicYn
        request.certiNum = certiNum

        return orderService.submitOrder(request)
    }

    fun favoriteCorp(clientNo: String, entpNo: String, type: String): Flowable<Response> {

        var request = Request()
        request.clientNo = clientNo
        request.entpNo = entpNo
        request.type = type

        return orderService.favoriteCorp(request)
    }

    fun getCertiNum(clientNo: String, mobileToken: String?): Flowable<Certi> {

        var request = Request()
        request.clientNo = clientNo
        request.MOBILE_TOCKEN = mobileToken

        return orderService.getCertiNum(request)
    }

    fun myNegoList(clientNo: String, start: Int?, end: Int?, stk_nm: String?, searchRequstDeBgn: String?, searchRequstDeEnd: String?): Flowable<OrderNego> {

        var request = Request()
        request.clientNo = clientNo
        request.start = start
        request.end = end
        request.stk_nm = stk_nm
        request.searchRequstDeBgn = searchRequstDeBgn
        request.searchRequstDeEnd = searchRequstDeEnd

        return orderService.myNegoList(request)
    }

}
