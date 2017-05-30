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

public class EditQuestionActivity extends AppCompatActivity {
    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private EditText editquestiontitle;
    private TextView editquestionwordcount;
    private EditText editquestioncontent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editquestion);

        setTitle();
        findView();


        // 更新求助标题字数统计
        editquestiontitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editquestionwordcount.setText(editquestiontitle.getText().toString().length() + "/20");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

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

        tv_title.setText("发提问");
        tv_nextope.setText("发送");
    }



    private void findView() {
        editquestiontitle = (EditText) findViewById(R.id.editquestiontitle);
        editquestionwordcount = (TextView) findViewById(R.id.editquestionwordcount);
        editquestioncontent = (EditText) findViewById(R.id.editquestioncontent);
    }




}
