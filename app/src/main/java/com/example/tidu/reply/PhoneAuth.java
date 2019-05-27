package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    MaterialButton send;
    MaterialButton verify;
    TextInputLayout phn;
    TextInputLayout code;
    private FirebaseAuth mAuth;
    private static final String TAG = "PhoneAuth";
    ProgressBar progressBar;
    private String mVerificationId;
    private String phone;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        init();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verCode = code.getEditText().getText().toString().trim();
                if(!TextUtils.isEmpty(verCode))
                {
                   progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(phn.getEditText().getText().toString().trim()))
                {
                    phone = phn.getEditText().getText().toString();
                    progressBar.setVisibility(View.VISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91"+phn.getEditText().getText().toString().trim(),         // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneAuth.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PhoneAuth.this, e.toString(), Toast.LENGTH_SHORT).show();
                send.setVisibility(View.VISIBLE);
                phn.setVisibility(View.VISIBLE);
                verify.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE);
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneAuth.this, "Code sent", Toast.LENGTH_SHORT).show();
                send.setVisibility(View.GONE);
                phn.setVisibility(View.GONE);
                verify.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);

            }
        };
    }

    private void init() {
        send = findViewById(R.id.btn_send);
        verify = findViewById(R.id.btn_verify);
        phn = findViewById(R.id.text_input_phn);
        code = findViewById(R.id.text_input_phn_code);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid())
                                    .child("Phone").setValue(phone);
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(PhoneAuth.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(PhoneAuth.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
