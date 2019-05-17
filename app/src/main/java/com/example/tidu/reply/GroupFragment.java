package com.example.tidu.reply;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

private View groupFragmentView;
private ArrayList<String> mNames;
private ArrayList<String> mImages;
private RecyclerView recyclerView;
private GroupFragmentAdapter groupFragmentAdapter;
private DatabaseReference grpRef;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView =  inflater.inflate(R.layout.fragment_group, container, false);
        grpRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        mImages = new ArrayList<String>();
        mNames = new ArrayList<String>();
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
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext())
                {
                    mNames.add(((DataSnapshot)iterator.next()).getKey());
                }
                int size = mNames.size();
                while(size != 0)
                {
                    mImages.add("https://i.pinimg.com/564x/6b/46/1a/6b461a006f4e718ee0e406550ca88f33.jpg");
                    size--;
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
        groupFragmentAdapter = new GroupFragmentAdapter(mNames,mImages,getContext());
        recyclerView.setAdapter(groupFragmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
