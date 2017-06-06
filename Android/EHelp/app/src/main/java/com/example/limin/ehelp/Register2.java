package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.RegisterResult;
import com.example.limin.ehelp.utility.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by limin on 2017/4/30.
 */
public class Register2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);
        Bundle bundle = this.getIntent().getExtras();
        final String code = bundle.getString("code");
        final String phone = bundle.getString("phone");

        TextView done = (TextView) findViewById(R.id.done);

        final EditText mima = (EditText)findViewById(R.id.mima);
        final EditText againmima = (EditText)findViewById(R.id.againmima);
        final EditText user = (EditText)findViewById(R.id.user);
        final ApiService apiService = ApiService.retrofit.create(ApiService.class);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(user.getText().toString())) {
                    Toast.makeText(Register2.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mima.getText().toString())){
                    Toast.makeText(Register2.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(againmima.getText().toString())){
                    Toast.makeText(Register2.this, "请重复输入密码", Toast.LENGTH_SHORT).show();
                } else if (!mima.getText().toString().equals(againmima.getText().toString())){
                    Toast.makeText(Register2.this, "两次输入的密码不匹配", Toast.LENGTH_SHORT).show();
                } else if (mima.getText().toString().equals(againmima.getText().toString())){


                    Call<RegisterResult> call = apiService.requestRegister(
                            code,
                            phone,
                            user.getText().toString().trim(),
                            mima.getText().toString().trim());

                    call.enqueue(new Callback<RegisterResult>() {
                        @Override
                        public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                            if (!response.isSuccessful()) {
                                ToastUtils.show(Register2.this, ToastUtils.SERVER_ERROR);
                                return;
                            }
                            if (response.body().status != 200) {
                                ToastUtils.show(Register2.this, response.body().errmsg);
                                return;
                            }
                            ToastUtils.show(Register2.this, "注册成功");
                            Intent intent = new Intent(Register2.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<RegisterResult> call, Throwable t) {
                            ToastUtils.show(Register2.this, ToastUtils.NETWORK_ERROR);
                        }
                    });
                }
            }
        });
    }
}