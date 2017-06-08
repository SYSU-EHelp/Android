package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by limin on 2017/5/17.
 */

public class EmergencyHelp extends AppCompatActivity{
    //final Button exit = (Button)findViewById(R.id.exit_help);
    private Button exit;
    private static final String TAG = "EmergencyHelp";
    private TextView tv_helpernum;
    final ApiService apiService = ApiService.retrofit.create(ApiService.class);
    private TextView hint_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_help);

        tv_helpernum = (TextView)findViewById(R.id.tv_helpernum);
        exit = (Button)findViewById(R.id.exit_help);
        hint_code = (TextView)findViewById(R.id.hint);

        timer.start();
        

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyHelp.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private CountDownTimer timer = new CountDownTimer(6000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tv_helpernum.setText((millisUntilFinished / 1000) + "");
        }

        @Override
        public void onFinish() {
            tv_helpernum.setEnabled(true);
            tv_helpernum.setText("0");

            Call<EmptyResult> call = apiService.requestEmergency();
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
            });
            exit.setText("已经发送紧急求救信息");
            exit.setBackgroundColor(getResources().getColor(R.color.mGray));
            exit.setClickable(false);
            hint_code.setText("我们已经向您的紧急联系人发送预先编辑好的求救信息！");

        }
    };
}
