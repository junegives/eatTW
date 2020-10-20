package com.example.eattw.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eattw.Activity.ReadPostActivity;
import com.example.eattw.Helper.TimeCaculator;
import com.example.eattw.Item.CommentInfo;
import com.example.eattw.Item.PostInfo;
import com.example.eattw.Item.UserInfo;
import com.example.eattw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<CommentInfo> mDataset;

    private FirebaseFirestore db;
    private CommentInfo commentInfo;
    private Context context;

    public CommentAdapter(ArrayList<CommentInfo> myDataset) {
        mDataset = myDataset;
        db = FirebaseFirestore.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView comment_image;
        TextView comment_username;
        TextView comment_message;
        TextView comment_timestamp;
        ImageView btn_like;
        TextView cnt_like;

        ViewHolder(View itemView) {
            super(itemView);

            comment_image = itemView.findViewById(R.id.comment_image);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_message = itemView.findViewById(R.id.comment_message);
            comment_timestamp = itemView.findViewById(R.id.comment_timestamp);
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

        View view = inflater.inflate(R.layout.comment_list_item, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, final int position) {
        commentInfo = mDataset.get(position);

        getUserData(holder.comment_image, holder.comment_username);

        holder.comment_message.setText(commentInfo.getComment());

        TimeCaculator timeCaculator = new TimeCaculator();
        holder.comment_timestamp.setText(
                timeCaculator.beforeTime(commentInfo.getTimestamp()));
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
