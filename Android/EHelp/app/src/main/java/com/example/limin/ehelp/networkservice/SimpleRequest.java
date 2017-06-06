package com.example.limin.ehelp.networkservice;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/06/06.
 */


// 单例模式
public class SimpleRequest {
    private static SimpleRequest instance = null;
    public static SimpleRequest getInstance() {
        if (instance == null) {
            instance = new SimpleRequest();
        }
        return instance;
    }
    private SimpleRequest() {};
    private ApiService apiService = ApiService.retrofit.create(ApiService.class);

    // example login
    // we recommend the ApiServiceRequestResultHandler only handle Ui case
    public void login(final Context context, String username, String password, final ApiServiceRequestResultHandler apiServiceRequestResultHandler) {
        Call<LoginResult> call = apiService.requestLogin(username, password);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (!response.isSuccessful()) {
                    apiServiceRequestResultHandler.onError(ToastUtils.SERVER_ERROR);;
                    return;
                }
                if (response.body().status != 200) {
                    apiServiceRequestResultHandler.onError(response.body().errmsg);
                    return;
                }
                // save cookir  and id
                String cookit = "";
                String headersString = response.headers().toString();
                String pattern = "(JSESSIONID[^;]*;)[\\s\\S]*(user[^;]*;)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(headersString);
                if (m.find()) {
                    if (m.group(1) != null) {
                        cookit += m.group(1);
                    }
                    if (m.group(2) != null) {
                        cookit += m.group(2);
                    }
                }

                CurrentUser.cookie = cookit;
                CurrentUser.id = response.body().data.id;

                SharedPreferences.Editor editor = context.getSharedPreferences("login_info", context.MODE_PRIVATE).edit();
                editor.putInt("id", response.body().data.id);
                editor.putString("cookit", CurrentUser.cookie);
                editor.commit();
                apiServiceRequestResultHandler.onSuccess(response.body().data);
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                apiServiceRequestResultHandler.onError(ToastUtils.NETWORK_ERROR);
            }
        });
    }
}
