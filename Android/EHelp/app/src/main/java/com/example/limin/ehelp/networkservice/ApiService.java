package com.example.limin.ehelp.networkservice;

import com.example.limin.ehelp.bean.QuestionDetailBean;
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
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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

    @POST("/api/users/autologin")
    Call<LoginResult> requestAutoLogin();

    @POST("/api/users/logout")
    Call<EmptyResult> requestLogout();

    @GET("/api/users/contacts")
    Call<ContactsResult> requestContacts();

    @POST("/api/users/contacts")
    @FormUrlEncoded
    Call<EmptyResult> requestAddContact(@Field("username") String username,
                                        @Field("phone") String phone);

    @GET("/api/questions")
    Call<QuestionsResult> requestQuestions();

    @GET("/api/questions/{question_id}")
    Call<QuestionDetailResult> requestQuestionDetail(@Path("question_id") int question_id);

    @POST("/api/questions")
    @FormUrlEncoded
    Call<EmptyResult> requestAddQuestion(@Field("title") String  title,
                                         @Field("description") String description);


    @PATCH("/api/questions/{question_id}")
    Call<EmptyResult> requestAnswerQuestion(@Path("question_id") int question_id,
                                            @Query("answer") String answer);

    @GET("/api/helps")
    Call<HelpsResult> requestHelps();

    @GET("/api/helps/{help_id}")
    Call<HelpDetailResult> requestHelpDetail(@Path("help_id") int help_id);

    @POST("/api/helps/{help_id}/responses")
    Call<EmptyResult> requestResponsesHelp(@Path("help_id") int help_id);

    @PATCH("/api/helps/{help_id}/finish")
    Call<EmptyResult> requestFinishHelp(@Path("help_id") int help_id);



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
