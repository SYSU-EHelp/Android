package com.example.limin.ehelp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = this.getIntent().getExtras();
        apiService = ApiService.retrofit.create(ApiService.class);

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

                    Toast.makeText(HomeActivity.this, "已经退出登录", Toast.LENGTH_SHORT).show();
                    finish();
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
}

