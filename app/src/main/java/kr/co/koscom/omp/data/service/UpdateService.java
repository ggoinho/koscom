package kr.co.koscom.omp.data.service;

import io.reactivex.Flowable;
import kr.co.koscom.omp.data.model.Request;
import kr.co.koscom.omp.data.model.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UpdateService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/appUpdate")
    Flowable<Response> updateVersion(@Body Request request);

}
