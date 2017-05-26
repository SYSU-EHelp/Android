package com.example.limin.ehelp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yunzhao on 2017/5/7.
 */

public class StateFragment extends Fragment {
    private ListView lv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_statelist, container, false);

        lv = (ListView)root.findViewById(R.id.statelist);
        SimpleAdapter adapter = new SimpleAdapter(getContext(),getData(),R.layout.layout_stateitem,
                new String[] {"statename", "statetype", "statetime", "stateanswer"}, new int[] {R.id.state_name,
                R.id.state_type, R.id.state_time, R.id.state_answer});
        lv.setAdapter(adapter);
        return root;
    }

    private List<Map<String, Object>> getData() {
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
