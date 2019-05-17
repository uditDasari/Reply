package com.example.tidu.reply;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

public class GroupChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    String grpName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        grpName = getIntent().getExtras().get("GroupName").toString();
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.grp_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(grpName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
