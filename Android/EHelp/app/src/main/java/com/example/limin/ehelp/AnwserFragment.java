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

public class AnwserFragment extends Fragment {
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_questiondetail, container, false);

        lv = (ListView)root.findViewById(R.id.anwserslist);
        SimpleAdapter adapter = new SimpleAdapter(getContext(),getData(),R.layout.layout_questionitem,
                new String[] {"anwsername", "anwsertime", "anwsercontent"}, new int[] {R.id.anwsername,
                R.id.anwsertime, R.id.anwsercontent});
        lv.setAdapter(adapter);
        return root;
    }

    private List<Map<String, Object>> getData() {
        String[] anwsername = new String[] {"张三","张三","张三","张三","张三"};
        String[] anwsertime = new String[] {"3分钟前","3分钟前","3分钟前","3分钟前","3分钟前"};
        String[] anwsercontent = new String[] {"一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金","一般都是刷校卡就行，有些校车要用现金"};

        List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
        for (int i = 0;i<anwsername.length;i++) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("anwsername",anwsername[i]);
            temp.put("anwsertime",anwsertime[i]);
            temp.put("anwsercontent", anwsercontent[i]);
            ls.add(temp);
        }
        return ls;

    }
}
