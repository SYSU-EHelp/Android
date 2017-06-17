package com.example.limin.ehelp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.limin.ehelp.R.id.questionname;

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
    private ListView anwserslist;
    private Button btn_gohelp;

    // 数据
    private String title;
    private String questioncontent;
    private String questionname;
    private String anwsercount;

    // 网络访问
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questiondetail);

        setTitle();
        findView();
        getData();
        setView();

        apiService = ApiService.retrofit.create(ApiService.class);

        btn_gohelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionDetailActivity.this, AnwserQuestionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        questioncontent = bundle.getString("questioncontent");
        questionname = bundle.getString("questionname");
        anwsercount = bundle.getString("anwsercount");
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

    private void setView() {
        questiondetailtitle.setText(title);
        questiondetailcontent.setText(questioncontent);
        questiondetailname.setText(questionname);
        questiondetailtime.setText(anwsercount);
    }

    private void findView() {
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        questiondetailname = (TextView) findViewById(R.id.questiondetailname);
        questiondetailtime = (TextView) findViewById(R.id.questiondetailtime);
        questiondetailtitle = (TextView) findViewById(R.id.questiondetailtitle);
        questiondetailcontent = (TextView) findViewById(R.id.questiondetailcontent);
        questiondetailcount = (TextView) findViewById(R.id.questiondetailcount);
        anwserslist = (ListView) findViewById(R.id.anwserslist);
        btn_gohelp = (Button) findViewById(R.id.btn_gohelp);
    }
}
