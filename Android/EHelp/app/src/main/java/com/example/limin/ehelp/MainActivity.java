package com.example.limin.ehelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.LoginBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiServiceRequestResultHandler;
import com.example.limin.ehelp.networkservice.SimpleRequest;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);
        final TextView register = (TextView)findViewById(R.id.register);

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
                                ToastUtils.show(MainActivity.this,(String) errorMessage);
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


        // -------------测试部分--------------
        Button apiTest = (Button) findViewById(R.id.apiTest);
        apiTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,APITestActivity.class);
                startActivity(intent);
            }
        });
    }
}
