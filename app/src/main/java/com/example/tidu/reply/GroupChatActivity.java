package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.messages.MessageInput;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    String grpName,grpId,userId,userName,currentDate,currentTime,msgKey;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,grpNameRef,grpImageRef,userGrpImageRef;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    MaterialButton sendBtn;
    TextView messageInput;
    MsgAdapter msgAdapter;
    ProgressBar progressBar;
    ArrayList<FbGrpMsg> arrayList;
    MediaPlayer mediaPlayer;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private static final int galleryPick = 1;
    private StorageReference grpImageStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        init();
        grpName = getIntent().getExtras().get("GroupName").toString();
        grpId = getIntent().getExtras().get("GroupID").toString();
        grpNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(grpId).child("Messages");
        grpImageStorageRef = FirebaseStorage.getInstance().getReference().child("Group Images");
        grpImageRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(grpId).child("GrpImage");
        userGrpImageRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid())
                .child("Groups").child(grpId).child("Image");
        getUserInfoo();
        getSupportActionBar().setTitle(grpName);
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
        progressBar = findViewById(R.id.progress_bar_grp_chat_activity);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.grp_features,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.add_members :
                addMemebers();
                break;
            case R.id.change_dp:
                changeGrpDP();
                break;

        }
        return true;
    }

    private void changeGrpDP() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    private void addMemebers() {

    }
    //////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                Uri resultUri = result.getUri();
                final StorageReference filePath = grpImageStorageRef.child(grpId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(GroupChatActivity.this, "image uploaded", Toast.LENGTH_SHORT).show();

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadedUrl = uri.toString();
                                    grpImageRef.setValue(downloadedUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(GroupChatActivity.this, "image saved in database", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(GroupChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    userGrpImageRef.setValue(downloadedUrl);
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(GroupChatActivity.this, "img not uploaded  " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


}
