package com.example.eattw.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eattw.Item.UserInfo;
import com.example.eattw.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private CircleImageView profile_img;
    private TextView tv_nickname;

    private ImageView btn_back;
    private TextView tv_follower;
    private TextView tv_following;

    private ConstraintLayout ll_chatting;
    private ConstraintLayout ll_follow;
    private ConstraintLayout ll_follow_cancel;

    private TextView btn_post;
    private TextView btn_comment;
    private TextView tv_store;

    private TextView btn_review;
    private TextView btn_scrap_store;

    private TextView btn_translation_scrap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        profile_img = (CircleImageView) findViewById(R.id.profile_img);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        tv_follower = (TextView) findViewById(R.id.tv_follower);
        tv_following = (TextView) findViewById(R.id.tv_following);

        ll_chatting = (ConstraintLayout) findViewById(R.id.ll_chatting);
        ll_chatting.setOnClickListener(onClickListener);
        ll_follow = (ConstraintLayout) findViewById(R.id.ll_follow);
        ll_follow.setOnClickListener(onClickListener);
        ll_follow_cancel = (ConstraintLayout) findViewById(R.id.ll_follow_cancel);
        ll_follow_cancel.setOnClickListener(onClickListener);

        btn_post = (TextView) findViewById(R.id.btn_post);
        btn_post.setOnClickListener(onClickListener);
        btn_comment = (TextView) findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(onClickListener);
        tv_store = (TextView) findViewById(R.id.tv_store);
        tv_store.setOnClickListener(onClickListener);

        btn_review = (TextView) findViewById(R.id.btn_review);
        btn_review.setOnClickListener(onClickListener);
        btn_scrap_store = (TextView) findViewById(R.id.btn_scrap_store);
        btn_scrap_store.setOnClickListener(onClickListener);

        btn_translation_scrap = (TextView) findViewById(R.id.btn_translation_scrap);
        btn_translation_scrap.setOnClickListener(onClickListener);

        updateUI();

    }

    public void updateUI() {


        if (userInfo.getPhotoUrl() != null && userInfo.getPhotoUrl() != "") {
            Log.d("errorTAG", "8");
            Glide.with(this)
                    .load(Uri.parse(userInfo.getPhotoUrl()))
                    .override(300, 300)
                    .thumbnail(0.1f)
                    .into(profile_img);
        } else {
            Log.d("errorTAG", "9");
        }

        tv_nickname.setText(userInfo.getNickname());

        //팔루워
        tv_follower.setText("");

        //팔로잉
        tv_following.setText("");

//        if ("팔로잉중이면") {
//            ll_follow.setVisibility(View.GONE);
//            ll_follow_cancel.setVisibility(View.VISIBLE);
//        } else {
//            ll_follow_cancel.setVisibility(View.GONE);
//            ll_follow.setVisibility(View.VISIBLE);
//        }
    }

    //클릭 이벤트
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //뒤로가기
                case R.id.btn_back:
                    finish();
                    break;

                //채팅하기
                case R.id.ll_chatting:
                    startActivityObj(ChattingActivity.class, "userInfo", userInfo);
                    break;

                //팔로우하기
                case R.id.ll_follow:
                    ll_follow.setVisibility(View.GONE);
                    ll_follow_cancel.setVisibility(View.VISIBLE);
                    break;

                //언팔하기
                case R.id.ll_follow_cancel:
                    ll_follow_cancel.setVisibility(View.GONE);
                    ll_follow.setVisibility(View.VISIBLE);
                    break;

                //게시글 보기
                case R.id.btn_post:
                    startActivityObject(QueryActivity.class, "userInfo", userInfo, "query", "post");
                    break;

                //작성 댓글 보기
                case R.id.btn_comment:
                    startActivityObject(QueryActivity.class, "userInfo", userInfo, "query", "comment");
                    break;

                //스크랩글 보기
                case R.id.tv_store:
                    startActivityObject(QueryActivity.class, "userInfo", userInfo, "query", "scrap");
                    break;

                //맛집 후기 보기
                case R.id.btn_review:
                    startActivityObject(QueryActivity.class, "userInfo", userInfo, "query", "review");
                    break;

                //스크랩한 맛집 보기
                case R.id.btn_scrap_store:
                    startActivityObject(QueryActivity.class, "userInfo", userInfo, "query", "store");
                    break;

//                //저장한 번역 보기
//                case R.id.btn_translation_scrap:
//                    startActivityObject(QueryActivity.class, "userInfo", userInfo, "query", "translation");
//                    break;

            }
        }
    };

    // 액티비티 전환 함수

    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // userInfo 객체 인텐트 전달 함수
    public void startActivityObj(Class c, String name , UserInfo sendItem) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendItem);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // userInfo 객체와 문자열 인텐트 전달 함수
    public void startActivityObject(Class c, String name , UserInfo sendItem, String name2, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendItem);
        intent.putExtra(name2, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 쉐어드 함수정의
    public void updateSharedString(String key, String value) {
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedString(String key) {
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String result = prefs.getString(key, "null");
        return result;
    }

    public void deleteShared(String key) {
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
}