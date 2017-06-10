package com.example.limin.ehelp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.limin.ehelp.bean.HelpBean;
import com.example.limin.ehelp.bean.UserBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.UserResult;
import com.example.limin.ehelp.utility.CurrentUser;
import com.example.limin.ehelp.utility.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yunzhao on 2017/5/7.
 */

public class StateFragment extends Fragment {
    private ListView lv;
    private ApiService apiService;
    private UserBean userdata;
    private SimpleAdapter adapter;
    private List<UserBean.Launch> launch = new ArrayList<>();
    private List<UserBean.Response> response = new ArrayList<>();
    private List<Map<String, Object>> userListData = new ArrayList<Map<String, Object>>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_statelist, container, false);
        apiService = ApiService.retrofit.create(ApiService.class);
        getData();

        lv = (ListView)root.findViewById(R.id.statelist);
        adapter = new SimpleAdapter(getContext(),userListData,R.layout.layout_stateitem,
                new String[] {"statename", "statetype", "statetime", "stateanswer"}, new int[] {R.id.state_name,
                R.id.state_type, R.id.state_time, R.id.state_answer});
        lv.setAdapter(adapter);
        return root;
    }

    private void getData() {
        Call<UserResult> call = apiService.requestUser(CurrentUser.id);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {

                if (!response.isSuccessful()) {
                    ToastUtils.show(getContext(), ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    ToastUtils.show(getContext(), response.body().errmsg);
                    return;
                }
                ToastUtils.show(getContext(), new Gson().toJson(response.body()));
                userdata = response.body().data;
                //遍历发起列表
                for(int i = 0; i < userdata.launch.size();i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    if (userdata.launch.get(i).type == 0) {
                        item.put("statetype", "我发起的，提问");
                        item.put("statename", userdata.launch.get(i).title);
                        item.put("statetime", userdata.launch.get(i).date);
                        item.put("stateanswer", userdata.launch.get(i).num+"人响应");
                        userListData.add(item);
                    } else if (userdata.launch.get(i).type == 1) {
                        item.put("statetype", "我发起的，求助");
                        item.put("statename", userdata.launch.get(i).title);
                        item.put("statetime", userdata.launch.get(i).date);
                        item.put("stateanswer", userdata.launch.get(i).num+"人响应");
                        userListData.add(item);
                    } else if (userdata.launch.get(i).type == 2) {
                        item.put("statetype", "我发起的，求救");
                        item.put("statename", userdata.launch.get(i).title);
                        item.put("statetime", userdata.launch.get(i).date);
                        item.put("stateanswer", userdata.launch.get(i).num+"人响应");
                        userListData.add(item);
                    }
                }
                //遍历响应列表
                for(int i = 0; i < userdata.response.size();i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    if (userdata.launch.get(i).type == 0) {
                        item.put("statetype", "我响应的，提问");
                        item.put("statename", userdata.response.get(i).title);
                        item.put("statetime", "发起人:" + userdata.response.get(i).launcher_username);
                        item.put("stateanswer", userdata.response.get(i).num+"人响应");
                        userListData.add(item);
                    } else if (userdata.launch.get(i).type == 1) {
                        item.put("statetype", "我响应的，求助");
                        item.put("statename", userdata.response.get(i).title);
                        item.put("statetime", "发起人:" + userdata.response.get(i).launcher_username);
                        item.put("stateanswer", userdata.response.get(i).num+"人响应");
                        userListData.add(item);
                    } else if (userdata.launch.get(i).type == 2) {
                        item.put("statetype", "我响应的，求救");
                        item.put("statename", userdata.response.get(i).title);
                        item.put("statetime", "发起人:" + userdata.response.get(i).launcher_username);
                        item.put("stateanswer", userdata.response.get(i).num+"人响应");
                        userListData.add(item);
                    }
                }

                adapter.notifyDataSetChanged();

            }
            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                ToastUtils.show(getContext(), t.toString());
            }
        });
    }
    private List<Map<String, Object>> getlistData() {
        String[] statename = new String[] {"请问有没有人帮忙拿一下快递？","请问有没有人帮忙领一下校园卡","请问有没有人帮忙拿一下快递？","请问中大热水怎么充值？","请问哪里有中大方格纸卖？"};
        String[] statetype = new String[] {"我响应的，求助","我发起的，求助","我发起的，求助","我发起的，提问","我回答的，提问"};
        String[] statetime = new String[] {"10分钟前","5小时前","1小时前","1天前","2小时前"};
        String[] stateanswer = new String[] {"1人响应","4人响应","10人响应","10人回答","9人回答"};

        List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
        for (int i = 0;i<statename.length;i++) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("statename",statename[i]);
            temp.put("statetype",statetype[i]);
            temp.put("statetime", statetime[i]);
            temp.put("stateanswer",stateanswer[i]);
            ls.add(temp);
        }
        return ls;

    }
}
