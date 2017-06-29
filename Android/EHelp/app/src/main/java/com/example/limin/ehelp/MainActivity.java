package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.ApiServiceRequestResultHandler;
import com.example.limin.ehelp.networkservice.LoginResult;
import com.example.limin.ehelp.networkservice.SimpleRequest;
import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ScrollView scrolllayout;
    private TableLayout tablelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ApiService apiService = ApiService.retrofit.create(ApiService.class);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);
        final TextView register = (TextView)findViewById(R.id.register);
        scrolllayout = (ScrollView) findViewById(R.id.scrolllayout);
        tablelayout = (TableLayout) findViewById(R.id.tablelayout);

        // 不允许输入换行
        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });


//        username.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                changeScrollView();
//                return false;
//            }
//        });

//        password.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                changeScrollView();
//                return false;
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(username.getText().toString())) {
                    Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {

                    SimpleRequest.getInstance().login(
                        MainActivity.this,
                        username.getText().toString().trim(),
                        password.getText().toString().trim(),
                        new ApiServiceRequestResultHandler() {
                            @Override
                            public void onSuccess(Object dataBean) {
                                // example
    //                          ToastUtils.show(MainActivity.this, new Gson().toJson((LoginBean)dataBean));
                                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Object errorMessage) {
                                //ToastUtils.show(MainActivity.this,(String) errorMessage);
                            }
                        });


                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Register1.class);
                startActivity(intent);
            }
        });

        //  先进行cookit 注入
        String cookit = getSharedPreferences("login_info", MODE_PRIVATE).getString("cookit", "");
        if (cookit == "") {
            //ToastUtils.show(MainActivity.this, "你还未来曾经登录");
            return;
        }
        CurrentUser.cookie = cookit;

        Call<LoginResult> call = apiService.requestAutoLogin();
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (!response.isSuccessful()) {
                    //ToastUtils.show(MainActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    //ToastUtils.show(MainActivity.this, response.body().errmsg);
                    return;
                }
                //Toast.makeText(MainActivity.this, ToastUtils.LOGIN_SUCCESS + response.body().data.id, Toast.LENGTH_SHORT).show();
                CurrentUser.id = response.body().data.id;
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {

            }
        });


//        // -------------测试部分--------------
//        Button apiTest = (Button) findViewById(R.id.apiTest);
//        apiTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,APITestActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void changeScrollView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrolllayout.scrollTo(0, scrolllayout.getHeight());
            }
        }, 100);
    }

}
