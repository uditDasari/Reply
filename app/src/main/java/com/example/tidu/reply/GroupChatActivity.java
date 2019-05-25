package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessageInput;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class GroupChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    String grpName,userId,userName,currentDate,currentTime,msgKey;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,grpNameRef,grpMsgKeyRef;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    MaterialButton sendBtn;
    TextView messageInput;
    MsgAdapter msgAdapter;
    ArrayList<FbGrpMsg> arrayList;
    MediaPlayer mediaPlayer;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        init();
        grpName = getIntent().getExtras().get("GroupName").toString();
        grpNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(grpName);
        getUserInfoo();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FbGrpMsg msg = new FbGrpMsg();
                String input = messageInput.getText().toString();
                if(input.trim().length() != 0) {
                    msg.setMessage(input.toString());
                    msg.setId(userId);
                    msg.setName(userName);
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    String time = timeFormat.format(Calendar.getInstance().getTime());
                    msg.setDate(date);
                    msg.setTime(time);
                    int s = arrayList.size();
                    String key = grpNameRef.push().getKey();
                    grpNameRef.child(key).setValue(msg);
                    messageInput.setText("");
                    if(mediaPlayer == null)
                    {
                        mediaPlayer = MediaPlayer.create(GroupChatActivity.this,R.raw.reply_new_message);
                    }
                    mediaPlayer.start();
                }
            }
        });
        grpNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    FbGrpMsg message = ds.getValue(FbGrpMsg.class);
                    arrayList.add(message);

                }
                msgAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(msgAdapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getUserInfoo() {

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    userName = dataSnapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageInput = findViewById(R.id.input);
        arrayList = new ArrayList<>();
        msgAdapter = new MsgAdapter(this,arrayList);
        recyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(msgAdapter);
        timeFormat = new SimpleDateFormat("h:mm a");
        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        sendBtn = findViewById(R.id.sendIcon);

    }
}
