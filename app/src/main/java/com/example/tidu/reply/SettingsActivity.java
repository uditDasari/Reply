package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    EditText username, userstatus;
    private CircleImageView circleImageView;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
        retrieveUserInfo();
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name")) && (dataSnapshot.hasChild("Image"))) {
                    String retreivedUserName = dataSnapshot.child("Name").getValue().toString();
                    String retreivedUserStatus = dataSnapshot.child("Status").getValue().toString();
                    String retreivedUserImage = dataSnapshot.child("Image").getValue().toString();
                    username.setText(retreivedUserName);
                    userstatus.setText(retreivedUserStatus);
                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name"))) {
                    String retreivedUserName = dataSnapshot.child("Name").getValue().toString();
                    String retreivedUserStatus = dataSnapshot.child("Status").getValue().toString();
                    username.setText(retreivedUserName);
                    userstatus.setText(retreivedUserStatus);

                } else {
                    Toast.makeText(SettingsActivity.this, "update ur profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSettings() {
        String name = username.getText().toString().trim();
        String status = userstatus.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(status)) {
            Toast.makeText(this, "fields are empty", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<String, String>();
            profileMap.put("Uid", currentUserId);
            profileMap.put("Name", name);
            profileMap.put("Status", status);
            rootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this, "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
    }

    private void initialize() {
        updateAccountSettings = findViewById(R.id.update_settings);
        username = findViewById(R.id.set_user_name);
        userstatus = findViewById(R.id.set_profile_status);
        circleImageView = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
