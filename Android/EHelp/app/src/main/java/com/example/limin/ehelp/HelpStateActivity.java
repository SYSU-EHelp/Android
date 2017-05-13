package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yunzhao on 2017/5/13.
 */

public class HelpStateActivity extends AppCompatActivity {
    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private TextView tv_helptitle;
    private TextView tv_helpernum;
    private TextView tv_contacthelperhint;
    private ListView helperlist;
    private Button btn_finishhelpevent;

    private SimpleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpstate);

        setTitle();
        findView();

        adapter = new SimpleAdapter(this, getHelper(), R.layout.layout_helperitem,
                new String[]{"avatar", "name", "phone"}, new int[]{R.id.avatar, R.id.tv_helpername, R.id.tv_phone});

        helperlist.setAdapter(adapter);

        btn_finishhelpevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 应跳转至求助详情页面的事件结束状态，该页面尚未完成
                Intent intent = new Intent(HelpStateActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
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

        tv_title.setText("正在求助");

        tv_nextope.setVisibility(View.GONE);
    }

    private void findView() {
        tv_helptitle = (TextView) findViewById(R.id.et_helptitle);
        tv_helpernum = (TextView) findViewById(R.id.tv_helpernum);
        tv_contacthelperhint = (TextView) findViewById(R.id.tv_contacthelperhint);
        helperlist = (ListView) findViewById(R.id.helperlist);
        btn_finishhelpevent = (Button) findViewById(R.id.btn_finishhelpevent);
    }

    // 网络访问
    private List<Map<String, Object>> getHelper() {
        int[] avatars = {R.mipmap.avatar, R.mipmap.avatar, R.mipmap.avatar};
        String[] names = {"张三", "李四", "王五"};
        String[] phones = {"15566667777", "15566667778", "15566667779"};

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < avatars.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("avatar", avatars[i]);
            item.put("name", names[i]);
            item.put("phone", phones[i]);
            list.add(item);
        }

        return list;
    }
}
