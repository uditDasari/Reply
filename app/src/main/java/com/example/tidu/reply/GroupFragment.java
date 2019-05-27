package com.example.tidu.reply;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

private View groupFragmentView;
private ArrayList<String> mNames;
private ArrayList<String> mImages;
private ArrayList<String> mGrpIds;
private RecyclerView recyclerView;
private GroupFragmentAdapter groupFragmentAdapter;
private FirebaseAuth mAuth;
private DatabaseReference grpRef;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView =  inflater.inflate(R.layout.fragment_group, container, false);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null)
        {
            startActivity(new Intent(getActivity(),LoginActivity.class));
        }
        grpRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid())
                .child("Groups");
        mImages = new ArrayList<String>();
        mNames = new ArrayList<String>();
        mGrpIds = new ArrayList<>();
        retrieveAndDisplayGrps();
        initializeFields();
        return groupFragmentView;
    }

    private void retrieveAndDisplayGrps() {
        grpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNames.clear();
                mImages.clear();
                mGrpIds.clear();
                for(DataSnapshot d1 : dataSnapshot.getChildren())
                {
                    mGrpIds.add(d1.getKey().toString());
                    mNames.add(d1.child("Name").getValue().toString());
                    if(d1.child("Image").getValue()== null)
                    {
                        mImages.add("https://firebasestorage.googleapis.com/v0/b/reply-ecfe4.appspot.com/o/Group%20Images%2F-LfomUPbr3UlK51eYCTh.jpg?alt=media&token=1ee0e5d4-f07f-4081-a4b3-abd853fdeb40");
                        //mImages.add("https://firebasestorage.googleapis.com/v0/b/reply-ecfe4.appspot.com/o/Profile%20Images%2FyyCAWstr4jR9GRlkh68cko6Jzgf2.jpg?alt=media&token=07a28026-2e7c-4a5a-aa13-d8b6aede59c9");
                    }
                    else {
                        mImages.add(d1.child("Image").getValue().toString());
                    }
                    //mImages.add("https://firebasestorage.googleapis.com/v0/b/reply-ecfe4.appspot.com/o/Profile%20Images%2FyyCAWstr4jR9GRlkh68cko6Jzgf2.jpg?alt=media&token=07a28026-2e7c-4a5a-aa13-d8b6aede59c9");

                }

                groupFragmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeFields() {
        recyclerView = groupFragmentView.findViewById(R.id.rv_grp_frag);
        groupFragmentAdapter = new GroupFragmentAdapter(mNames, mImages, mGrpIds, getContext());
        recyclerView.setAdapter(groupFragmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
