package com.example.limin.ehelp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.limin.ehelp.bean.HelpIdBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.networkservice.HelpIdResult;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yunzhao on 2017/5/10.
 */

public class EditHelpActivity extends AppCompatActivity {
    // 标题栏控件
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_nextope;

    // 页面控件
    private EditText et_helptitle;
    private TextView tv_wordcount;
    private EditText et_helpcontent;
    private TextView tv_helplocation;
    //private EditText et_helpernum;

    // 高德地图
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private String curLocStr = "正在获取定位...";
    private Location curLoc = null;

    // 网络访问
    private ApiService apiService;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edithelp);

        setTitle();
        findView();

        apiService = ApiService.retrofit.create(ApiService.class);

        //初始化定位
        initLocation();

        // 更新求助标题字数统计
        et_helptitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_wordcount.setText(et_helptitle.getText().toString().length() + "/20");
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

        tv_title.setText("发求助");

        tv_nextope.setText("发送");
        tv_nextope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(EditHelpActivity.this, "发送了求助", Toast.LENGTH_SHORT).show();
                if (et_helptitle.getText().toString().isEmpty()) {
                    Toast.makeText(EditHelpActivity.this, "求助标题不能为空", Toast.LENGTH_SHORT).show();
                } else if (et_helptitle.getText().toString().length() > 20) {
                    Toast.makeText(EditHelpActivity.this, "求助标题不能超过20个字", Toast.LENGTH_SHORT).show();
                } else if (et_helpcontent.getText().toString().isEmpty()) {
                    Toast.makeText(EditHelpActivity.this, "求助描述不能为空", Toast.LENGTH_SHORT).show();
                }
//                else if (et_helpernum.getText().toString().isEmpty()) {
//                    Toast.makeText(EditHelpActivity.this, "求助人数不能为空", Toast.LENGTH_SHORT).show();
//                }
                else {
                    final String title = et_helptitle.getText().toString();
                    String description = et_helpcontent.getText().toString();
                    String address = tv_helplocation.getText().toString();
                    double longitude = curLoc.getLongitude();
                    double latitude = curLoc.getLatitude();

                    Log.e("addhelp", title + " " + description + " " + address + " " + longitude + " " + latitude);

                    // 网络访问
                    Call<HelpIdResult> call = apiService.requestAddHelp(title, description, address, longitude, latitude);
                    call.enqueue(new Callback<HelpIdResult>() {
                        @Override
                        public void onResponse(Call<HelpIdResult> call, Response<HelpIdResult> response) {

                            if (!response.isSuccessful()) {
                                ToastUtils.show(EditHelpActivity.this, ToastUtils.SERVER_ERROR);
                                Log.e("addhelp", "1");
                                return;
                            }
                            if (response.body().status != 200) {
                                ToastUtils.show(EditHelpActivity.this, response.body().errmsg);
                                Log.e("addhelp", "2");
                                return;
                            }

                            //ToastUtils.show(EditHelpActivity.this, new Gson().toJson(response.body()));
                            HelpIdBean helpIdBean = response.body().data;
                            int help_id = helpIdBean.id;

                            Toast.makeText(EditHelpActivity.this, "发求助成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditHelpActivity.this, HelpStateActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("title", title);
                            bundle.putInt("helpid", help_id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onFailure(Call<HelpIdResult> call, Throwable t) {
                            ToastUtils.show(EditHelpActivity.this, t.toString());
                            Log.e("addhelp", "3");
                        }
                    });

                }
            }
        });
    }

    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                // 更新当前位置
                curLoc = location;

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    //sb.append(location.getAddress());
                    sb.append(location.getDistrict());
                    sb.append(location.getStreet());
                    sb.append(location.getDescription());
                } else {
                    //定位失败
//                    sb.append("错误码:" + location.getErrorCode() + "\n");
//                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
//                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
//                    Toast.makeText(EditHelpActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }

                curLocStr = sb.toString();
                tv_helplocation.setText(curLocStr);

            } else {
                Toast.makeText(EditHelpActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(10000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    private void findView() {
        et_helptitle = (EditText) findViewById(R.id.et_helptitle);
        tv_wordcount = (TextView) findViewById(R.id.tv_wordcount);
        et_helpcontent = (EditText) findViewById(R.id.et_helpcontent);
        tv_helplocation = (TextView) findViewById(R.id.tv_helplocation);
        //et_helpernum = (EditText) findViewById(R.id.et_helpernum);
    }

    /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        //获取权限列表
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {      //没有授权
                showMissingPermissionDialog();              //显示提示信息
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("发求助需要使用手机的定位权限");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消求助",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermissions(needPermissions);
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onPause() {
        stopLocation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
        startLocation();
        super.onResume();
    }


}
