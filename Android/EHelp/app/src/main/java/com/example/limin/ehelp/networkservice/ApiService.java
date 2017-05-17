package com.example.limin.ehelp.networkservice;

import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.Global;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/05/17.
 */

public interface ApiService {

    @POST("/api/users/sendCode")
    @FormUrlEncoded
    Call<SendCodeResult> requestSendCode(@Field("phone") String phone);

    @POST("/api/users/register")
    @FormUrlEncoded
    Call<RegisterResult> requestRegister(@Field("code") String code,
                                         @Field("phone") String phone,
                                         @Field("username") String username,
                                         @Field("password") String password);

    @POST("/api/users/login")
    @FormUrlEncoded
    Call<LoginResult> requestLogin(@Field("username") String username,
                                   @Field("password") String password);


    static final Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            Request newRequest = oldRequest
                    .newBuilder()
                    .header("Cookie", CurrentUser.cookie)
                    .build();
            return chain.proceed(newRequest);
        }
    };
    static final OkHttpClient client = new OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build();

    static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Global.getInstance().baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();
}
