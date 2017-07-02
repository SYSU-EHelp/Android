package com.example.limin.ehelp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.limin.ehelp.bean.HelpBean;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.HelpDetailResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yunzhao on 2017/7/2.
 */

public class HelpFinishActivity extends AppCompatActivity {

    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private RoundedImageView avatar;
    private TextView tv_name;
    private TextView tv_helptitle;
    private TextView tv_content;
    private TextView tv_time;
    private TextView tv_address;

    // 地图
    private MapView map;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle;
    private UiSettings mUiSettings;//定义一个UiSettings对象

    // 网络访问
    private int id = 0;
    private ApiService apiService = ApiService.retrofit.create(ApiService.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpfinish);


        setTitle();
        findView();
        setMap(savedInstanceState);
        getDataAndInit();

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

        tv_title.setText("求助已结束");

        tv_nextope.setVisibility(View.GONE);
    }

    private void findView() {
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_helptitle = (TextView) findViewById(R.id.tv_helptitle);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
        map = (MapView) findViewById(R.id.map);
    }

    private void setMap(Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        map.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = map.getMap();
        }
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }

    private void getDataAndInit() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");

        Call<HelpDetailResult> call = apiService.requestHelpDetail(id);
        call.enqueue(new Callback<HelpDetailResult>() {
            @Override
            public void onResponse(Call<HelpDetailResult> call, Response<HelpDetailResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(HelpFinishActivity.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(HelpFinishActivity.this, response.body().errmsg);
                    return;
                }
                //ToastUtils.show(HelpDetailActivity.this, new Gson().toJson(response.body()));
                HelpBean helpData = response.body().data;
                tv_helptitle.setText(helpData.title);
                tv_content.setText(helpData.description);
                tv_address.setText("帮助地点：" + helpData.address);
                tv_time.setText("发起时间：" + helpData.date);
                double longitude = helpData.longitude;
                double latitude = helpData.latitude;
                tv_name.setText(helpData.launcher_username);
                addMarker(longitude, latitude, helpData.address);
            }

            @Override
            public void onFailure(Call<HelpDetailResult> call, Throwable t) {
                ToastUtils.show(HelpFinishActivity.this, t.toString());
            }
        });

    }

    private void addMarker(double longitude, double latitude, String address) {
        LatLng latLng = new LatLng(latitude, longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("帮助地点：").snippet(address));
        marker.showInfoWindow();

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

}
