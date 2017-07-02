package com.example.limin.ehelp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.HelpBean;
import com.example.limin.ehelp.bean.UserBean;
import com.example.limin.ehelp.bean.UserInfoBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.HelpDetailResult;
import com.example.limin.ehelp.networkservice.UserInfoResult;
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
    private UserInfoBean userInfoBean;
    private String username;

    private SimpleAdapter adapter;
    private SwipeRefreshLayout refreshlayout;
    private static final int REFRESH_COMPLETE = 0X110;

    private List<UserBean.Launch> Launch = new ArrayList<>();
    private List<UserBean.Response> Response = new ArrayList<>();
    private List<Map<String, Object>> userListData = new ArrayList<Map<String, Object>>();

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    getData();
                    adapter.notifyDataSetChanged();
                    //changeColor();
                    refreshlayout.setRefreshing(false);
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_statelist, container, false);
        lv = (ListView)root.findViewById(R.id.statelist);
        apiService = ApiService.retrofit.create(ApiService.class);
        getData();
        refreshlayout = (SwipeRefreshLayout) root.findViewById(R.id.refreshlayout);
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });
        refreshlayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        adapter = new SimpleAdapter(getContext(),userListData,R.layout.layout_stateitem,
                new String[] {"statename", "statetype", "statetime", "stateanswer"}, new int[] {R.id.state_name,
                R.id.state_type, R.id.state_time, R.id.state_answer});

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < userdata.launch.size()) {
                    if (userdata.launch.get(position).type == 0) {
                        //我发起的提问
                        Intent intent = new Intent(getContext(), QuestionDetailActivity.class);
                        Bundle bundle = new Bundle();
                        Call<UserInfoResult> call = apiService.requestUserInfo(CurrentUser.id);
                        call.enqueue(new Callback<UserInfoResult>() {
                            @Override
                            public void onResponse(Call<UserInfoResult> call, Response<UserInfoResult> response) {

                                if (!response.isSuccessful()) {
                                    //ToastUtils.show(getContext(), ToastUtils.SERVER_ERROR);
                                    return;
                                }
                                if (response.body().status != 200) {
                                    //ToastUtils.show(getContext(), response.body().errmsg);
                                    return;
                                }
                                //ToastUtils.show(getContext(), new Gson().toJson(response.body()));
                                userInfoBean = response.body().data;
                                username = userInfoBean.username.toString();
                            }
                            @Override
                            public void onFailure(Call<UserInfoResult> call, Throwable t) {
                                //ToastUtils.show(getContext(), t.toString());
                            }
                        });
                        bundle.putString("questionname", username);
                        bundle.putInt("id", userdata.launch.get(position).id);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else if (userdata.launch.get(position).type == 1 && userdata.launch.get(position).finished == 0) {
                        Intent intent = new Intent(getContext(), HelpStateActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("helpid", userdata.launch.get(position).id);
                        bundle.putString("title", userdata.launch.get(position).title);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (userdata.launch.get(position).type == 1 && userdata.launch.get(position).finished == 1) {
                        Intent intent = new Intent(getContext(), HelpFinishActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", userdata.launch.get(position).id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (userdata.launch.get(position).type == 2) {
                        //Toast.makeText(getContext(), "您的紧急求救信息已经发送！", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), userdata.launch.get(position).finished+"", Toast.LENGTH_SHORT).show();
                        if (userdata.launch.get(position).finished == 0) {
                            Intent intent = new Intent(getContext(), EmergencyHelpFinish.class);
                            intent.putExtra("event_id", userdata.launch.get(position).id);
                            startActivity(intent);
                        } else {
                            new AlertDialog.Builder(getContext()).setTitle("求救事件已结束").setMessage("求救短信已发送给您的紧急联系人\n求救时间：" + userdata.launch.get(position).date)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    }

                } else {
                    int l = position-userdata.launch.size();
                    if (userdata.response.get(l).type == 0) {

                        //我响应的提问
                        Intent intent = new Intent(getContext(), QuestionDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", userdata.response.get(l).id);
                        bundle.putString("questionname", userdata.response.get(l).launcher_username);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else if (userdata.response.get(l).type == 1) {
                        if (userdata.response.get(l).finished == 0) {
                            Intent intent = new Intent(getContext(), HelpDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", userdata.response.get(l).id);

                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), HelpFinishActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", userdata.response.get(l).id);

                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                }





                /*if (userListData.get(position).get("statetype") == "我发起的，求助") {
                    Intent intent = new Intent(getContext(), HelpStateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", userdata.launch.get(position).id);
                    bundle.putString("title", userdata.launch.get(position).title);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (userListData.get(position).get("statetype") == "我响应的，求助"){
                    Intent intent = new Intent(getContext(), HelpStateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", userdata.launch.get(position).id);
                    bundle.putString("title", userdata.launch.get(position).title);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (userListData.get(position).get("statetype") == "我发起的，提问") {

                } else if (userListData.get(position).get("statetype") == "我响应的，提问") {

                } else if (userListData.get(position).get("statetype") == "我发起的，提问") {

                } else if (userListData.get(position).get("statetype") == "我发起的，求救") {

                } else if (userListData.get(position).get("statetype") == "我响应的，求救") {*/

            }
        });


        return root;
    }

    public void onResume() {
        getData();
        super.onResume();
    }

    /*private void changeColor() {
        for (int i = 0; i < userListData.size(); i++) {
            if (userListData.get(i).get("finish").toString().equals("1")) {
                LinearLayout it = (LinearLayout)lv.getChildAt(i);
                TextView tv = (TextView)it.findViewById(R.id.state_answer);
                tv.setTextColor(getResources().getColor(R.color.mGray));
            }
        }
    }*/

    private void getData() {
        Call<UserResult> call = apiService.requestUser(CurrentUser.id);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {

                if (!response.isSuccessful()) {
                    //Utils.show(getContext(), ToastUtils.SERVER_ERROR);
                    return;
                }
                if (response.body().status != 200) {
                    //ToastUtils.show(getContext(), response.body().errmsg);
                    return;
                }
                //ToastUtils.show(getContext(), new Gson().toJson(response.body()));
                userdata = response.body().data;
                Launch = userdata.launch;
                Response = userdata.response;

                //清空列表
                userListData.clear();

                //遍历发起列表
                for(int i = 0; i < userdata.launch.size();i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    if (userdata.launch.get(i).type == 0) {
                        item.put("statetype", "我发起的，提问");
                        item.put("statename", userdata.launch.get(i).title);
                        item.put("statetime", userdata.launch.get(i).date);
                        item.put("stateanswer", userdata.launch.get(i).num+"人回答");
                        item.put("finish", "0");
                        userListData.add(item);
                    } else if (userdata.launch.get(i).type == 1) {
                        if (userdata.launch.get(i).finished == 0) {
                            item.put("statetype", "我发起的，求助");
                            item.put("statename", userdata.launch.get(i).title);
                            item.put("statetime", userdata.launch.get(i).date);
                            item.put("stateanswer", userdata.launch.get(i).num+"人响应");
                            item.put("finish", "0");
                            userListData.add(item);
                        } else if (userdata.launch.get(i).finished == 1){
                            item.put("statetype", "我发起的，求助");
                            item.put("statename", userdata.launch.get(i).title);
                            item.put("statetime", userdata.launch.get(i).date);
                            item.put("stateanswer", "求助事件已经结束");
                            item.put("finish", "1");
                            userListData.add(item);
                        }

                    } else if (userdata.launch.get(i).type == 2) {
                        if (userdata.launch.get(i).finished == 0) {
                            item.put("statetype", "我发起的，求救");
                            item.put("statename", userdata.launch.get(i).title);
                            item.put("statetime", userdata.launch.get(i).date);
                            item.put("stateanswer", "您的紧急求救信息已经发送");
                            userListData.add(item);
                            item.put("finish", "0");
                        } else if (userdata.launch.get(i).finished == 1){
                            item.put("statetype", "我发起的，求救");
                            item.put("statename", userdata.launch.get(i).title);
                            item.put("statetime", userdata.launch.get(i).date);
                            item.put("stateanswer", "求救事件已经结束");
                            userListData.add(item);
                            item.put("finish", "1");
                        }
                    }
                }
                //遍历响应列表
                for(int i = 0; i < userdata.response.size();i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    if (userdata.response.get(i).type == 0) {
                        item.put("statetype", "我响应的，提问");
                        item.put("statename", userdata.response.get(i).title);
                        item.put("statetime", "发起人:" + userdata.response.get(i).launcher_username);
                        item.put("stateanswer", userdata.response.get(i).num+"人回答");
                        userListData.add(item);
                        item.put("finish", "0");

                    } else if (userdata.response.get(i).type == 1) {
                        if (userdata.response.get(i).finished == 0) {
                            item.put("statetype", "我响应的，求助");
                            item.put("statename", userdata.response.get(i).title);
                            item.put("statetime", "发起人:" + userdata.response.get(i).launcher_username);
                            item.put("stateanswer", userdata.response.get(i).num+"人响应");
                            userListData.add(item);
                            item.put("finish", "0");

                        } else if (userdata.response.get(i).finished == 1){
                            item.put("statetype", "我响应的，求助");
                            item.put("statename", userdata.response.get(i).title);
                            item.put("statetime", "发起人:" + userdata.response.get(i).launcher_username);
                            item.put("stateanswer", "求助事件已经结束");
                            userListData.add(item);
                            item.put("finish", "1");

                        }
                    }
                }

                adapter.notifyDataSetChanged();


            }
            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                //ToastUtils.show(getContext(), t.toString());
            }
        });
    }
    /*private List<Map<String, Object>> getlistData() {
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

    }*/
}
