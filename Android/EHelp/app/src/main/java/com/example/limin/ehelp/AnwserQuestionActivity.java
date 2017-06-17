package com.example.limin.ehelp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Yunzhao on 2017/5/10.
 */

public class AnwserQuestionActivity extends AppCompatActivity {
    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private TextView anwserquestiontitle;
    private EditText anwserquestioncontent;

    // 数据
    private int id;
    private String title;

    // 网络访问
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anwserquestion);

        setTitle();
        findView();
        anwserquestiontitle.setText(title);

        apiService = ApiService.retrofit.create(ApiService.class);
        getData();

    }

    private void setTitle() {
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_nextope = (TextView) findViewById(R.id.tv_nextope);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_title.setText("回答");
        tv_nextope.setText("完成");

        tv_nextope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<EmptyResult> call = apiService.requestAnswerQuestion(id, anwserquestioncontent.getText().toString());
                call.enqueue(new Callback<EmptyResult>() {
                    @Override
                    public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                        if (!response.isSuccessful()) {
                            return;
                        }
                        if (response.body().status != 200) {
                            return;
                        }
                    }
                    @Override
                    public void onFailure(Call<EmptyResult> call, Throwable t) {
                    }
                });
            }
        });
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        title = bundle.getString("title");
    }

    private void findView() {
        anwserquestiontitle = (TextView) findViewById(R.id.anwserquestiontitle);
        anwserquestioncontent = (EditText) findViewById(R.id.anwserquestioncontent);
    }
}
