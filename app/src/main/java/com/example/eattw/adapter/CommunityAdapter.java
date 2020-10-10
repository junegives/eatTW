package com.example.eattw.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eattw.ImgInfo;
import com.example.eattw.PostInfo;
import com.example.eattw.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private ArrayList<PostInfo> mDataset;

    public CommunityAdapter(ArrayList<PostInfo> myDataset){
        StrictMode.enableDefaults();
        mDataset = myDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_content;
        ImageView iv_image;

        ViewHolder(View itemView){
            super(itemView);

            Log.d("context","test.ViewHolder");

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_image = itemView.findViewById(R.id.iv_image);
        }
    }

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        Log.d("context","test.AddMemoAdapter.ViewHolder");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_community, parent, false) ;
        CommunityAdapter.ViewHolder vh = new CommunityAdapter.ViewHolder(view) ;

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.ViewHolder holder, int position) {
        final PostInfo postInfo = mDataset.get(position) ;
        Log.d("context","test.onBindViewHolder");

        holder.tv_title.setText(postInfo.getTitle());
        holder.tv_content.setText(postInfo.getContent());
        if(postInfo.getImg().size() == 0){
            Log.d("postInfo.getImg()", "null");
            holder.iv_image.setVisibility(View.GONE);
        }
        else{
            holder.iv_image.setVisibility(View.VISIBLE);
            Log.d("postgetImg", String.valueOf(new HashMap((Map) postInfo.getImg().get(0)).get("stringUri")));
            holder.iv_image.setImageBitmap(getImageBitmap(String.valueOf(new HashMap((Map) postInfo.getImg().get(0)).get("stringUri"))));

        }
    }

    private Bitmap getImageBitmap(String url) {

        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("gggggg", "Error getting bitmap", e);
        }
        return bm;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
