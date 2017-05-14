package com.example.limin.ehelp;

import android.Manifest;
import android.content.DialogInterface;
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
import com.makeramen.roundedimageview.RoundedImageView;

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

    // 地图
    private MapView map;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpdetail);

        setTitle();
        findView();

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        map.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = map.getMap();
        }

        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);

        addMarker();

        btn_gohelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog参数设置
                AlertDialog.Builder builder=new AlertDialog.Builder(HelpDetailActivity.this);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("是否确认去帮TA?"); //设置内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_gohelp.setClickable(false);
                        btn_gohelp.setText("您正响应该求助，建议您打电话联系求助者");
                        btn_gohelp.setBackgroundColor(R.color.mGray);
                        tv_phone.setVisibility(View.VISIBLE);
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

        tv_title.setText("求助详情");

        tv_nextope.setVisibility(View.GONE);
    }

    private void findView() {
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_helptitle = (TextView) findViewById(R.id.et_helptitle);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
        map = (MapView) findViewById(R.id.map);
        btn_gohelp = (Button) findViewById(R.id.btn_gohelp);
    }

    private void addMarker() {
        LatLng latLng = new LatLng(22.95, 113.36);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("帮助地点：").snippet("广州大学城中山大学明德园6号"));
        marker.showInfoWindow();

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
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


}
