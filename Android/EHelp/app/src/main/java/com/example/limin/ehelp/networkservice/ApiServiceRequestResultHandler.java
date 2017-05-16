package com.example.limin.ehelp.networkservice;

/**
 * Created by Administrator on 2017/05/17.
 */

public interface ApiServiceRequestResultHandler {
    public void onSuccess(Object dataBean);
    public void onError(Object errorMessage);
}
