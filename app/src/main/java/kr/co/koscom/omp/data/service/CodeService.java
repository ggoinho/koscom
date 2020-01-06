package kr.co.koscom.omp.data.service;

import io.reactivex.Flowable;
import kr.co.koscom.omp.data.model.Code;
import kr.co.koscom.omp.data.model.Order;
import kr.co.koscom.omp.data.model.OrderDetail;
import kr.co.koscom.omp.data.model.Request;
import kr.co.koscom.omp.data.model.Response;
import kr.co.koscom.omp.data.model.Stock;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CodeService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/getSysMetaMgmtList")
    Flowable<Code> searchCode(@Body Request request);

}
