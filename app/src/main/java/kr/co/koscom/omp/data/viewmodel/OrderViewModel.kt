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

package com.scsoft.boribori.data.viewmodel

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

    fun orderDetail(orderNo: String, rqtClientNo: String, orderStkCode: String): Flowable<OrderDetail> {

        var request = Request()
        request.orderNo = orderNo
        request.rqtClientNo = rqtClientNo
        request.orderStkCode = orderStkCode

        return orderService.orderDetail(request)
    }

    fun contractList(start: Int?, end: Int?, keyword: String?): Flowable<OrderContract> {

        var request = Request()
        request.start = start
        request.end = end
        request.keyword = keyword

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
                    stockCode: String, startDate: String): Flowable<Response> {

        var request = Request()
        request.unitPrice = unitPrice
        request.orderType = orderType
        request.condition = condition
        request.clientName = clientName
        request.clientNo = clientNo
        request.orderQty = orderQty
        request.stockCode = stockCode
        request.startDate = startDate

        return orderService.submitOrder(request)
    }

    fun favoriteCorp(clientNo: String, entpNo: String, type: String): Flowable<Response> {

        var request = Request()
        request.clientNo = clientNo
        request.entpNo = entpNo
        request.type = type

        return orderService.favoriteCorp(request)
    }

}
