package com.example.tidu.reply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<SearchResult> arrayList;
    String grpName;
    String grpImage,grpId;

    public SearchAdapter(Context context, ArrayList<SearchResult> arrayList, String grpName, String grpImage, String grpId) {
        this.context = context;
        this.arrayList = arrayList;
        this.grpName = grpName;
        this.grpImage = grpImage;
        this.grpId = grpId;
    }


    public SearchAdapter() {
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.search_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        final SearchResult searchResult = arrayList.get(position);
        holder.name.setText(searchResult.getName());
        holder.status.setText(searchResult.getStatus());
        Picasso.get().load(searchResult.getImage())
                .into(holder.circleImageView);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(searchResult.getId())
                        .child("Groups").child(grpId);
                 databaseReference.child("Image").setValue(grpImage);
                 databaseReference.child("Name").setValue(grpName);
                Toast.makeText(context, searchResult.getName() +" is added !!", Toast.LENGTH_SHORT).show();
             //   Toast.makeText(context, searchResult.getName() + " is added", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView circleImageView;
        ConstraintLayout constraintLayout;
        TextView name;
        TextView status;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.search_cl);
            circleImageView = itemView.findViewById(R.id.search_dp);
            name = itemView.findViewById(R.id.tv_name_search);
            status = itemView.findViewById(R.id.tv_search_status);

        }
    }
}
