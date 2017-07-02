package com.example.limin.ehelp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.limin.ehelp.bean.HelpBean;
import com.example.limin.ehelp.bean.HelpStatusBean;
import com.example.limin.ehelp.bean.ResponseDetailBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.networkservice.HelpDetailResult;
import com.example.limin.ehelp.networkservice.HelpStatusResult;
import com.example.limin.ehelp.networkservice.ResponseDetailResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yunzhao on 2017/5/13.
 */

public class HelpDetailActivity extends AppCompatActivity {

    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private RoundedImageView avatar;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_helptitle;
    private TextView tv_content;
    private TextView tv_time;
    private TextView tv_address;
    private Button btn_gohelp;

    // 数据
    private int id = 0;
    private String title;
    private String description;
    private String address;
    private String date;
    private double longitude;
    private double latitude;
    private String launcher_username;
    private String launcher_avatar;
    private String phone;
    private int finished;

    // 地图
    private MapView map;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    // 网络访问
    private ApiService apiService = ApiService.retrofit.create(ApiService.class);
    ;

    // 本地数据
    private int helpingEventID;
    private boolean canHelp;

    // 10秒检测一次求助是否结束
    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, 10 * 1000);
            refreshEventState();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpdetail);

        // 更新正在响应的求助的状态
        SharedPreferences sp = getSharedPreferences("helpingEventID", Context.MODE_PRIVATE);
        helpingEventID = sp.getInt("id", -1);
        if (helpingEventID != -1) {
            updateHelpingState(helpingEventID);
        }

        //初始化
        setTitle();
        findView();
        map.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = map.getMap();
        }
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);

        // 网络访问
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        if (isEventFinished(id)) {  // 如果事件已结束则跳转至求助结束页面
            Intent intent = new Intent(HelpDetailActivity.this, HelpFinishActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        getDataAndInit();

        canHelp = (helpingEventID == -1) & (finished == 0);
        handler.postDelayed(task, 10 * 1000);

        if (canHelp)
            btn_gohelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dialog参数设置
                    AlertDialog.Builder builder = new AlertDialog.Builder(HelpDetailActivity.this);  //先得到构造器
                    builder.setTitle("提示"); //设置标题
                    builder.setMessage("是否确认去帮TA?"); //设置内容
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<EmptyResult> call = apiService.requestResponsesHelp(id);
                            //Toast.makeText(HelpDetailActivity.this, id + "", Toast.LENGTH_SHORT).show();
                            call.enqueue(new Callback<EmptyResult>() {
                                @Override
                                public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                                    if (!response.isSuccessful()) {
                                        ToastUtils.show(HelpDetailActivity.this, ToastUtils.SERVER_ERROR);
                                        return;
                                    }
                                    if (response.body().status != 200) {
                                        ToastUtils.show(HelpDetailActivity.this, response.body().errmsg);
                                        return;
                                    }
                                    ToastUtils.show(HelpDetailActivity.this, new Gson().toJson(response.body()));
                                    btn_gohelp.setClickable(false);
                                    btn_gohelp.setText("您正响应该求助，建议您打电话联系求助者");
                                    btn_gohelp.setBackgroundColor(R.color.mGray);
                                    tv_phone.setVisibility(View.VISIBLE);
                                    // 将正在响应的事件ID存入本地
                                    SharedPreferences sp = getSharedPreferences("helpingEventID", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putInt("id", id);
                                    editor.commit();
                                }

                                @Override
                                public void onFailure(Call<EmptyResult> call, Throwable t) {
                                    ToastUtils.show(HelpDetailActivity.this, t.toString());
                                }
                            });

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

    private void getDataAndInit() {
        Call<HelpDetailResult> call = apiService.requestHelpDetail(id);
        call.enqueue(new Callback<HelpDetailResult>() {
            @Override
            public void onResponse(Call<HelpDetailResult> call, Response<HelpDetailResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(HelpDetailActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(HelpDetailActivity.this, response.body().errmsg);
                    return;
                }
                //ToastUtils.show(HelpDetailActivity.this, new Gson().toJson(response.body()));
                HelpBean helpData = response.body().data;
                title = helpData.title;
                description = helpData.description;
                address = helpData.address;
                date = helpData.date;
                longitude = helpData.longitude;
                latitude = helpData.latitude;
                launcher_username = helpData.launcher_username;
                launcher_avatar = helpData.launcher_avatar;
                phone = helpData.phone;
                finished = helpData.finished;

                setView();
                addMarker();
            }

            @Override
            public void onFailure(Call<HelpDetailResult> call, Throwable t) {
                ToastUtils.show(HelpDetailActivity.this, t.toString());
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

        tv_title.setText("求助详情");

        tv_nextope.setVisibility(View.GONE);
    }

    // 进入页面时执行一次
    private void setView() {
        tv_name.setText(launcher_username);
        tv_phone.setText(phone);
        tv_helptitle.setText(title);
        tv_content.setText(description);
        tv_time.setText("发起时间：" + date);
        tv_address.setText("帮助地点：" + address);

        if (finished == 1) {
            btn_gohelp.setClickable(false);
            btn_gohelp.setText("求助事件已结束");
            btn_gohelp.setBackgroundColor(R.color.mGray);
        } else if (helpingEventID == id) {
            btn_gohelp.setClickable(false);
            btn_gohelp.setText("您正响应该求助，建议您打电话联系求助者");
            btn_gohelp.setBackgroundColor(R.color.mGray);
            tv_phone.setVisibility(View.VISIBLE);
        } else if (helpingEventID != -1) {
            btn_gohelp.setClickable(false);
            btn_gohelp.setText("您正响应其他求助事件，暂时无法响应该求助");
            btn_gohelp.setBackgroundColor(R.color.mGray);
        }
    }

    private void findView() {
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_helptitle = (TextView) findViewById(R.id.tv_helptitle);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
        map = (MapView) findViewById(R.id.map);
        btn_gohelp = (Button) findViewById(R.id.btn_gohelp);
    }

    // 网络访问
    private void refreshEventState() {
        //  获取求助的finish状态
        Call<HelpStatusResult> call = apiService.requestHelpStatus(id);
        call.enqueue(new Callback<HelpStatusResult>() {
            @Override
            public void onResponse(Call<HelpStatusResult> call, Response<HelpStatusResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(HelpDetailActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(HelpDetailActivity.this, response.body().errmsg);
                    return;
                }
                //ToastUtils.show(HelpDetailActivity.this, new Gson().toJson(response.body()));
                HelpStatusBean helpStatus = response.body().data;
                // 如果求助已结束，则跳转页面
                if (helpStatus.finished == 1) {
                    // 清除本地存储的事件ID
                    if (helpingEventID == id) {
                        SharedPreferences sp = getSharedPreferences("helpingEventID", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("id");
                        editor.commit();
                    }

                    Intent intent = new Intent(HelpDetailActivity.this, HelpFinishActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<HelpStatusResult> call, Throwable t) {
                ToastUtils.show(HelpDetailActivity.this, t.toString());
            }
        });
    }

    private void addMarker() {
        LatLng latLng = new LatLng(latitude, longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("帮助地点：").snippet(address));
        marker.showInfoWindow();

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    private void updateHelpingState(int helpingEventID) {
        //  获取求助的finish状态
        Call<HelpStatusResult> call = apiService.requestHelpStatus(helpingEventID);
        call.enqueue(new Callback<HelpStatusResult>() {
            @Override
            public void onResponse(Call<HelpStatusResult> call, Response<HelpStatusResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(HelpDetailActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(HelpDetailActivity.this, response.body().errmsg);
                    return;
                }
                //ToastUtils.show(HelpDetailActivity.this, new Gson().toJson(response.body()));
                HelpStatusBean helpStatus = response.body().data;
                // 如果求助已结束，则更新UI
                if (helpStatus.finished == 1) {
                    // 清除本地存储的事件ID
                    SharedPreferences sp = getSharedPreferences("helpingEventID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("id");
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<HelpStatusResult> call, Throwable t) {
                ToastUtils.show(HelpDetailActivity.this, t.toString());
            }
        });
    }

    private boolean isEventFinished(int eventID) {
        final boolean[] isFinished = {false};

        //  获取求助的finish状态
        Call<HelpStatusResult> call = apiService.requestHelpStatus(eventID);
        call.enqueue(new Callback<HelpStatusResult>() {
            @Override
            public void onResponse(Call<HelpStatusResult> call, Response<HelpStatusResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(HelpDetailActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(HelpDetailActivity.this, response.body().errmsg);
                    return;
                }
                //ToastUtils.show(HelpDetailActivity.this, new Gson().toJson(response.body()));
                HelpStatusBean helpStatus = response.body().data;

                if (helpStatus.finished == 1) {
                    isFinished[0] = true;
                }
            }

            @Override
            public void onFailure(Call<HelpStatusResult> call, Throwable t) {
                ToastUtils.show(HelpDetailActivity.this, t.toString());
            }
        });

        return isFinished[0];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause();
        handler.removeCallbacks(task);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }

}
