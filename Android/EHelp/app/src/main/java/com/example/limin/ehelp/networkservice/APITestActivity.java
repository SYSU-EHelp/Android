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
                        // 维护cookie 和 记录id
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

                Call<QuestionDetailResult> call = apiService.requestQuestionDetail(8);
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

                Call<EmptyResult> call = apiService.requestAnswerQuestion(8,"answer question content");
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

        //  获取求助列表
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

        //  获取求助详情
        Button helpDetail = (Button) findViewById(R.id.helpDetail);
        helpDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<HelpDetailResult> call = apiService.requestHelpDetail(58);
                call.enqueue(new Callback<HelpDetailResult>() {
                    @Override
                    public void onResponse(Call<HelpDetailResult> call, Response<HelpDetailResult> response) {

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
                    public void onFailure(Call<HelpDetailResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  获取求助状态
        Button helpStatus = (Button) findViewById(R.id.helpStatus);
        helpStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<HelpStatusResult> call = apiService.requestHelpStatus(58);
                call.enqueue(new Callback<HelpStatusResult>() {
                    @Override
                    public void onResponse(Call<HelpStatusResult> call, Response<HelpStatusResult> response) {

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
                    public void onFailure(Call<HelpStatusResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  请求响应
        Button responses = (Button) findViewById(R.id.responses);
        responses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<EmptyResult> call = apiService.requestResponsesHelp(59);
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

        //  请求结束求助响应
        Button finishHelp = (Button) findViewById(R.id.finishHelp);
        finishHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<EmptyResult> call = apiService.requestFinishHelp(63);
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

        //  发起求助
        Button addHelp = (Button) findViewById(R.id.addHelp);
        addHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<HelpIdResult> call = apiService.requestAddHelp("title2", "descript2", "address2", 22.95, 113.36);
                call.enqueue(new Callback<HelpIdResult>() {
                    @Override
                    public void onResponse(Call<HelpIdResult> call, Response<HelpIdResult> response) {

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
                    public void onFailure(Call<HelpIdResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  请求响应详情
        Button responseDetail = (Button) findViewById(R.id.responseDetail);
        responseDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<ResponseDetailResult> call = apiService.requestResponseDetail(56);
                call.enqueue(new Callback<ResponseDetailResult>() {
                    @Override
                    public void onResponse(Call<ResponseDetailResult> call, Response<ResponseDetailResult> response) {

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
                    public void onFailure(Call<ResponseDetailResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });

        //  发起求救
        Button emergency = (Button) findViewById(R.id.emergency);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<EmptyResult> call = apiService.requestEmergency();
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

        //  获取用户事件消息
        Button user = (Button) findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<UserResult> call = apiService.requestUser(CurrentUser.id);
                call.enqueue(new Callback<UserResult>() {
                    @Override
                    public void onResponse(Call<UserResult> call, Response<UserResult> response) {

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
                    public void onFailure(Call<UserResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });


        //  获取用户基本消息
        Button userInfo = (Button) findViewById(R.id.userInfo);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<UserInfoResult> call = apiService.requestUserInfo(CurrentUser.id);
                call.enqueue(new Callback<UserInfoResult>() {
                    @Override
                    public void onResponse(Call<UserInfoResult> call, Response<UserInfoResult> response) {

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
                    public void onFailure(Call<UserInfoResult> call, Throwable t) {
                        ToastUtils.show(APITestActivity.this, t.toString());
                    }
                });
            }
        });


    }
}
