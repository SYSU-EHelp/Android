package com.example.limin.ehelp.networkservice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.gson.Gson;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        final Button register = (Button) findViewById(R.id.register);
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

                        ToastUtils.show(APITestActivity.this, cookit);
                        CurrentUser.cookie = cookit;
                        CurrentUser.id = response.body().data.id;

                        SharedPreferences.Editor editor = getSharedPreferences("login_info", MODE_PRIVATE).edit();
                        editor.putInt("id", response.body().data.id);
                        editor.putString("cookit", CurrentUser.cookie);
                        editor.commit();
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

        //  退出登录
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<EmptyResult> call = apiService.requestLogout();
                call.enqueue(new Callback<EmptyResult>() {
                    @Override
                    public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {
                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }

                        SharedPreferences.Editor editor = getSharedPreferences("login_info", MODE_PRIVATE).edit();
                        editor.putInt("id", -1);
                        editor.putString("cookit", "");
                        editor.commit();
                        CurrentUser.id = -1;
                        CurrentUser.cookie = "";

                        Toast.makeText(APITestActivity.this, "已经退出登录", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<EmptyResult> call, Throwable t) {

                    }
                });
            }
        });

        //  获取紧急联系人
        Button contacts = (Button) findViewById(R.id.contacts);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ToastUtils.show(APITestActivity.this, CurrentUser.cookie);

                Call<ContactsResult> call = apiService.requestContacts();
                call.enqueue(new Callback<ContactsResult>() {
                    @Override
                    public void onResponse(Call<ContactsResult> call, Response<ContactsResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(Call<ContactsResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  添加新的联系人

        Button addContact = (Button) findViewById(R.id.addContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<EmptyResult> call = apiService.requestAddContact("Gordan", "18819253762");
                call.enqueue(new Callback<EmptyResult>() {
                    @Override
                    public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }
                    @Override
                    public void onFailure(Call<EmptyResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  获取问题

        Button questions = (Button) findViewById(R.id.questions);
        questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<QuestionsResult> call = apiService.requestQuestions();
                call.enqueue(new Callback<QuestionsResult>() {
                    @Override
                    public void onResponse(Call<QuestionsResult> call, Response<QuestionsResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }
                    @Override
                    public void onFailure(Call<QuestionsResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        // 获取问题详情
        Button questionDetail = (Button) findViewById(R.id.questionDetail);
        questionDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<QuestionDetailResult> call = apiService.requestQuestionDetail(1);
                call.enqueue(new Callback<QuestionDetailResult>() {
                    @Override
                    public void onResponse(Call<QuestionDetailResult> call, Response<QuestionDetailResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }
                    @Override
                    public void onFailure(Call<QuestionDetailResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        // 添加问题

        Button addQuestion = (Button) findViewById(R.id.addQuestion);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<EmptyResult> call = apiService.requestAddQuestion("title", "description");
                call.enqueue(new Callback<EmptyResult>() {
                    @Override
                    public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }
                    @Override
                    public void onFailure(Call<EmptyResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  回答问题
        Button answerQuestion = (Button) findViewById(R.id.answerQuestion);
        answerQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<EmptyResult> call = apiService.requestAnswerQuestion(2,"answer question content");
                call.enqueue(new Callback<EmptyResult>() {
                    @Override
                    public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }
                    @Override
                    public void onFailure(Call<EmptyResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  获取求救列表

        Button helps = (Button) findViewById(R.id.helps);
        helps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<HelpsResult> call = apiService.requestHelps();
                call.enqueue(new Callback<HelpsResult>() {
                    @Override
                    public void onResponse(Call<HelpsResult> call, Response<HelpsResult> response) {

                        if (!response.isSuccessful()) {
                            ToastUtils.show(APITestActivity.this, ToastUtils.SERVER_ERROR);
                            return;
                        }
                        if (response.body().status != 200) {
                            ToastUtils.show(APITestActivity.this, response.body().errmsg);
                            return;
                        }
                        ToastUtils.show(APITestActivity.this, new Gson().toJson(response.body()));
                    }
                    @Override
                    public void onFailure(Call<HelpsResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

    }
}
