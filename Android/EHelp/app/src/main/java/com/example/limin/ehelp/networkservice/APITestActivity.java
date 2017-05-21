package com.example.limin.ehelp.networkservice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.limin.ehelp.R;
import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APITestActivity extends AppCompatActivity {

    Button sendCode;
    private ApiService apiService;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apitest);
        apiService = ApiService.retrofit.create(ApiService.class);

        sendCode = (Button) findViewById(R.id.sendCode);

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<SendCodeResult> call = apiService.requestSendCode("18826234601");
                call.enqueue(new Callback<SendCodeResult>() {
                    @Override
                    public void onResponse(Call<SendCodeResult> call, Response<SendCodeResult> response) {
                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        code = response.body().data.code;
                        Toast.makeText(APITestActivity.this, response.body().data.code, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<SendCodeResult> call, Throwable t) {

                    }
                });

            }
        });

        // --- 注册
        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<RegisterResult> call = apiService.requestRegister(code,
                        "18826234601", "aaaaaa", "bbbbbb");
                call.enqueue(new Callback<RegisterResult>() {
                    @Override
                    public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        Toast.makeText(APITestActivity.this, ToastUtils.REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<RegisterResult> call, Throwable t) {

                    }
                });
            }
        });

        // 登录

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<LoginResult> call = apiService.requestLogin("aaaaaa", "bbbbbb");
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        // 维护cookir 和 记录id
                        CurrentUser.cookie = response.headers().get("Set-Cookie");
                        CurrentUser.userId = response.body().data.id;
                        SharedPreferences.Editor editor = getSharedPreferences("login_info", MODE_PRIVATE).edit();
                        editor.putInt("id", response.body().data.id);
                        editor.putString("cookit", CurrentUser.cookie);
                        editor.commit();
                        ToastUtils.show(APITestActivity.this, CurrentUser.cookie);

                        Toast.makeText(APITestActivity.this, ToastUtils.LOGIN_SUCCESS + response
                                .body().data.id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {

                    }
                });
            }
        });

        //  自动登录
        Button autoLogin = (Button) findViewById(R.id.autologin);
        autoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  先进行cookit 注入
                String cookit = getSharedPreferences("login_info", MODE_PRIVATE).getString("cookit", "");
                if (cookit == "") {
                    ToastUtils.show(APITestActivity.this, "你还未来曾经登录");
                    return;
                }
                CurrentUser.cookie = cookit;

                Call<LoginResult> call = apiService.requestAutoLogin();
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        Toast.makeText(APITestActivity.this, ToastUtils.LOGIN_SUCCESS + response
                                .body().data.id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {

                    }
                });
            }
        });


    }
}
