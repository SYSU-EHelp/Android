package com.example.limin.ehelp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.ToastUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by limin on 2017/4/30.
 */
public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private FloatingActionButton createBtn;
    private PopUpDialog popDialog;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = this.getIntent().getExtras();
        apiService = ApiService.retrofit.create(ApiService.class);

        checkPermissions(needPermissions);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("求助", HelpFragment.class)
                .add("提问", AskFragment.class)
                .add("我的", StateFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        createBtn = (FloatingActionButton) findViewById(R.id.fab);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        createBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EmergencyHelp.class);
                startActivity(intent);
                return true;
            }

        });

    }

    private void showDialog() {
        popDialog = new PopUpDialog(this, onClickListener);
        //showAtLocation(View parent, int gravity, int x, int y)
        popDialog.showAtLocation(findViewById(R.id.layout_home), Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_createhelp:
                    // Toast.makeText(HomeActivity.this, "发求助", Toast.LENGTH_SHORT).show();
                    popDialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this, EditHelpActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_createquestion:
                    popDialog.dismiss();
                    intent = new Intent(HomeActivity.this, EditQuestionActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id ==R.id.action_userinfo) {
            Intent i=new Intent(this,Information.class) ;
            startActivity(i);

            return true;
        }

        if (id ==R.id.action_urgencycontact) {
            Intent i=new Intent(this,EmergencyContact.class) ;
            startActivity(i);
            return true;
        }

        if (id == R.id.action_logout) {
            Call<EmptyResult> call = apiService.requestLogout();
            call.enqueue(new Callback<EmptyResult>() {
                @Override
                public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {
                    if (!response.isSuccessful()) {
                        ToastUtils.show(HomeActivity.this, ToastUtils.SERVER_ERROR);
                        return;
                    }
                    if (response.body().status != 200) {
                        ToastUtils.show(HomeActivity.this, response.body().errmsg);
                        return;
                    }

                    SharedPreferences.Editor editor = getSharedPreferences("login_info", MODE_PRIVATE).edit();
                    editor.putInt("id", -1);
                    editor.putString("cookit", "");
                    editor.commit();
                    CurrentUser.id = -1;
                    CurrentUser.cookie = "";

                    /*Toast.makeText(HomeActivity.this, "已经退出登录", Toast.LENGTH_SHORT).show();*/

                    Intent i=new Intent(getBaseContext(),MainActivity.class) ;
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<EmptyResult> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "退出失败？？", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                //isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {

    }


}

