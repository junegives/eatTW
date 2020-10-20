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
import com.example.eattw.Item.PostInfo;
import com.example.eattw.Item.UserInfo;
import com.example.eattw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private ArrayList<PostInfo> mDataset;
    private Fragment Fcontext;

    private FirebaseFirestore db;
    private UserInfo userInfo;
    private PostInfo postInfo;

    public CommunityAdapter(ArrayList<PostInfo> myDataset, Fragment context){
        //StrictMode.enableDefaults();
        mDataset = myDataset;
        Fcontext = context;
        db = FirebaseFirestore.getInstance();
    }

//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    OnItemClickListener mListener;
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        mListener = listener;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_content;
        ImageView iv_image;
        TextView tv_comment;
        TextView tv_timestamp;
        TextView tv_nickname;
        View like_icon;
        TextView like_count;

        ViewHolder(View itemView){
            super(itemView);

            Log.d("context","test.ViewHolder");

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            like_icon = itemView.findViewById(R.id.like_icon);
            like_count = itemView.findViewById(R.id.like_count);
        }
    }

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        Log.d("context","test.AddMemoAdapter.ViewHolder");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_community, parent, false) ;
        final CommunityAdapter.ViewHolder vh = new CommunityAdapter.ViewHolder(view) ;
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadPostActivity.class);
                intent.putExtra("postID", mDataset.get(vh.getAdapterPosition()).getPostID());
                context.startActivity(intent);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommunityAdapter.ViewHolder holder, final int position) {
        //final PostInfo postInfo = mDataset.get(position) ;
        postInfo = mDataset.get(position);
        Log.d("context","test.onBindViewHolder");

        getUserData(holder.tv_nickname);
        getCommentsCount(holder.tv_comment);

        holder.tv_title.setText(postInfo.getTitle());
        holder.tv_content.setText(postInfo.getContent());
        if(postInfo.getImageList().size() == 0){
            Log.d("postInfo.getImg()", "null");
            holder.iv_image.setVisibility(View.GONE);
        }
        else{
            holder.iv_image.setVisibility(View.VISIBLE);
            Log.d("postgetImg", postInfo.getImageList().get(0));
            //holder.iv_image.setImageURI(Uri.parse(postInfo.getImageList().get(0)));
            Glide.with(Fcontext)
                    .load(Uri.parse(postInfo.getImageList().get(0)))
                    .override(300, 300)
                    .thumbnail(0.1f)
                    .into(holder.iv_image);
            }

        holder.tv_comment.setText(String.valueOf(postInfo.getComments()));

        TimeCaculator timeCaculator = new TimeCaculator();
        holder.tv_timestamp.setText(
                timeCaculator.beforeTime(postInfo.getTimestamp()));

        holder.like_icon.setBackgroundResource(R.drawable.like_full);

        holder.like_count.setText(String.valueOf(postInfo.getLike()));

//        holder.itemView.setTag(position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mListener != null){
//                    mListener.onItemClick(holder.itemView, position);
//                }
//            }
//        });
    }

    public void getCommentsCount(final TextView tv_comment){

        db.collection("Posts/" + postInfo.getPostID() + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try{
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        tv_comment.setText(String.valueOf(count));
                    } else {
                        tv_comment.setText("0");
                    }
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });
    }

    public void getUserData(final TextView tv_nickname){
        Log.d("getDataTest", "getUserData()");

        db.collection("Users").document(postInfo.getUserID()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("errorTAG", task.getResult().toString());
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Log.d("errorTAG", "5");
                                tv_nickname.setText(document.getData().get("nickname").toString());
                            }
                            else{
                                tv_nickname.setText("존재하지 않는 회원");
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
