package com.example.eattw;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class MypageFragment  extends Fragment {

    private View view;

    private CircleImageView mypage_profile_img;
    private TextView tv_nickname;
    private LinearLayout layout_follower;
    private TextView tv_follower;
    private LinearLayout layout_following;
    private TextView tv_follwing;

    private ImageButton btn_change_profile;

    private Button btn_logout;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    AlertDialog waitingDialog;

    //사진 경로
    Uri photoUri = null;


    @Nullable
    @Override
    //Fragment는 onCreateView로 생성
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mypage, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        waitingDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("잠시만 기다려주세요")
                .setContext(getContext())
                .build();

        mypage_profile_img = (CircleImageView)view.findViewById(R.id.mypage_profile_img);
        tv_nickname = (TextView)view.findViewById(R.id.tv_nickname);
        layout_follower = (LinearLayout)view.findViewById(R.id.layout_follower);
        tv_follower = (TextView)view.findViewById(R.id.tv_follower);
        layout_following = (LinearLayout)view.findViewById(R.id.layout_following);
        tv_follwing = (TextView)view.findViewById(R.id.tv_following);
        btn_change_profile = (ImageButton)view.findViewById(R.id.btn_change_profile);
        btn_logout = (Button)view.findViewById(R.id.btn_logout);

        layout_follower.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //팔로워 목록 보여주기
                Toast.makeText(getContext(), "팔로워 목록 보여주기", Toast.LENGTH_SHORT).show();
            }
        });

        layout_following.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //팔로잉 목록 보여주기
                Toast.makeText(getContext(), "팔로잉 목록 보여주기", Toast.LENGTH_SHORT).show();
            }
        });

        btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilemodifyActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mypage_profile_img.setImageResource(R.drawable.mandoo_profile);
        tv_nickname.setText(user.getDisplayName());

        if(user.getPhotoUrl() != null){
            Glide.with(getActivity())
                    .load(user.getPhotoUrl())
                    .into(mypage_profile_img);
            //image_profile.setImageURI(user.getPhotoUrl());
            photoUri = user.getPhotoUrl();
        }


        //팔로워와 팔로잉 firebase에서 가져오기
        //tv_follower.setText(user.get)
    }
}
