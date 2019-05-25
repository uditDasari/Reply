package com.example.tidu.reply;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MsgAdapter";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Context context;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<FbGrpMsg> msgList;
    public MsgAdapter() {
    }

    public MsgAdapter(Context context, ArrayList<FbGrpMsg> msgList) {
        this.context = context;
        this.msgList = msgList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == R.layout.right_msg)
        {
            return new RightViewHolder(LayoutInflater.from(context).inflate(R.layout.right_msg,parent,false));
        }
        else
        {
            return new LeftViewHolder(LayoutInflater.from(context).inflate(R.layout.left_msg,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final FbGrpMsg fbGrpMsg = msgList.get(position);
        String id = fbGrpMsg.getId();
        rootRef.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("Image").getValue().toString();
                Log.d(TAG, "onBindViewHolder: " + imageUrl);
                ////////
                if(holder instanceof LeftViewHolder)
                {
                    ((LeftViewHolder) holder).lname.setText(fbGrpMsg.name);
                    ((LeftViewHolder) holder).ldate.setText(fbGrpMsg.date);
                    ((LeftViewHolder) holder).ltime.setText(fbGrpMsg.time);
                    ((LeftViewHolder) holder).lmessage.setText(fbGrpMsg.message);
                    Picasso.get().load(imageUrl).into(((LeftViewHolder) holder).leftImage);
                }
                else if(holder instanceof RightViewHolder)
                {
                    ((RightViewHolder) holder).rname.setText(fbGrpMsg.name);
                    ((RightViewHolder) holder).rdate.setText(fbGrpMsg.date);
                    ((RightViewHolder) holder).rtime.setText(fbGrpMsg.time);
                    ((RightViewHolder) holder).rmessage.setText(fbGrpMsg.message);
                    //Picasso.get().load(imageUrl).into(((RightViewHolder) holder).rightImage);
                    Glide.with(context).asBitmap().load(imageUrl).into(((RightViewHolder) holder).rightImage);
                }
                //////////
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //String imageUrl = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Image").
                //"https://i.pinimg.com/564x/6b/46/1a/6b461a006f4e718ee0e406550ca88f33.jpg";
       // "https://firebasestorage.googleapis.com/v0/b/reply-ecfe4.appspot.com/o/Profile%20Images%2FyyCAWstr4jR9GRlkh68cko6Jzgf2.jpg?alt=media&token=3f2d17c7-b79b-4e5f-9dbe-59bce2f720d6";



    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String id = firebaseAuth.getCurrentUser().getUid();
        Log.d(TAG, "id  : " + id + " ");
        Log.d(TAG, "id from server: " + msgList.get(position).getId() + " \n");
        if(id.equals(msgList.get(position).getId()))
            return R.layout.right_msg;
        else
            return R.layout.left_msg;
    }
    public class LeftViewHolder extends RecyclerView.ViewHolder
    {
        TextView lname,ldate,lmessage,ltime;
        CircleImageView leftImage;
        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            ldate = itemView.findViewById(R.id.l_date);
            lmessage = itemView.findViewById(R.id.l_msg);
            lname = itemView.findViewById(R.id.l_name);
            ltime = itemView.findViewById(R.id.l_time);
            leftImage = itemView.findViewById(R.id.avatar_left);

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder

    {
        TextView rname,rdate,rmessage,rtime;
        CircleImageView rightImage;
        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            rdate = itemView.findViewById(R.id.r_date);
            rmessage = itemView.findViewById(R.id.r_msg);
            rname = itemView.findViewById(R.id.r_name);
            rtime = itemView.findViewById(R.id.r_time);
            rightImage = itemView.findViewById(R.id.avatar_right);
        }
    }

}
