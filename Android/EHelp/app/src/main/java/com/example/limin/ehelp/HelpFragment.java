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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yunzhao on 2017/5/7.
 */

public class HelpFragment extends Fragment {

    private ListView helpeventlist;
    private SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_helplist, container, false);

        helpeventlist = (ListView) root.findViewById(R.id.helpeventlist);

        adapter = new SimpleAdapter(getContext(), getData(), R.layout.layout_helpeventitem,
                new String[]{"title", "content", "avatar", "name", "address"},
                new int[]{R.id.tv_title, R.id.tv_content, R.id.avatar, R.id.tv_name, R.id.tv_address});

        helpeventlist.setAdapter(adapter);

        helpeventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), HelpDetailActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    // 网络访问
    private List<Map<String, Object>> getData() {
        String[] titles = {"请问有没有人能帮忙拿个快递？", "请问有没有人能帮忙拿个快递？", "请问有没有人能帮忙拿个快递？",
                "请问有没有人能帮忙拿个快递？", "请问有没有人能帮忙拿个快递？"};
        String[] contents = {"快递到了实在找不到人帮我去拿，一个小东西，不重的，我住至九",
                             "快递到了实在找不到人帮我去拿，一个小东西，不重的，我住至九",
                             "快递到了实在找不到人帮我去拿，一个小东西，不重的，我住至九",
                             "快递到了实在找不到人帮我去拿，一个小东西，不重的，我住至九",
                             "快递到了实在找不到人帮我去拿，一个小东西，不重的，我住至九"};
        int[] avatars = {R.mipmap.avatar, R.mipmap.avatar, R.mipmap.avatar, R.mipmap.avatar, R.mipmap.avatar};
        String[] names = {"张三", "李四", "王五", "张三", "李四"};
        String[] addresses = {"广州大学城中山大学明德园6号", "广州大学城中山大学明德园6号", "广州大学城中山大学明德园6号",
                "广州大学城中山大学明德园6号", "广州大学城中山大学明德园6号"};

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < avatars.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("title", titles[i]);
            item.put("content", contents[i]);
            item.put("avatar", avatars[i]);
            item.put("name", names[i]);
            item.put("address", addresses[i]);
            list.add(item);
        }

        return list;
    }
}
