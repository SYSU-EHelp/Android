package com.example.limin.ehelp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.ResponseDetailBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.networkservice.ResponseDetailResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // 数据
    private String helpTitle = "";
    private int helpNum;
    List<Map<String, Object>> responserlist = new ArrayList<Map<String, Object>>();

    // 网络访问
    private int help_id;
    private ApiService apiService;

    // 5秒刷新一次响应者列表
    private Handler handler = new Handler();
    private Runnable task =new Runnable() {
        public void run() {
            handler.postDelayed(this, 5*1000);
            getHelperAndRefresh();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpstate);

        apiService = ApiService.retrofit.create(ApiService.class);

        // 接收上个页面传来的数据
        Bundle bundle = this.getIntent().getExtras();
        helpTitle = bundle.getString("title");
        help_id = bundle.getInt("helpid");

        // 初始化
        setTitle();
        findView();
        initView();

        getHelperAndRefresh();

        handler.postDelayed(task, 5*1000);

        // 设置响应者列表
        adapter = new SimpleAdapter(this, responserlist, R.layout.layout_helperitem,
                new String[]{"avatar", "name", "phone"}, new int[]{R.id.avatar, R.id.tv_helpername, R.id.tv_phone});
        helperlist.setAdapter(adapter);

        btn_finishhelpevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog参数设置
                AlertDialog.Builder builder=new AlertDialog.Builder(HelpStateActivity.this);
                builder.setTitle("提示"); //设置标题
                builder.setMessage("您确定结束求助?"); //设置内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 结束求助
                        Call<EmptyResult> call = apiService.requestFinishHelp(help_id);
                        call.enqueue(new Callback<EmptyResult>() {
                            @Override
                            public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                                if (!response.isSuccessful()) {
                                    ToastUtils.show(HelpStateActivity.this, ToastUtils.SERVER_ERROR);
                                    return;
                                }
                                if (response.body().status != 200) {
                                    ToastUtils.show(HelpStateActivity.this, response.body().errmsg);
                                    return;
                                }
                                ToastUtils.show(HelpStateActivity.this, new Gson().toJson(response.body()));
                            }
                            @Override
                            public void onFailure(Call<EmptyResult> call, Throwable t) {
                                ToastUtils.show(HelpStateActivity.this, t.toString());
                            }
                        });

                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
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
        tv_helptitle = (TextView) findViewById(R.id.tv_helptitle);
        tv_helpernum = (TextView) findViewById(R.id.tv_helpernum);
        tv_contacthelperhint = (TextView) findViewById(R.id.tv_contacthelperhint);
        helperlist = (ListView) findViewById(R.id.helperlist);
        btn_finishhelpevent = (Button) findViewById(R.id.btn_finishhelpevent);
    }

    private void initView() {
        tv_helptitle.setText(helpTitle);
    }

    // 网络访问
    private void getHelperAndRefresh() {
        Call<ResponseDetailResult> call = apiService.requestResponseDetail(help_id);
        call.enqueue(new Callback<ResponseDetailResult>() {
            @Override
            public void onResponse(Call<ResponseDetailResult> call, Response<ResponseDetailResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(HelpStateActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(HelpStateActivity.this, response.body().errmsg);
                    return;
                }
                ToastUtils.show(HelpStateActivity.this, new Gson().toJson(response.body()));
                ResponseDetailBean responseDetailBean = response.body().data;
                ArrayList<HashMap<String, Object>> responsers = responseDetailBean.responser;
                responserlist.clear();
                for (int i = 0; i < responsers.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("avatar", R.mipmap.avatar);
                    item.put("name", responsers.get(i).get("responser_username"));
                    item.put("phone", responsers.get(i).get("phone"));
                    responserlist.add(item);
                }

                //responserlist = responseDetailBean.responser;

                // 更新数据
                adapter.notifyDataSetChanged();

                helpNum = responserlist.size();
                tv_helpernum.setText(helpNum + "");
                if (helpNum > 0) {
                    tv_contacthelperhint.setVisibility(View.VISIBLE);
                } else {
                    tv_contacthelperhint.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<ResponseDetailResult> call, Throwable t) {
                ToastUtils.show(HelpStateActivity.this, t.toString());
            }
        });

    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(task);
        super.onPause();
    }
}
