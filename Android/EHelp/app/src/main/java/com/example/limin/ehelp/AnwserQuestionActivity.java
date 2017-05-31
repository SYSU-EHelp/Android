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


/**
 * Created by Yunzhao on 2017/5/10.
 */

public class AnwserQuestionActivity extends AppCompatActivity {
    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private EditText anwserquestiontitle;
    private TextView anwserquestioncontent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anwserquestion);

        setTitle();
        findView();

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
    }



    private void findView() {
        anwserquestiontitle = (EditText) findViewById(R.id.anwserquestiontitle);
        anwserquestioncontent = (TextView) findViewById(R.id.anwserquestioncontent);
    }
}
