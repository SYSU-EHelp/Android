package com.example.limin.ehelp.networkservice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.limin.ehelp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APITestActivity extends AppCompatActivity {

    Button sendCode;
    private ApiService apiService;

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
                        Toast.makeText(APITestActivity.this, "api", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<SendCodeResult> call, Throwable t) {

                    }
                });

            }
        });
    }
}
