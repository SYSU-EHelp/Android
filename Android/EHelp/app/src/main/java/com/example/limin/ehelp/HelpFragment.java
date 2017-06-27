package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.limin.ehelp.bean.HelpBean;
import com.example.limin.ehelp.networkservice.APITestActivity;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.HelpDetailResult;
import com.example.limin.ehelp.networkservice.HelpsResult;
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

public class HelpFragment extends Fragment {

    private ListView helpeventlist;
    private SimpleAdapter adapter;
    private SwipeRefreshLayout refreshlayout;
    private static final int REFRESH_COMPLETE = 0X110;

    private List<HelpBean> helpData = new ArrayList<HelpBean>();
    private List<Map<String, Object>> helpListData = new ArrayList<Map<String, Object>>();

    // 网络访问
    private ApiService apiService;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    getData();
                    adapter.notifyDataSetChanged();
                    refreshlayout.setRefreshing(false);
                    break;
            }
        };
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.activity_helplist, container, false);

        helpeventlist = (ListView) root.findViewById(R.id.helpeventlist);
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

        adapter = new SimpleAdapter(getContext(), helpListData, R.layout.layout_helpeventitem,
                new String[]{"title", "content", "avatar", "name", "address"},
                new int[]{R.id.tv_helptitle, R.id.tv_content, R.id.avatar, R.id.tv_name, R.id.tv_address});

        helpeventlist.setAdapter(adapter);

        helpeventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), HelpDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", helpData.get(i).id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return root;
    }

    // 网络访问
    private void getData() {
        Call<HelpsResult> call = apiService.requestHelps();
        call.enqueue(new Callback<HelpsResult>() {
            @Override
            public void onResponse(Call<HelpsResult> call, Response<HelpsResult> response) {

                if (!response.isSuccessful()) {
                    //ToastUtils.show(getContext(), ToastUtils.SERVER_ERROR);
                    //Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().status != 200) {
                    //Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                    //ToastUtils.show(getContext(), response.body().errmsg);
                    return;
                }

                //ToastUtils.show(getContext(), new Gson().toJson(response.body()));
                // 获取求助事件的所有数据list

                helpData.clear();
                helpData = response.body().data;
                // Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                // 赋值至求助列表的list
                helpListData.clear();
                for (int i = 0; i < helpData.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("title", helpData.get(i).title);
                    item.put("content", helpData.get(i).description);
                    item.put("avatar", R.mipmap.avatar);
                    item.put("name", helpData.get(i).launcher_username);
                    item.put("address", helpData.get(i).address);
                    helpListData.add(item);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<HelpsResult> call, Throwable t) {
                ToastUtils.show(getContext(), t.toString());
            }
        });

    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }
}
