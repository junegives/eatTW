package com.example.eattw.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eattw.Adapter.CommentAdapter;
import com.example.eattw.Helper.SoftKeyboard;
import com.example.eattw.Helper.TimeCaculator;
import com.example.eattw.Item.CommentInfo;
import com.example.eattw.Item.PostInfo;
import com.example.eattw.Item.UserInfo;
import com.example.eattw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReadPostActivity extends AppCompatActivity {

    SoftKeyboard softKeyboard;
    ConstraintLayout ll;
    InputMethodManager controlManager;

    private View view;
    private Date bundleID;
    private int depth;
    private String userID="";

    private static final String TAG = "ReadPostActivity";

    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private String postID;
    private PostInfo postInfo;
    private UserInfo userInfo;

    private ArrayList<String> ImageList;
    private ArrayList<String> DesList;

    private ImageView iv_post_image;
    private TextView tv_post_nickname;
    private TextView tv_post_category;
    private TextView tv_post_timestamp;

    private ImageButton btn_mypost;
    private BottomSheetDialog bottomSheetDialog;
    private Button btn_mypost_modfiy;
    private Button btn_mypost_delete;

    private TextView tv_post_title;
    private TextView tv_post_content;

    private ImageView iv_post_image1;
    private TextView tv_post_image1;
    private ImageView iv_post_image2;
    private TextView tv_post_image2;
    private ImageView iv_post_image3;
    private TextView tv_post_image3;
    private ImageView iv_post_image4;
    private TextView tv_post_image4;
    private ImageView iv_post_image5;
    private TextView tv_post_image5;

    private ArrayList<ImageView> ImageViewList;
    private ArrayList<TextView> DesTextList;

    private RecyclerView rv_comment;
    private CommentAdapter commentAdapter;
    private ArrayList<CommentInfo> commentList;

    private BottomSheetDialog bottomSheetDialog_comment;
    private Button btn_mycomment_modfiy;
    private Button btn_mycomment_delete;

    private ScrollView scrollView;

    private CircleImageView comment_user_image;
    private EditText et_comment;
    private Button btn_add_comment;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);

        Log.d("errorTAG", "1");

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Log.d("errorTAG", "3");

        dialog = new ProgressDialog(this);
        dialog.setMessage("불러오는중...");
        dialog.setCancelable(true);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_read_post);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getPostData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        ll = (ConstraintLayout)findViewById(R.id.ll);
        controlManager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(ll, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
        {
            @Override
            public void onSoftKeyboardHide()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 내려갔을때
                        if(view != null) {
                            view.setBackgroundResource(R.drawable.comment_list);
                            if (et_comment.getText().length() > 0) {
                                showAlert();
                            }
                        }
                        view = null;
                        depth = 0;
                        userID = "";
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 올라왔을때
                        //scrollView.scrollTo(0, view.getTop() - 100);
                        if(view != null) {
                            view.setBackgroundResource(R.drawable.comment_selected);
                            if (et_comment.getText().length() > 0) {
                                showAlert();
                            }
                        }
                    }
                });
            }
        });
        postID = (String) getIntent().getSerializableExtra("postID");
        getPostData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final DocumentReference docRef = db.collection("Posts").document(postID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    //getPostData();
                } else {
                    Log.d(TAG, "Current data: null");
                    Toast.makeText(ReadPostActivity.this, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

/*    @Override
    protected void onPause() {
        super.onPause();
        controlManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }*/

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        softKeyboard.closeSoftKeyboard();
        softKeyboard.unRegisterSoftKeyboardCallback();
        //controlManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void initRecycler() {
        rv_comment = findViewById(R.id.rv_comment);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        rv_comment.setAdapter(commentAdapter);


        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick_mycomment(int position) {
                mycomment(position);
            }

            public void onItemClick_recomment(View view, int position, int depth, String userID, Date bundleID) {
                reply(view, position, depth, userID, bundleID);
            }
        });
}

    public void mycomment(final int position) {
        Log.d("CommentTest", "position : " + position);
        bottomSheetDialog_comment.show();
        btn_mycomment_modfiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog_comment.dismiss();
            }
        });
        btn_mycomment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CommentTest", "삭제");
                db.collection("Posts/" + postID + "/Comments").document(commentList.get(position).getCommentID())
                        .update("deleted", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                Toast.makeText(ReadPostActivity.this, "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                commentList.get(position).setDeleted(true);
                                commentAdapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                                Toast.makeText(ReadPostActivity.this, "댓글 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                bottomSheetDialog_comment.dismiss();
            }
        });
    }

    public void reply(final View view, final int position, int depth, String userID, Date bundleID){
        Log.d("ReplyTest", "position : " + position);
        this.view = view;
        this.depth = 1;
        this.userID = userID;
        this.bundleID = bundleID;

        et_comment.requestFocus();
        controlManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        //softKeyboard.openSoftKeyboard();
    }

    public void getPostData() {
        dialog.show();
        Log.d("getDataTest", "getPostData()");
        db.collection("Posts").document(postID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("errorTAG", "4");
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("errorTAG", "5");
                                postInfo = new PostInfo(
                                        document.getId(),
                                        document.getData().get("category").toString(),
                                        document.getData().get("title").toString(),
                                        document.getData().get("content").toString(),
                                        (ArrayList<String>) document.getData().get("imageList"),
                                        (ArrayList<String>) document.getData().get("desList"),
                                        new Date(document.getDate("timestamp").getTime()),
                                        document.getLong("like").intValue(),
                                        document.getLong("scrap").intValue(),
                                        document.getLong("comments").intValue(),
                                        document.getDocumentReference("userRef"));
                                Log.d("ReferenceTest", postInfo.getUserRef().toString());
                                getUserData(postInfo);
                            } else {
                                Log.d("errorTAG", "6");
                                Toast.makeText(ReadPostActivity.this, "존재하지 않는 게시글입니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Log.d("errorTAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getUserData(PostInfo postInfo) {
        Log.d("getDataTest", "getUserData()");

        postInfo.getUserRef().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("errorTAG", task.getResult().toString());
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("errorTAG", "5");
                                userInfo = new UserInfo(document.getData().get("userID").toString(),
                                        document.getData().get("nickname").toString(),
                                        document.getData().get("photoUrl").toString());
                                updateUI();
                                getCommentData();
                            } else {
                                Log.d("errorTAG", "6");
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        } else {
                            Log.d("errorTAG", "6");
                        }
                    }
                });
    }

    public void getCommentData() {
        initRecycler();
        Log.d("getDataTest", "getCommentData()");
        db.collection("Posts/" + postID + "/Comments").orderBy("bundleID").orderBy("timestamp").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("CommentTestAdd", document.getId() + " => " + document.getData());
                        commentList.add(new CommentInfo(
                                document.getId(),
                                document.getData().get("postID").toString(),
                                document.getData().get("userID").toString(),
                                document.getData().get("comment").toString(),
                                document.getLong("like").intValue(),
                                new Date(document.getDate("timestamp").getTime()),
                                document.getData().get("reply_userID").toString(),
                                document.getLong("depth").intValue(),
                                new Date(document.getDate("bundleID").getTime()),
                                document.getBoolean("deleted").booleanValue()));
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updateUI() {

        Log.d("errorTAG", "7");

        iv_post_image = (ImageView) findViewById(R.id.iv_post_image);
        tv_post_nickname = (TextView) findViewById(R.id.tv_post_nickname);
        tv_post_category = (TextView) findViewById(R.id.tv_post_category);
        tv_post_timestamp = (TextView) findViewById(R.id.tv_post_timestamp);

        btn_mypost = (ImageButton) findViewById(R.id.btn_mypost);
        btn_mypost.setVisibility(View.GONE);

        bottomSheetDialog = new BottomSheetDialog(ReadPostActivity.this);
        bottomSheetDialog.setContentView(R.layout.mypost_dialog);

        btn_mypost_modfiy = (Button) bottomSheetDialog.findViewById(R.id.btn_mypost_modfiy);
        btn_mypost_modfiy.setOnClickListener(onClickListener);

        btn_mypost_delete = (Button) bottomSheetDialog.findViewById(R.id.btn_mypost_delete);
        btn_mypost_delete.setOnClickListener(onClickListener);

        bottomSheetDialog_comment = new BottomSheetDialog(ReadPostActivity.this);
        bottomSheetDialog_comment.setContentView(R.layout.mycomment_dialog);

        btn_mycomment_modfiy = (Button) bottomSheetDialog_comment.findViewById(R.id.btn_mycomment_modfiy);
        //btn_mycomment_modfiy.setOnClickListener(onClickListener);

        btn_mycomment_delete = (Button) bottomSheetDialog_comment.findViewById(R.id.btn_mycomment_delete);
        //btn_mycomment_delete.setOnClickListener(onClickListener);

        tv_post_title = (TextView) findViewById(R.id.tv_post_title);
        tv_post_content = (TextView) findViewById(R.id.tv_post_content);

        iv_post_image1 = (ImageView) findViewById(R.id.iv_post_image1);
        tv_post_image1 = (TextView) findViewById(R.id.tv_post_image1);
        iv_post_image2 = (ImageView) findViewById(R.id.iv_post_image2);
        tv_post_image2 = (TextView) findViewById(R.id.tv_post_image2);
        iv_post_image3 = (ImageView) findViewById(R.id.iv_post_image3);
        tv_post_image3 = (TextView) findViewById(R.id.tv_post_image3);
        iv_post_image4 = (ImageView) findViewById(R.id.iv_post_image4);
        tv_post_image4 = (TextView) findViewById(R.id.tv_post_image4);
        iv_post_image5 = (ImageView) findViewById(R.id.iv_post_image5);
        tv_post_image5 = (TextView) findViewById(R.id.tv_post_image5);

        ImageList = postInfo.getImageList();
        DesList = postInfo.getDesList();

        ImageViewList = new ArrayList<ImageView>(
                Arrays.asList(iv_post_image1, iv_post_image2, iv_post_image3, iv_post_image4, iv_post_image5)
        );

        DesTextList = new ArrayList<TextView>(
                Arrays.asList(tv_post_image1, tv_post_image2, tv_post_image3, tv_post_image4, tv_post_image5)
        );

        for (int i = 0; i < ImageViewList.size(); i++) {
            ImageViewList.get(i).setVisibility(View.GONE);
            DesTextList.get(i).setVisibility(View.GONE);
        }

        if (userInfo.getPhotoUrl() != null && userInfo.getPhotoUrl() != "") {
            Log.d("errorTAG", "8");
            Glide.with(this)
                    .load(Uri.parse(userInfo.getPhotoUrl()))
                    .override(300, 300)
                    .thumbnail(0.1f)
                    .into(iv_post_image);
        } else {
            Log.d("errorTAG", "9");
        }

        tv_post_nickname.setText(userInfo.getNickname());
        tv_post_category.setText(postInfo.getCategory());

        TimeCaculator timeCaculator = new TimeCaculator();
        tv_post_timestamp.setText(
                timeCaculator.beforeTime(postInfo.getTimestamp()));

        if (user.getUid().equals(userInfo.getUserID())) {
            btn_mypost.setVisibility(View.VISIBLE);
            btn_mypost.setOnClickListener(onClickListener);
        }

        tv_post_title.setText(postInfo.getTitle());
        tv_post_content.setText(postInfo.getContent());

        for (int i = 0; i < ImageList.size(); i++) {
            ImageViewList.get(i).setVisibility(View.VISIBLE);
            DesTextList.get(i).setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Uri.parse(ImageList.get(i)))
                    .override(1000, 1000)
                    .into(ImageViewList.get(i));

            Log.d("ImageTest", "i : " + i + " size : " + ImageList.size());
            DesTextList.get(i).setText(DesList.get(i));
        }

        comment_user_image = findViewById(R.id.comment_user_image);
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .override(100, 100)
                    .thumbnail(0.1f)
                    .into(comment_user_image);
        } else {
            Glide.with(this)
                    .load(R.drawable.mandoo_profile)
                    .override(100, 100)
                    .thumbnail(0.1f)
                    .into(comment_user_image);
        }

        et_comment = findViewById(R.id.et_comment);

        btn_add_comment = findViewById(R.id.btn_add_comment);
        btn_add_comment.setOnClickListener(onClickListener);

        scrollView = findViewById(R.id.post_scrollview);

        dialog.dismiss();
    }

    //클릭 이벤트
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_mypost:
                    bottomSheetDialog.show();
                    break;

                case R.id.btn_mypost_modfiy:
                    bottomSheetDialog.dismiss();
                    modifyPost();
                    break;

                case R.id.btn_mypost_delete:
                    bottomSheetDialog.dismiss();
                    deletePost();
                    break;

                case R.id.btn_add_comment:
                    if(depth == 0){
                        bundleID = new Date();
                    }
                    addComment();
                    controlManager.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
                    break;
            }
        }
    };

    public void addComment() {
        final String comment = et_comment.getText().toString();
        et_comment.setText("");

        if (!comment.isEmpty()) {
            final CommentInfo commentInfo = new CommentInfo(postID, user.getUid(), comment, new Date(), userID, depth, bundleID, false);
            depth = 0;
            userID = "";

            db.collection("Posts/" + postID + "/Comments").add(commentInfo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    String documentID = String.valueOf(task.getResult());
                    Log.d("CommentTest", "String.valueOf(task.getResult()) : " + String.valueOf(task.getResult()));
                    if (!task.isSuccessful()) {
                        Toast.makeText(ReadPostActivity.this, "Error Posting Comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        et_comment.setText(comment);
                        //알람 보내기
                        if(!commentInfo.getReply_userID().equals("")){

                        }
                    } else {
                        getCommentData();
                        scrollView.post(new Runnable() {

                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                }
            });
        }
    }

    public void modifyPost() {
        Intent intent = new Intent(ReadPostActivity.this, WritePostActivity.class);
        intent.putExtra("postInfo", postInfo);
        intent.putExtra("postID", postID);
        startActivity(intent);
    }

    public void deletePost() {
        db.collection("Posts").document(postID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(ReadPostActivity.this, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(ReadPostActivity.this, "게시글 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReadPostActivity.this);
        builder.setTitle("작성중인 댓글이 있습니다. 삭제하시겠습니까?");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        et_comment.setText("");
                        depth = 0;
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();

    }
}