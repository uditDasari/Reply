package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton RegisterButton;
    TextInputLayout userEmail,userPassword;
    TextView alreadyHaveAcc;
    ImageView icError;
    TextView tvError;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialiseFields();
        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAcc();
            }
        });
    }

    private void createNewAcc() {
        String email = userEmail.getEditText().getText().toString();
        String password = userPassword.getEditText().getText().toString();
        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "email|password null", Toast.LENGTH_SHORT).show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                rootRef.child("Users").child(currentUserId).setValue("");
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(RegisterActivity.this, "Error" +task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });
        }


    }

    private void initialiseFields() {
        RegisterButton = findViewById(R.id.materialButton_createaccount);
        userEmail = findViewById(R.id.text_input_email_register);
        userPassword = findViewById(R.id.text_input_password_register);
        alreadyHaveAcc = findViewById(R.id.already_have_acc);
        icError = findViewById(R.id.ic_error);
        tvError = findViewById(R.id.tv_show_error);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_register);
        rootRef = FirebaseDatabase.getInstance().getReference();
    }
}
