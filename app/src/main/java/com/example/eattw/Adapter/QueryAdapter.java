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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eattw.Helper.TimeCaculator;
import com.example.eattw.Item.CommentInfo;
import com.example.eattw.Item.QueryItem;
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

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private ArrayList<QueryItem> mDataset;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private QueryItem queryItem;
    private Context context;

    private String query;

    public interface OnItemClickListener {
        void onItemClick_mycomment(View view, int position, TextView modify_to_username, EditText modify_message, Button modfiy_ok, Button modify_cancel);

        void onItemClick_recomment(View view, int position, int depth, String userID, Date bundleID);
    }

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public QueryAdapter(ArrayList<QueryItem> myDataset, String query_type) {
        mDataset = myDataset;
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        query = query_type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_content;
        TextView cnt_comment;
        TextView query_timestamp;

        ViewHolder(View itemView) {
            super(itemView);

            tv_content = itemView.findViewById(R.id.tv_content);
            cnt_comment = itemView.findViewById(R.id.cnt_comment);
            query_timestamp = itemView.findViewById(R.id.query_timestamp);
        }
    }

    @NonNull
    @Override
    public QueryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        context = parent.getContext();
        Log.d("context", "test.AddMemoAdapter.ViewHolder");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_query, parent, false);
        QueryAdapter.ViewHolder vh = new QueryAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final QueryAdapter.ViewHolder holder, final int position) {
        queryItem = mDataset.get(position);

        TimeCaculator timeCaculator = new TimeCaculator();
        holder.tv_content.setText(queryItem.getTitle());
        if(query.equals("댓글")){
            holder.cnt_comment.setVisibility(View.GONE);
        }
        else{
            holder.cnt_comment.setVisibility(View.VISIBLE);
        }
        holder.query_timestamp.setText(timeCaculator.beforeTime(queryItem.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
