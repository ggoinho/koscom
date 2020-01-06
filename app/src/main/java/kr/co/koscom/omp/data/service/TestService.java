package kr.co.koscom.omp.data.service;

import kr.co.koscom.omp.data.model.Response;
import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.*;

public interface TestService {

    @POST("/posts")
    @FormUrlEncoded
    Flowable<Response> savePost(@Field("title") String title,
                                @Field("body") String body,
                                @Field("userId") long userId);

    // RxJava
   /* @POST("/posts")
    @FormUrlEncoded
    Observable<Response> savePost(@Field("title") String title,
                              @Field("body") String body,
                              @Field("userId") long userId);*/

    @POST("/posts")
    Call<Response> savePost(@Body Response response);

    @PUT("/posts/{id}")
    @FormUrlEncoded
    Call<Response> updatePost(@Path("id") long id,
                              @Field("title") String title,
                              @Field("body") String body,
                              @Field("userId") long userId);

    @DELETE("/posts/{id}")
    Call<Response> deletePost(@Path("id") long id);

}
