package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.SendCodeBean;
import com.example.limin.ehelp.networkservice.ApiServiceRequestResultHandler;
import com.example.limin.ehelp.networkservice.SimpleRequest;
import com.example.limin.ehelp.utility.ToastUtils;

/**
 * Created by limin on 2017/4/30.
 */
public class Register1 extends AppCompatActivity {
    private TextView send_verified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);

        TextView next = (TextView) findViewById(R.id.next);
        final EditText phone = (EditText)findViewById(R.id.phone);
        final EditText verified = (EditText)findViewById(R.id.verified);
        send_verified = (TextView) findViewById(R.id.send_verified);

        send_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(Register1.this, "请输入您的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.getText().toString().length() != 11) {
                    Toast.makeText(getApplicationContext(),"请输入11位有效手机号！",Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleRequest.getInstance().sendCode(phone.getText().toString().trim(), new ApiServiceRequestResultHandler() {
                    @Override
                    public void onSuccess(Object dataBean) {
                        SendCodeBean code = (SendCodeBean) dataBean;
                        ToastUtils.show(Register1.this, code.code);
                    }
                    @Override
                    public void onError(Object errorMessage) {
                        ToastUtils.show(Register1.this, (String)errorMessage);
                    }
                });
                timer.start();
                send_verified.setClickable(false);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(Register1.this, "请输入您的手机号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(verified.getText().toString())){
                    Toast.makeText(Register1.this, "请先输入您的手机验证码", Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().length() != 11) {
                    Toast.makeText(getApplicationContext(),"请输入11位有效手机号！",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(Register1.this,Register2.class);
                    intent.putExtra("code", verified.getText().toString().trim());
                    intent.putExtra("phone",phone.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });


    }
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            send_verified.setText((millisUntilFinished / 1000) + "秒");
            send_verified.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onFinish() {
            send_verified.setEnabled(true);
            send_verified.setClickable(true);
            send_verified.setText("获取验证码");
            send_verified.setTextColor(getResources().getColor(R.color.verified));

            /*Call<EmptyResult> call = apiService.requestEmergency();
            call.enqueue(new Callback<EmptyResult>() {
                @Override
                public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                    if (!response.isSuccessful()) {
                        ToastUtils.show(EmergencyHelp.this, ToastUtils.SERVER_ERROR);
                        return;
                    }
                    if (response.body().status != 200) {
                        ToastUtils.show(EmergencyHelp.this, response.body().errmsg);
                        return;
                    }
                    ToastUtils.show(EmergencyHelp.this, new Gson().toJson(response.body()));
                }
                @Override
                public void onFailure(Call<EmptyResult> call, Throwable t) {
                    ToastUtils.show(EmergencyHelp.this, t.toString());
                }
            });*/
        }
    };
}

