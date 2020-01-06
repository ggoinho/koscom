package kr.co.koscom.omp.data.service;

import io.reactivex.Flowable;
import kr.co.koscom.omp.data.model.Alarm;
import kr.co.koscom.omp.data.model.Order;
import kr.co.koscom.omp.data.model.OrderDetail;
import kr.co.koscom.omp.data.model.Request;
import kr.co.koscom.omp.data.model.Response;
import kr.co.koscom.omp.data.model.Stock;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AlarmService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/getUserAncNotReadCnt")
    Flowable<Alarm> searchNotReadCount(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/selectUserAncLst")
    Flowable<Alarm> searchList(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/updateUserAnc")
    Flowable<Response> updateRead(@Body Request request);

}
