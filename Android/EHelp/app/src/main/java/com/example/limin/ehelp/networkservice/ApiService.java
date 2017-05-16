package com.example.limin.ehelp.networkservice;

import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.Global;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/05/17.
 */

public interface ApiService {

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
