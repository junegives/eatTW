package com.example.eattw.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
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

public class MyProfileActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private CircleImageView profile_img;
    private TextView tv_nickname;

    private ImageView btn_back;
    private TextView tv_follower;
    private TextView tv_following;

    private ConstraintLayout ll_chatting;

    private TextView btn_post;
    private TextView btn_comment;
    private TextView tv_store;

    private TextView btn_review;
    private TextView btn_scrap_store;

    private TextView btn_translation_scrap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        profile_img = (CircleImageView) findViewById(R.id.profile_img);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        tv_follower = (TextView) findViewById(R.id.tv_follower);
        tv_following = (TextView) findViewById(R.id.tv_following);

        ll_chatting = (ConstraintLayout) findViewById(R.id.ll_chatting);
        ll_chatting.setOnClickListener(onClickListener);

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

                //채팅 목록 보기
                case R.id.ll_chatting:
                    break;

                //게시글 보기
                case R.id.btn_post:
                    break;
                //작성 댓글 보기
                case R.id.btn_comment:
                    break;
                //스크랩글 보기
                case R.id.tv_store:
                    break;

                //맛집 후기 보기
                case R.id.btn_review:
                    break;
                //스크랩한 맛집 보기
                case R.id.btn_scrap_store:
                    break;
                //저장한 번역 보기
                case R.id.btn_translation_scrap:
                    break;
            }
        }
    };
}