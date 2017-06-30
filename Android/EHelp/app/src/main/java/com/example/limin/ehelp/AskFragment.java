package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limin.ehelp.bean.HelpBean;
import com.example.limin.ehelp.bean.QuestionBean;
import com.example.limin.ehelp.networkservice.ApiService;
import com.example.limin.ehelp.networkservice.HelpsResult;
import com.example.limin.ehelp.networkservice.QuestionsResult;
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

public class AskFragment extends Fragment {
    private ListView lv;
    private TextView anwserquestion;
    private SwipeRefreshLayout refreshlayout;
    private static final int REFRESH_COMPLETE = 0X110;
    private SimpleAdapter adapter;
    private List<QuestionBean> questionData = new ArrayList<QuestionBean>();
    private List<Map<String, Object>> questionListData = new ArrayList<Map<String, Object>>();

    // 网络访问
    private ApiService apiService;

    // 下拉刷新
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
        View root = inflater.inflate(R.layout.activity_questionslist, container, false);

        lv = (ListView)root.findViewById(R.id.questionslist);
        anwserquestion = (TextView)root.findViewById(R.id.anwserquestion);

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

        getData();
        adapter = new SimpleAdapter(getContext(),questionListData,R.layout.layout_questionitem,
                new String[] {"questiontitle", "questioncontent", "questionname", "anwserdate","anwserquestion"},
                new int[] {R.id.questiontitle, R.id.questioncontent, R.id.questionname, R.id.anwserdate, R.id.anwserquestion});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), QuestionDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", questionData.get(i).id);
                bundle.putString("questiontitle", questionData.get(i).title);
                bundle.putString("questioncontent", questionData.get(i).description);
                bundle.putString("questionname", questionData.get(i).asker_username);
                bundle.putString("anwserdate", questionData.get(i).date);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return root;
    }

//    private List<Map<String, Object>> getData() {
//        String[] questiontitle = new String[] {"请问中大的校车要怎么买票？","请问中大的校车要怎么买票？","请问中大的校车要怎么买票？","请问中大的校车要怎么买票？","请问中大的校车要怎么买票？"};
//        String[] questioncontent = new String[] {"我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛","我是中大南校区的学生，今天要来东校参加一个定向越野比赛"};
//        String[] questionname = new String[] {"张三","张三","张三","张三","张三"};
//        String[] anwserdate = new String[] {"2人回答","2人回答","2人回答","2人回答","2人回答"};
//        String[] anwserquestion = new String[] {"去回答","去回答","去回答","去回答","去回答"};
//
//        List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
//        for (int i = 0;i<questiontitle.length;i++) {
//            Map<String, Object> temp = new HashMap<>();
//            temp.put("questiontitle",questiontitle[i]);
//            temp.put("questioncontent",questioncontent[i]);
//            temp.put("questionname", questionname[i]);
//            temp.put("anwserdate",anwserdate[i]);
//            temp.put("anwserquestion",anwserquestion[i]);
//            ls.add(temp);
//        }
//        return ls;
//    }

    // 网络访问
    private void getData() {
        Call<QuestionsResult> call = apiService.requestQuestions();
        call.enqueue(new Callback<QuestionsResult>() {
            @Override
            public void onResponse(Call<QuestionsResult> call, Response<QuestionsResult> response) {

                if (!response.isSuccessful()) {
//                    Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().status != 200) {
//                    Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                    return;
                }

//                ToastUtils.show(getContext(), new Gson().toJson(response.body()));
                // 获取问题列表的所有数据list

                questionData.clear();
                questionData = response.body().data;
                // Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                // 赋值至求助列表的list
                questionListData.clear();
                for (int i = 0; i < questionData.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("questiontitle", questionData.get(i).title);
                    item.put("questioncontent", questionData.get(i).description);
                    item.put("questionname", questionData.get(i).asker_username);
                    item.put("anwserdate", questionData.get(i).answer_num + "人回答");
                    item.put("anwserquestion", "查看");
                    questionListData.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<QuestionsResult> call, Throwable t) {
//                ToastUtils.show(getContext(), t.toString());
            }
        });

    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

}
