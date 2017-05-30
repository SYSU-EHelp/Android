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

public class AskFragment extends Fragment {
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_questionslist, container, false);

        lv = (ListView)root.findViewById(R.id.questionslist);
        SimpleAdapter adapter = new SimpleAdapter(getContext(),getData(),R.layout.layout_questionitem,
                new String[] {"questiontitle", "questioncontent", "questionname", "anwsercount","anwserquestion"}, new int[] {R.id.questiontitle,
                R.id.questioncontent, R.id.questionname, R.id.anwsercount, R.id.anwserquestion});
        lv.setAdapter(adapter);
        return root;
    }

    private List<Map<String, Object>> getData() {
        String[] questiontitle = new String[] {"请问中大的校车要怎么买票？","请问中大的校车要怎么买票？","请问中大的校车要怎么买票？","请问中大的校车要怎么买票？","请问中大的校车要怎么买票？"};
        String[] questioncontent = new String[] {"我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛"};
        String[] questionname = new String[] {"张三","张三","张三","张三","张三"};
        String[] anwsercount = new String[] {"2人回答","2人回答","2人回答","2人回答","2人回答"};
        String[] anwserquestion = new String[] {"去回答","去回答","去回答","去回答","去回答"};

        List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
        for (int i = 0;i<questiontitle.length;i++) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("questiontitle",questiontitle[i]);
            temp.put("questioncontent",questioncontent[i]);
            temp.put("questionname", questionname[i]);
            temp.put("anwsercount",anwsercount[i]);
            temp.put("anwserquestion",anwserquestion[i]);
            ls.add(temp);
        }
        return ls;

    }
}
