package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.utility.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by limin on 2017/5/17.
 */

public class EmergencyHelp extends AppCompatActivity{
    //final Button exit = (Button)findViewById(R.id.exit_help);
    private Button exit;
    private static final String TAG = "EmergencyHelp";
    private TextView tv_helpernum;
    final ApiService apiService = ApiService.retrofit.create(ApiService.class);
    private TextView hint_code;

    // 高德地图定位服务
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    //private Location curLoc = null;
    private String curLocStr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_help);

        //初始化定位
        //initLocation();

        tv_helpernum = (TextView)findViewById(R.id.tv_helpernum);
        exit = (Button)findViewById(R.id.exit_help);
        hint_code = (TextView)findViewById(R.id.hint);

        timer.start();
        

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyHelp.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private CountDownTimer timer = new CountDownTimer(6000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tv_helpernum.setText((millisUntilFinished / 1000) + "");
        }

        @Override
        public void onFinish() {
            tv_helpernum.setEnabled(true);
            tv_helpernum.setText("0");

            Call<EmptyResult> call = apiService.requestEmergency();
            call.enqueue(new Callback<EmptyResult>() {
                @Override
                public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                    if (!response.isSuccessful()) {
                        ToastUtils.show(EmergencyHelp.this, ToastUtils.SERVER_ERROR);
                        return;
                    }
                    if (response.body().status != 200) {
                        ToastUtils.show(EmergencyHelp.this, response.body().errmsg);
                        return;
                    }
                    //ToastUtils.show(EmergencyHelp.this, new Gson().toJson(response.body()));
                    //Toast.makeText(EmergencyHelp.this, curLocStr, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(Call<EmptyResult> call, Throwable t) {
                    ToastUtils.show(EmergencyHelp.this, t.toString());
                }
            });
            exit.setText("已经发送紧急求救信息");
            exit.setBackgroundColor(getResources().getColor(R.color.mGray));
            exit.setClickable(false);
            hint_code.setText("我们已经向您的紧急联系人发送预先编辑好的求救信息！");

        }
    };

//    private void startLocation(){
//        // 设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 启动定位
//        locationClient.startLocation();
//    }
//
//    private void stopLocation(){
//        // 停止定位
//        locationClient.stopLocation();
//    }
//
//    private void initLocation(){
//        //初始化client
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        locationOption = getDefaultOption();
//        //设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 设置定位监听
//        locationClient.setLocationListener(locationListener);
//    }
//
//    /**
//     * 定位监听
//     */
//    AMapLocationListener locationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation location) {
//            if (null != location) {
//                // 更新当前位置
//                //curLoc = location;
//
//                StringBuffer sb = new StringBuffer();
//                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
//                if(location.getErrorCode() == 0){
//                    sb.append(location.getAddress());
//                    sb.append(location.getDistrict());
//                    sb.append(location.getStreet());
//                    sb.append(location.getDescription());
//                } else {
//                    //定位失败
////                    sb.append("错误码:" + location.getErrorCode() + "\n");
////                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
////                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
////                    Toast.makeText(EditHelpActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
//                }
//
//                curLocStr = sb.toString();
//
//            } else {
//                Toast.makeText(EmergencyHelp.this, "定位失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//    private AMapLocationClientOption getDefaultOption(){
//        AMapLocationClientOption mOption = new AMapLocationClientOption();
//        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(10000);//可选，设置定位间隔。默认为2秒
//        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
//        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
//        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
//        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
//        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
//        return mOption;
//    }
//
//    @Override
//    protected void onPause() {
//        stopLocation();
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        startLocation();
//        super.onResume();
//    }


}
