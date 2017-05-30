package com.example.limin.ehelp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yunzhao on 2017/5/13.
 */

public class QuestionDetailActivity extends AppCompatActivity {

    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private RoundedImageView avatar;
    private TextView questiondetailname;
    private TextView questiondetailtime;
    private TextView questiondetailtitle;
    private TextView questiondetailcontent;
    private TextView questiondetailcount;
    private Button btn_gohelp;

    // 数据
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questiondetail);

        setTitle();
        findView();
        getData();
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
//        title = bundle.getString("title");
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

        tv_title.setText("提问详情");

        tv_nextope.setVisibility(View.GONE);
    }



    private void findView() {
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        questiondetailname = (TextView) findViewById(R.id.questiondetailname);
        questiondetailtime = (TextView) findViewById(R.id.questiondetailtime);
        questiondetailtitle = (TextView) findViewById(R.id.questiondetailtitle);
        questiondetailcontent = (TextView) findViewById(R.id.questiondetailcontent);
        questiondetailcount = (TextView) findViewById(R.id.questiondetailcount);
        btn_gohelp = (Button) findViewById(R.id.btn_gohelp);
    }
}
