package com.example.eattw.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.eattw.Fragment.CommunityFragment;
import com.example.eattw.Fragment.HomeFragment;
import com.example.eattw.Fragment.MypageFragment;
import com.example.eattw.Fragment.TranslateFragment;
import com.example.eattw.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private HomeFragment homeFragment;
    private TranslateFragment translateFragment;
    private CommunityFragment communityFragment;
    private MypageFragment mypageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //바텀 내비게이션에서 하나의 요소를 선택하면 발생하는 일
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_translate:
                        setFrag(1);
                        break;
                    case R.id.action_community:
                        setFrag(2);
                        break;
                    case R.id.action_mypage:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        homeFragment = new HomeFragment();
        translateFragment = new TranslateFragment();
        communityFragment = new CommunityFragment();
        mypageFragment = new MypageFragment();

        //첫 프래그먼트 화면 지정 (홈 화면)
        setFrag(0);
    }

    //프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0:
                ft.replace(R.id.main_frame, homeFragment);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, translateFragment);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, communityFragment);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, mypageFragment);
                ft.commit();
                break;
        }
    }
}