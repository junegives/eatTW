package com.example.eattw.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eattw.Item.PostInfo;
import com.example.eattw.R;
import com.example.eattw.Activity.WritePostActivity;
import com.example.eattw.Adapter.CommunityAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Date;

public class CommunityFragment  extends Fragment {

    private static final String TAG = "CommunityFragment";

    private View view;

    private RadioGroup community_radio;
    private RadioButton btnRadio_board;
    private RadioButton btnRadio_information;
    private RadioButton btnRadio_review;
    private RadioButton btnRadio_qna;
    private RadioButton btnRadio_promotion;

    private String category = "잡담";

    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ArrayList<PostInfo> postList = new ArrayList<>();

    private RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;

    ProgressDialog dialog;

    @Nullable
    @Override
    //Fragment는 onCreateView로 생성
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_community, container, false);

        //initRecycler();

        view.findViewById(R.id.btn_community_search).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_community_write).setOnClickListener(onClickListener);

        db = FirebaseFirestore.getInstance();

        community_radio = view.findViewById(R.id.community_radio);

        btnRadio_board = view.findViewById(R.id.btnRadio_board);
        btnRadio_information = view.findViewById(R.id.btnRadio_information);
        btnRadio_review = view.findViewById(R.id.btnRadio_review);
        btnRadio_qna = view.findViewById(R.id.btnRadio_qna);
        btnRadio_promotion = view.findViewById(R.id.btnRadio_promotion);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("게시글 불러오는중...");
        dialog.setCancelable(true);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        community_radio.setOnCheckedChangeListener(onCheckedChangeListener);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                reload();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            dialog.show();
            if (checkedId == R.id.btnRadio_board) {
                category = "잡담";
                Log.d("alignTest", category + "");
                reload();
                //Adapter.notifyDataSetChanged();
            } else if (checkedId == R.id.btnRadio_information) {
                category = "정보";
                Log.d("alignTest", category + "");
                reload();
                //Adapter.notifyDataSetChanged();

            } else if (checkedId == R.id.btnRadio_review) {
                category = "리뷰";
                Log.d("alignTest", category + "");
                reload();
                //Adapter.notifyDataSetChanged();

            } else if (checkedId == R.id.btnRadio_qna) {
                category = "Q&A";
                Log.d("alignTest", category + "");
                reload();
                //Adapter.notifyDataSetChanged();

            } else if (checkedId == R.id.btnRadio_promotion) {
                category = "모임";
                Log.d("alignTest", category + "");
                reload();
                //Adapter.notifyDataSetChanged();

            } else {
            }

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_community_search:
                    break;

                //글쓰기
                case R.id.btn_community_write:
                    //bottomsheetdialog 띄우기
                    //게시판 선택
                    //글쓰기 화면으로 넘어가기
                    Intent intent = new Intent(getActivity(), WritePostActivity.class);
                    startActivity(intent);

                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        dialog.show();
        reload();
    }

    public void initRecycler(){
        recyclerView = view.findViewById(R.id.recycler_community);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CommunityAdapter(postList, this);
        recyclerView.setAdapter(mAdapter);
    }

    public void reload(){
        Log.d(TAG, category + " => " + category);
        postList.clear();
        initRecycler();
        db.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("category", category).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            TextView no_post = view.findViewById(R.id.no_posts);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                postList.add(new PostInfo(
                                        document.getId(),
                                        document.getData().get("userID").toString(),
                                        document.getData().get("category").toString(),
                                        document.getData().get("title").toString(),
                                        document.getData().get("content").toString(),
                                        document.getData().get("nickname").toString(),
                                        (ArrayList<String>) document.getData().get("imageList"),
                                        (ArrayList<String>) document.getData().get("desList"),
                                        new Date(document.getDate("timestamp").getTime()),
                                        document.getLong("like").intValue(),
                                        document.getLong("scrap").intValue(),
                                        document.getLong("comments").intValue()));
                            }
                            if(postList.size() == 0){
                                dialog.dismiss();
                                no_post.setVisibility(View.VISIBLE);
                            }
                            else{
                                Log.d("hihi", postList.get(0).getTimestamp().toString());
                                no_post.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        } else {
                            dialog.dismiss();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //글 쓰는건 D\android\android\clamp\NewPostActivity.java 참고

    //이게 찐
//    private void readPost() {
//        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        firebaseFirestore.collection("Posts").document(postid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                blogPostList.clear();
//                BlogPost blogPost = documentSnapshot.toObject(BlogPost.class);
//                blogPostList.add(blogPost);
//
//                blogRecyclerAdapter.notifyDataSetChanged();
//
//            }
//        });
//    }

//    private void readPostsFromDB()
//    {
//        // Read from the database
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String id = snapshot.getKey();
//                    final Post post = snapshot.getValue(Post.class);
//                    post.setId(id);
//                    DatabaseReference userRef = database.getReference("Users").child(post.getUserId());
//                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            // cast datasnapshot to User
//                            User user = dataSnapshot.getValue(User.class);
//                            post.setUser(user);
//
//                            // apend post to list (posts)
//                            posts.add(post);
//                            postAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Toast.makeText(getActivity(), " "+error.toException(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
}
