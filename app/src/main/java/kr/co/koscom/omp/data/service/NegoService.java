package kr.co.koscom.omp.data.service;

import io.reactivex.Flowable;
import kr.co.koscom.omp.data.model.Main;
import kr.co.koscom.omp.data.model.Nego;
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

public interface NegoService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/getNegoMngList")
    Flowable<Main> searchMain(@Body Request request);

}
