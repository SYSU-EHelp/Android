package com.example.limin.ehelp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.UserInfoBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.EmptyResult;
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
    private LinearLayout layout_name;
    private LinearLayout layout_gender;

    private String[] genders = new String[]{"男", "女"};
    private int mSingleChoiceID;
    private String mName = "";
    private String mGender = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        apiService = ApiService.retrofit.create(ApiService.class);

        setTitle();
        findView();
        getData();
        //icon = (ImageView)findViewById(R.id.icon);
        /*icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //设置格式为图片可以打开所有图片
                intent.setType("image:/*");
                startActivityForResult(intent,1);
            }
        });*/

        //RandomNG();

        // 修改信息
        layout_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(Information.this);
                new AlertDialog.Builder(Information.this).setTitle("请编辑您的姓名")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                upLoadInfo(mGender, input);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        layout_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dialog参数设置
                AlertDialog.Builder builder = new AlertDialog.Builder(Information.this);  //先得到构造器
                builder.setTitle("请选择您的性别"); //设置标题
                builder.setSingleChoiceItems(genders, mSingleChoiceID, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSingleChoiceID = i;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upLoadInfo(mSingleChoiceID + "", mName);
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

    private void findView() {
        username = (TextView) findViewById(R.id.username);
        phone = (TextView) findViewById(R.id.phone);
        next = (TextView) findViewById(R.id.tv_nextope);
        name = (TextView) findViewById(R.id.name);
        gender = (TextView) findViewById(R.id.gender);
        layout_name = (LinearLayout) findViewById(R.id.layout_name);
        layout_gender = (LinearLayout) findViewById(R.id.layout_gender);
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
                if (userInfoBean.sex.equals("0")) {
                    gender.setText("男");
                } else {
                    gender.setText("女");
                }
                mGender = userInfoBean.sex;
                mSingleChoiceID = Integer.valueOf(userInfoBean.sex);
                name.setText(userInfoBean.name);
                mName = userInfoBean.name;
            }

            @Override
            public void onFailure(Call<UserInfoResult> call, Throwable t) {
                ToastUtils.show(Information.this, t.toString());
            }
        });
    }

    /*private void RandomNG() {
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
    }*/

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

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title.setText("个人信息");
        next.setVisibility(View.GONE);
//        next.setText("保存");
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String na = name.getText().toString();
//                String gd = gender.getText().toString();
//                //这里需要将个人信息数据传入数据库
//                Toast.makeText(Information.this, "修改个人信息成功!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
    }

    private void upLoadInfo(final String newGender, final String newName) {
        //Toast.makeText(this, newGender + " " + newName, Toast.LENGTH_SHORT).show();
        Call<EmptyResult> call = apiService.requestEditInformation(CurrentUser.id, newName, newGender);
        call.enqueue(new Callback<EmptyResult>() {
            @Override
            public void onResponse(Call<EmptyResult> call, Response<EmptyResult> response) {

                if (!response.isSuccessful()) {
                    return;
                }
                if (response.body().status != 200) {
                    return;
                }
                //ToastUtils.show(Information.this, new Gson().toJson(response.body()));
                name.setText(newName);
                if (newGender.equals("0")) {
                    gender.setText("男");
                } else {
                    gender.setText("女");
                }
            }
            @Override
            public void onFailure(Call<EmptyResult> call, Throwable t) {
                //ToastUtils.show(Information.this, t.toString());
            }
        });
    }
}
