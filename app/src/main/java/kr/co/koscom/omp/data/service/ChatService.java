package kr.co.koscom.omp.data.service;

import java.util.HashMap;

import io.reactivex.Flowable;
import kr.co.koscom.omp.data.model.Channel;
import kr.co.koscom.omp.data.model.Contract;
import kr.co.koscom.omp.data.model.Escrow;
import kr.co.koscom.omp.data.model.ReqBodySet;
import kr.co.koscom.omp.data.model.Response;
import kr.co.koscom.omp.data.model.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatService {

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/naverCloudPlatform/regUserPUSH")
    Flowable<Response> registPushToken(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/naverCloudPlatform/sendPush")
    Flowable<Response> sendPush(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/properties")
    Flowable<Response> login(@Body Request request);
    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/channelsAccept")
    Flowable<Response> chatAccept(@Body Request request);
    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/channelsDecline")
    Flowable<Response> chatDeny(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalChannel")
    Flowable<Channel> openChannel(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalContract")
    Flowable<Contract> getContract(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalEscro")
    Flowable<Escrow> getEscro(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/negoInfoSave")
    Flowable<Response> saveNegotiation(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalContractOk")
    Flowable<Response> acceptNegotiation(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalContractCancel")
    Flowable<Response> denyNegotiation(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalJugwonhwakMaesuReq")
    Flowable<Response> requestPaperOfBuyer(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalJugwonhwakMaedoReq")
    Flowable<Response> requestPaperOfSeller(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/openChatModalESignComplete")
    Flowable<Response> signContract(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/invst/chatModal/channelsLeave")
    Flowable<Response> exitChannel(@Body Request request);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json",
            "Charset: UTF-8"
    })
    @POST("/api/login")
    Flowable<Response> logintest(@Body Request request);

}
