package kr.co.koscom.omp.data.service;

import io.reactivex.Flowable;
import kr.co.koscom.omp.BuildConfig;
import kr.co.koscom.omp.data.model.Channel;
import kr.co.koscom.omp.data.model.Contract;
import kr.co.koscom.omp.data.model.Escrow;
import kr.co.koscom.omp.data.model.Request;
import kr.co.koscom.omp.data.model.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    //@POST("/DID/api/getSkdidQRImage")
    @POST("/api/getDIDInfo")
    Flowable<Response> getSkDidInfo(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    //@POST("/DID/api/getSkdidQRImage")
    @POST("/DID/api/getDIDInitInfo")
    Flowable<Response> getSkdid(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/getOpenPassCertResult1")
    Flowable<Response> resultCertMyPass(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST(BuildConfig.LOGIN_URL)
    Flowable<Response> login(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST(BuildConfig.LOGOUT_URL)
    Flowable<Response> logout(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/naverCloudPlatform/getUserPUSH")
    Flowable<Response> getUserPush(@Body Request request);

}
