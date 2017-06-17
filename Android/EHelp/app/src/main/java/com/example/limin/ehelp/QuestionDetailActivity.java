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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.limin.ehelp.bean.QuestionDetailBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.networkservice.QuestionDetailResult;
import com.example.limin.ehelp.networkservice.QuestionsResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.limin.ehelp.R.id.questionname;
import static java.security.AccessController.getContext;

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
    private int id;
    private String title;
    private String questioncontent;
    private String questionname;
    private String anwsercount;

    // 网络访问
    private ApiService apiService;
    private List<QuestionDetailBean> questionDetailData = new ArrayList<QuestionDetailBean>();
    private List<Map<String, Object>> questionDetailListData = new ArrayList<Map<String, Object>>();

    private ListView lv;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questiondetail);

        apiService = ApiService.retrofit.create(ApiService.class);

        setTitle();
        findView();
        getData();
        setView();
        getAnwserData();


        lv = (ListView) findViewById(R.id.anwserslist);
        adapter = new SimpleAdapter(this,questionDetailListData,R.layout.layout_anwseritem,
                new String[] {"anwsername", "anwsertime", "anwsercontent"},
                new int[] {R.id.anwsername, R.id.anwsertime, R.id.anwsercontent});
        lv.setAdapter(adapter);

        btn_gohelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionDetailActivity.this, AnwserQuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putString("title", title);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
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

    // 网络访问
    private void getAnwserData() {
        Call<QuestionDetailResult> call = apiService.requestQuestionDetail(id);
        call.enqueue(new Callback<QuestionDetailResult>() {
            @Override
            public void onResponse(Call<QuestionDetailResult> call, Response<QuestionDetailResult> response) {

                if (!response.isSuccessful()) {
                    return;
                }
                if (response.body().status != 200) {
                    return;
                }

                //ToastUtils.show(getContext(), new Gson().toJson(response.body()));
                // 获取求助事件的所有数据list

                questionDetailData.clear();
                questionDetailData = response.body().data;
//                 Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                // 赋值至求助列表的list
                questionDetailListData.clear();
                for (int i = 0; i < questionDetailData.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("anwsername", questionDetailData.get(i).answerer_username);
                    item.put("anwsercontent", questionDetailData.get(i).description);
                    item.put("anwsertime", questionDetailData.get(i).date);
                    questionDetailListData.add(item);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<QuestionDetailResult> call, Throwable t) {
            }
        });

    }

//    private List<Map<String, Object>> getData() {
//        String[] anwsername = new String[] {"张三","张三","张三","张三","张三"};
//        String[] anwsertime = new String[] {"3分钟前","3分钟前","3分钟前","3分钟前","3分钟前"};
//        String[] anwsercontent = new String[] {"一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金"};
//
//        List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
//        for (int i = 0;i<anwsername.length;i++) {
//            Map<String, Object> temp = new HashMap<>();
//            temp.put("anwsername",anwsername[i]);
//            temp.put("anwsertime",anwsertime[i]);
//            temp.put("anwsercontent", anwsercontent[i]);
//            ls.add(temp);
//        }
//        return ls;
//
//    }
}
