package com.example.eattw.Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eattw.Helper.TimeCaculator;
import com.example.eattw.Item.CommentInfo;
import com.example.eattw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Color;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<CommentInfo> mDataset;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private CommentInfo commentInfo;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick_mycomment(int position);
        void onItemClick_recomment(View view, int position, int depth, String userID, Date bundleID);
    }

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CommentAdapter(ArrayList<CommentInfo> myDataset) {
        mDataset = myDataset;
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView comment_image;
        TextView comment_username;
        TextView comment_message;
        TextView comment_timestamp;
        ImageView btn_like;
        TextView cnt_like;
        ImageButton btn_mycomment;
        TextView btn_recomment;

        ViewHolder(View itemView) {
            super(itemView);

            comment_image = itemView.findViewById(R.id.comment_image);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_message = itemView.findViewById(R.id.comment_message);
            comment_timestamp = itemView.findViewById(R.id.comment_timestamp);
            btn_mycomment = itemView.findViewById(R.id.btn_mycomment);
            btn_recomment = itemView.findViewById(R.id.btn_recomment);
            btn_like = itemView.findViewById(R.id.btn_like);
            cnt_like = itemView.findViewById(R.id.cnt_like);
        }
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        context = parent.getContext();
        Log.d("context", "test.AddMemoAdapter.ViewHolder");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_comment, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, final int position) {
        commentInfo = mDataset.get(position);

        Log.d("UserNamememe", "commentInfo 충전");

        holder.btn_mycomment.setVisibility(View.GONE);

        TimeCaculator timeCaculator = new TimeCaculator();
        holder.comment_timestamp.setText(
                timeCaculator.beforeTime(commentInfo.getTimestamp()));

        if(commentInfo.isDeleted()){
            holder.btn_recomment.setVisibility(View.INVISIBLE);
            if(commentInfo.getDepth() != 0){
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                mLayoutParams.leftMargin = 100;
                holder.itemView.setLayoutParams(mLayoutParams);
                holder.itemView.setBackgroundResource(android.R.color.white);
            }
            else{
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                mLayoutParams.leftMargin = 0;
                holder.itemView.setLayoutParams(mLayoutParams);
                holder.itemView.setBackgroundResource(R.drawable.comment_list);
            }
            holder.comment_message.setText("(삭제한 댓글입니다)");
            holder.comment_username.setText("삭제한 댓글");
            holder.comment_image.setColorFilter(R.color.colorPrimaryDark);
        }

        else {

            getUserData(holder.comment_image, holder.comment_username);

            if (commentInfo.getDepth() != 0) {
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                mLayoutParams.leftMargin = 100;
                holder.itemView.setLayoutParams(mLayoutParams);
                holder.itemView.setBackgroundResource(android.R.color.white);
                getUsername(holder.comment_message, commentInfo);
            } else {
                RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                mLayoutParams.leftMargin = 0;
                holder.itemView.setLayoutParams(mLayoutParams);
                holder.itemView.setBackgroundResource(R.drawable.comment_list);
                Log.d("UserNamememe", commentInfo.getComment());
                holder.comment_message.setText(commentInfo.getComment());
            }

            if (user.getUid().equals(commentInfo.getUserID())) {
                holder.btn_mycomment.setVisibility(View.VISIBLE);
            }

            holder.btn_mycomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        mListener.onItemClick_mycomment(position);
                    }
                }
            });

            holder.btn_recomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        Log.d("Commentetete", "버튼 눌렀당 눌린 사람이 쓴 댓글은 "+commentInfo.getComment() + "\n눌린 position은" + position);
                        mListener.onItemClick_recomment(holder.itemView, position, mDataset.get(position).getDepth(), mDataset.get(position).getUserID(), mDataset.get(position).getBundleID());
                    }
                }
            });
        }
    }

    public void getUsername(final TextView comment_messeage, final CommentInfo com){
        db.collection("Users").document(com.getReply_userID()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Log.d("UserNamememe" , document.getData().get("nickname").toString());
                                comment_messeage.setText(document.getData().get("nickname").toString() + "님에게\n" + com.getComment());
                                Log.d("UserNamememe", com.getComment());
                            }
                            else{
                                comment_messeage.setText("존재하지 않는 회원\t" + com.getComment());
                            }
                        }
                    }
                });
    }

    public void getUserData(final CircleImageView comment_image, final TextView comment_username) {
        Log.d("getDataTest", "getUserData()");

        db.collection("Users").document(commentInfo.getUserID()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("errorTAG", task.getResult().toString());
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("errorTAG", "5");

                                if (document.getData().get("photoUrl").toString().length() > 0) {
                                    Glide.with(context)
                                            .load(Uri.parse(document.getData().get("photoUrl").toString()))
                                            .override(300, 300)
                                            .thumbnail(0.1f)
                                            .into(comment_image);
                                } else {
                                    Glide.with(context)
                                            .load(R.drawable.mandoo_profile)
                                            .override(100, 100)
                                            .thumbnail(0.1f)
                                            .into(comment_image);
                                }
                                comment_username.setText(document.getData().get("nickname").toString());
                            } else {
                                comment_username.setText("존재하지 않는 회원");
                            }
                        } else {
                            Log.d("errorTAG", "6");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
