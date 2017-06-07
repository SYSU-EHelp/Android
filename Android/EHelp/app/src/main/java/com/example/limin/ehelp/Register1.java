package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);

        TextView next = (TextView) findViewById(R.id.next);
        final EditText phone = (EditText)findViewById(R.id.phone);
        final EditText verified = (EditText)findViewById(R.id.verified);
        final TextView send_verified = (TextView) findViewById(R.id.send_verified);

        send_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(Register1.this, "请输入您的手机号", Toast.LENGTH_SHORT).show();
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
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(Register1.this, "请输入您的手机号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(verified.getText().toString())){
                    Toast.makeText(Register1.this, "请先输入您的手机验证码", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Register1.this,Register2.class);
                    intent.putExtra("code", verified.getText().toString().trim());
                    intent.putExtra("phone",phone.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }
}

