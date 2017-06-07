package com.example.limin.ehelp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.UserInfoBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.UserInfoResult;
import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tompkins on 2017/5/9.
 */

public class Information extends AppCompatActivity {
    private Button btn_back;
    private TextView title;
    private TextView next;
    private TextView username;
    private TextView phone;
    private ImageView icon;
    private ApiService apiService;
    private UserInfoBean userInfoBean;
    private TextView name;
    private TextView gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        apiService = ApiService.retrofit.create(ApiService.class);
        setTitle();
        getData();
        icon = (ImageView)findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //设置格式为图片可以打开所有图片
                intent.setType("image:/*");
                startActivityForResult(intent,1);
            }
        });

        RandomNG();

    }

    private void getData() {
        Call<UserInfoResult> call = apiService.requestUserInfo(CurrentUser.id);
        call.enqueue(new Callback<UserInfoResult>() {
            @Override
            public void onResponse(Call<UserInfoResult> call, Response<UserInfoResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(Information.this, ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(Information.this, response.body().errmsg);
                    return;
                }
                ToastUtils.show(Information.this, new Gson().toJson(response.body()));
                userInfoBean = response.body().data;
                username.setText(userInfoBean.username);
                phone.setText(userInfoBean.phone);
            }
            @Override
            public void onFailure(Call<UserInfoResult> call, Throwable t) {
                ToastUtils.show(Information.this, t.toString());
            }
        });
    }

    private void RandomNG() {
        name = (TextView)findViewById(R.id.name);
        gender = (TextView)findViewById(R.id.gender);
        float a = (float)(Math.random()*100);
        if (a > 0 && a < 25) {
            name.setText("胡南");
            gender.setText("男");
        } else if (a>25 && a < 50) {
            name.setText("李敏慧");
            gender.setText("女");
        } else if (a>50 && a<75) {
            name.setText("林国丹");
            gender.setText("男");
        } else if (a>75 && a < 100) {
            name.setText("李为");
            gender.setText("男");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                icon.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setTitle() {
        btn_back = (Button) findViewById(R.id.btn_back);
        title = (TextView) findViewById(R.id.tv_title);
        next = (TextView) findViewById(R.id.tv_nextope);
        username = (TextView) findViewById(R.id.username);
        phone = (TextView) findViewById(R.id.phone);
        next = (TextView) findViewById(R.id.tv_nextope);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title.setText("个人信息");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String un = username.getText().toString();
                String ph = phone.getText().toString();
                //这里需要将个人信息数据传入数据库
                /*Toast.makeText(Information.this, "修改个人信息成功!", Toast.LENGTH_SHORT).show();*/
                finish();
            }
        });
    }
}
