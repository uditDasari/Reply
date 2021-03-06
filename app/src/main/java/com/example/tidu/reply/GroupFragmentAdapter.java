package com.example.tidu.reply;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupFragmentAdapter extends RecyclerView.Adapter<GroupFragmentAdapter.GrpFragViewHolder> {
    private static final String TAG = "GroupFragmentAdapter";
    private ArrayList<String> grpNames = new ArrayList<String>();
    private ArrayList<String> grpImages = new ArrayList<String>();
    private ArrayList<String> grpIds = new ArrayList<>();
    private Context mContext;

    public GroupFragmentAdapter(ArrayList<String> grpNames, ArrayList<String> grpImages,ArrayList<String> grpIds, Context mContext) {
        this.grpNames = grpNames;
        this.grpImages = grpImages;
        this.mContext = mContext;
        this.grpIds = grpIds;
    }


    @NonNull
    @Override
    public GroupFragmentAdapter.GrpFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_grp_frag,parent,false);
        return  new GrpFragViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupFragmentAdapter.GrpFragViewHolder holder, final int position) {

        holder.textView.setText(grpNames.get(position));
        Log.d(TAG, "onBindViewHolder: "+ grpImages.get(position));
            Glide.with(mContext)
                    .asBitmap()
                    .load(grpImages.get(position))
                    .into(holder.circleImageView);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grpName = grpNames.get(position);
                String grpId = grpIds.get(position);
                Intent intent = new Intent(mContext,GroupChatActivity.class);
                intent.putExtra("GroupName",grpName);
                intent.putExtra("GroupID",grpId);
                intent.putExtra("GrpImage",grpImages.get(position));
                mContext.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return grpImages.size();
    }

    public class GrpFragViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView circleImageView;
        TextView textView;
        RelativeLayout relativeLayout;


        public GrpFragViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.grp_image);
            textView = itemView.findViewById(R.id.grp_name);
            relativeLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
