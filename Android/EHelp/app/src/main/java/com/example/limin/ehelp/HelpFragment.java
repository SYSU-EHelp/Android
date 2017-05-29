package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private List<HelpBean> helpData = new ArrayList<HelpBean>();
    private List<Map<String, Object>> helpListData = new ArrayList<Map<String, Object>>();

    // 网络访问
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_helplist, container, false);

        helpeventlist = (ListView) root.findViewById(R.id.helpeventlist);
        apiService = ApiService.retrofit.create(ApiService.class);
        getData();

        adapter = new SimpleAdapter(getContext(), helpListData, R.layout.layout_helpeventitem,
                new String[]{"title", "content", "avatar", "name", "address"},
                new int[]{R.id.tv_title, R.id.tv_content, R.id.avatar, R.id.tv_name, R.id.tv_address});

        helpeventlist.setAdapter(adapter);

        helpeventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), HelpDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", helpData.get(i).id);
                bundle.putString("title", helpData.get(i).title);
                bundle.putString("description", helpData.get(i).description);
                bundle.putString("address", helpData.get(i).address);
                bundle.putString("date", helpData.get(i).date);
                bundle.putDouble("longitude", helpData.get(i).longitude);
                bundle.putDouble("latitude", helpData.get(i).latitude);
                bundle.putString("launcher_username", helpData.get(i).launcher_username);
                bundle.putString("launcher_avatar", helpData.get(i).launcher_avatar);
                bundle.putString("phone", helpData.get(i).phone);
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
                    Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().status != 200) {
                    Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
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
