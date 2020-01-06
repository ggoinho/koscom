package kr.co.koscom.omp.data.service;

import io.reactivex.Flowable;
import kr.co.koscom.omp.data.model.Channel;
import kr.co.koscom.omp.data.model.Contract;
import kr.co.koscom.omp.data.model.Escrow;
import kr.co.koscom.omp.data.model.Main;
import kr.co.koscom.omp.data.model.Order;
import kr.co.koscom.omp.data.model.OrderContract;
import kr.co.koscom.omp.data.model.OrderContractDetail;
import kr.co.koscom.omp.data.model.OrderDetail;
import kr.co.koscom.omp.data.model.Request;
import kr.co.koscom.omp.data.model.Response;
import kr.co.koscom.omp.data.model.Stock;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OrderService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/getStockListBySearch")
    Flowable<Stock> searchStock(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/tradeList")
    Flowable<Order> tradeList(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/orderDetails")
    Flowable<OrderDetail> orderDetail(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/contExecList")
    Flowable<OrderContract> contractList(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/contExecDetail")
    Flowable<OrderContractDetail> contractDetail(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/orderSubmit")
    Flowable<Response> submitOrder(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/favoriteCorpReg")
    Flowable<Response> favoriteCorp(@Body Request request);

}
