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
    private TextView message;
    private String[] username = new String[] {"张三","李四"};
    private String[] phone = new String[] {"18888888888","15111111111"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        setTitle();

        lv = (ListView)findViewById(R.id.emergency);

        List<Map<String, Object>> contact = new ArrayList<>();
        for (int i = 0; i < username.length; i++) {
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
        next = (TextView) findViewById(R.id.tv_nextope);
        message = (TextView)findViewById(R.id.add_message);

        next.setText("保存");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title.setText("紧急联系人");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getText().toString();
                //这里需要将个人信息数据传入数据库
                Toast.makeText(EmergencyContact.this, "保存成功!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}


