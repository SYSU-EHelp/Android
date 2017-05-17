package com.example.limin.ehelp.utility;

/**
 * Created by Administrator on 2017/05/17.
 */

public class Global {
    private static Global mInstance = null;
    public static Global getInstance() {
        if (mInstance == null) {
            mInstance = new Global();
        }
        return mInstance;
    }
    public String baseUrl = "http://119.29.246.121:8080";
    private Global() {
    }
}
