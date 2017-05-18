package com.example.limin.ehelp.utility;

import android.content.Context;
import android.widget.Toast;

import java.util.Objects;

/**
 * Created by Administrator on 2017/05/17.
 */

public class ToastUtils {
    public static void show(final Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static final String NETWORK_ERROR = "网络请求失败";
    public static final String SERVER_ERROR = "服务器错误";
    public  static final  String REGISTER_SUCCESS = "注册成功";
    public static final  String LOGIN_SUCCESS = "登录成功";
}
