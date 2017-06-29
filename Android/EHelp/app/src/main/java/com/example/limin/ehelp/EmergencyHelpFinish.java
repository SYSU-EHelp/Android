package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.limin.ehelp.bean.HelpIdBean;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.networkservice.HelpIdResult;
import com.example.limin.ehelp.utility.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yunzhao on 2017/6/29.
 */

public class EmergencyHelpFinish extends AppCompatActivity {

    private Button finish_help;
    private ApiService apiService = ApiService.retrofit.create(ApiService.class);
    private int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishemergency);

        eventID = getIntent().getIntExtra("event_id", -1);

        finish_help = (Button) findViewById(R.id.finish_help);
        finish_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventID != -1)
                    finishHelp();
            }
        });
    }

    private void finishHelp() {
        Call<EmptyResult> call = apiService.requestEmergencyFinish(eventID);
        call.enqueue(new Callback<EmptyResult>() {
            @Override
            public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(EmergencyHelpFinish.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(EmergencyHelpFinish.this, response.body().errmsg);
                    return;
                }

                //ToastUtils.show(EmergencyHelp.this, new Gson().toJson(response.body()));
                finish();
                Toast.makeText(EmergencyHelpFinish.this, "求救已结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<EmptyResult> call, Throwable t) {
                ToastUtils.show(EmergencyHelpFinish.this, t.toString());
            }
        });
    }
}
