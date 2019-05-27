package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private MaterialButton updateAccountSettings;
    TextInputLayout username, userstatus;
    private CircleImageView circleImageView;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference rootRef;
    private static final int galleryPick = 1;
    private StorageReference UserProfileImagesRef;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        retrieveUserInfo();
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = username.getEditText().getText().toString().trim();
                String status = userstatus.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(status)) {
                    Toast.makeText(SettingsActivity.this, "fields are empty", Toast.LENGTH_SHORT).show();
                    rootRef.child("Users").child(currentUserId).child("Name").setValue("name");
                    rootRef.child("Users").child(currentUserId).child("Status").setValue("status");
                } else {
                    rootRef.child("Users").child(currentUserId).child("Name").setValue(name);
                    rootRef.child("Users").child(currentUserId).child("Status").setValue(status);
                    Toast.makeText(SettingsActivity.this, "success", Toast.LENGTH_SHORT).show();


                }
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);

            }
        });

    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name"))&& (dataSnapshot.hasChild("Status")) && (dataSnapshot.hasChild("Image"))) {
                    String retreivedUserName = dataSnapshot.child("Name").getValue().toString();
                    String retreivedUserStatus = dataSnapshot.child("Status").getValue().toString();
                    String retreivedUserImage = dataSnapshot.child("Image").getValue().toString();
                    username.getEditText().setText(retreivedUserName);
                    userstatus.getEditText().setText(retreivedUserStatus);
                    Picasso.get().load(retreivedUserImage).into(circleImageView);
                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name"))&& (dataSnapshot.hasChild("Status"))) {
                    String retreivedUserName = dataSnapshot.child("Name").getValue().toString();
                    String retreivedUserStatus = dataSnapshot.child("Status").getValue().toString();
                    username.getEditText().setText(retreivedUserName);
                    userstatus.getEditText().setText(retreivedUserStatus);

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
        String name = username.getEditText().getText().toString().trim();
        String status = userstatus.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(status)) {
            Toast.makeText(this, "fields are empty", Toast.LENGTH_SHORT).show();
            rootRef.child("Users").child(currentUserId).child("Name").setValue("name");
            rootRef.child("Users").child(currentUserId).child("Status").setValue("status");
        } else {
            rootRef.child("Users").child(currentUserId).child("Name").setValue(name);
            rootRef.child("Users").child(currentUserId).child("Status").setValue(status);
            sendUserToMainActivity();
            Toast.makeText(SettingsActivity.this, "success", Toast.LENGTH_SHORT).show();


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
        progressBar = findViewById(R.id.progress_bar_sett);

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

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
                final StorageReference filePath = UserProfileImagesRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SettingsActivity.this, "image uploaded", Toast.LENGTH_SHORT).show();

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadedUrl = uri.toString();
                                    rootRef.child("Users").child(currentUserId).child("Image").setValue(downloadedUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SettingsActivity.this, "image saved in database", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SettingsActivity.this, "img not uploaded  " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


}
