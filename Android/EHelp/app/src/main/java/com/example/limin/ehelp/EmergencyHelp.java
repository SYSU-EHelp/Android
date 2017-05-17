package com.example.limin.ehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by limin on 2017/5/17.
 */

public class EmergencyHelp extends AppCompatActivity{
    //final Button exit = (Button)findViewById(R.id.exit_help);
    private Button exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_help);

        exit = (Button)findViewById(R.id.exit_help);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyHelp.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
