package com.example.tidu.reply;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
Toolbar toolbar;
ArrayList<SearchResult> arrayList;
SearchAdapter searchAdapter;
RecyclerView recyclerView;
String grpName,grpImage,grpId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.search_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Members");
        grpName = getIntent().getExtras().get("GrpName").toString();
        grpId = getIntent().getExtras().get("GrpId").toString();
        grpImage = getIntent().getExtras().get("GrpImage").toString();
        recyclerView = findViewById(R.id.recycler_view_search);
        arrayList = new ArrayList<SearchResult>();
        searchAdapter = new SearchAdapter(this,arrayList,grpName,grpImage,grpId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_view,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(SearchActivity.this, newText, Toast.LENGTH_SHORT).show();
                Log.d("ToastUdit : ", newText);
                searchIt(newText);
                return false;
            }
        });
        return true;
    }

    private void searchIt(final String number) {
        arrayList.clear();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d1 : dataSnapshot.getChildren())
                        {
                            //if(!d1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                           // {
                            String phone = d1.child("Phone").getValue().toString();
                            if(phone.startsWith(number)) {
                                SearchResult searchResult = new SearchResult(d1.child("Name").getValue().toString()
                                        , d1.child("Status").getValue().toString()
                                        , d1.child("Image").getValue().toString(),d1.getKey().toString());
                                arrayList.add(searchResult);

                            }
                           // }
                            searchAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}
