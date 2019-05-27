package com.example.tidu.reply;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
 private  Toolbar mtoolbar;
private ViewPager viewPager;
private TabLayout tabLayout;
private TabAccessorAdapter tabAccessorAdapter;
private FirebaseUser currentUser;
private FirebaseAuth mAuth;
private ProgressBar progressBar;
private DatabaseReference rootRef;
private FloatingActionButton floatingActionButton;
private int tabPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mtoolbar = findViewById(R.id.main_page_tool_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Reply");
        viewPager = findViewById(R.id.main_tabs_pager);
        tabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAccessorAdapter);
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        progressBar = findViewById(R.id.main_pb);
        rootRef = FirebaseDatabase.getInstance().getReference();
        floatingActionButton = findViewById(R.id.new_group_fb);
        if(currentUser == null)
        {
            sendUserToLoginActivity();
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0)
                {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_round_person_add_24px));
                } else if (tab.getPosition() == 1)
                {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_baseline_people_24px));
                }
                else
                {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_round_dialpad_24px));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPos = tabLayout.getSelectedTabPosition();
                if(tabPos == 1)
                newGroupRequested();
                else
                {
                    Toast.makeText(MainActivity.this, "0 or 2", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void newGroupRequested() {
         MaterialAlertDialogBuilder materialAlertDialogBuilder= new MaterialAlertDialogBuilder(this,R.style.myDialogueTheme).setTitle("Enter Group Name:");
         final EditText editText = new EditText(MainActivity.this);
        //TextInputLayout textInputLayout = new TextInputLayout(this);
        //textInputLayout.chi
        materialAlertDialogBuilder.setView(editText);
          materialAlertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 String groupName = editText.getText().toString().trim();
                 if(TextUtils.isEmpty(groupName))
                 {
                     Toast.makeText(MainActivity.this, "no grp name!!", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                     createNewGroup( groupName);
                 }
             }
         });
        materialAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
            }
        });

         AlertDialog alertDialog = materialAlertDialogBuilder.show();
         Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));




    }

    private void createNewGroup(final String groupName) {

        String grpID = rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Groups").push().getKey();
        rootRef.child("Groups").child(grpID).child("Name").setValue(groupName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "create grp"+ groupName, Toast.LENGTH_SHORT).show();
                }
            }
        });

        rootRef.child("Groups").child(grpID).child("GrpImage").setValue("https://firebasestorage.googleapis.com/v0/b/reply-ecfe4.appspot.com/o/Group%20Images%2F-LfomUPbr3UlK51eYCTh.jpg?alt=media&token=1ee0e5d4-f07f-4081-a4b3-abd853fdeb40");
        rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Groups").child(grpID).child("Name").setValue(groupName);
        rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Groups").child(grpID).
                child("Image").setValue("https://firebasestorage.googleapis.com/v0/b/reply-ecfe4.appspot.com/o/Group%20Images%2F-LfomUPbr3UlK51eYCTh.jpg?alt=media&token=1ee0e5d4-f07f-4081-a4b3-abd853fdeb40");

    }


    @Override
    protected void onStart() {
        super.onStart();
        Typeface tf = Typeface.createFromAsset(getAssets(),"font/Rubik-Regular.ttf");
        mtoolbar = findViewById(R.id.main_page_tool_bar);
        TextView myTitle = (TextView) mtoolbar.getChildAt(0);
        myTitle.setTextSize(30);
        myTitle.setTypeface(tf);
        if(currentUser == null)
        {
            sendUserToLoginActivity();
        }
        else
        {
            verifyUserExistance();
        }



    }

    private void verifyUserExistance() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Name").exists())
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.options3,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         switch (item.getItemId())
         {
             case R.id.main_find_friends:
                 break;
             case R.id.settings_menu:
                 sendUserToSettingsActivity();
                 break;
             case R.id.logout_menu:
                 mAuth.signOut();
                 sendUserToLoginActivity();
                 break;
         }
         return true;
    }
    void sendUserToSettingsActivity()
    {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
}
