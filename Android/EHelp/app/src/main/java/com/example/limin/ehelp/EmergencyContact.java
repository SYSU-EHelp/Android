package com.example.limin.ehelp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmergencyContact extends AppCompatActivity {
    private ListView lv;
    private Button btn_back;
    private TextView title;
    private TextView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        lv = (ListView)findViewById(R.id.emergency);

        List<Map<String, Object>> contact = new ArrayList<>();
        String[] username = new String[] {"张三","李四"};
        String[] phone = new String[] {"18888888888","15111111111"};
        for (int i = 0; i < 3; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("username",username[i]);
            temp.put("phone", phone[i]);
            contact.add(temp);

        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, contact, R.layout.contact_item,
                new String[] {"username", "phone"}, new int[] {R.id.username, R.id.phone});
        lv.setAdapter(simpleAdapter);
    }

    private void setTitle() {
        btn_back = (Button) findViewById(R.id.btn_back);
        title = (TextView) findViewById(R.id.tv_title);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title.setText("紧急联系人");

    }
}


