package com.example.eattw;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CommunityFragment  extends Fragment {

    private View view;
    private Spinner spinnerCateogry;

    @Nullable
    @Override
    //Fragment는 onCreateView로 생성
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_community, container, false);

        view.findViewById(R.id.btn_community_search).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_community_write).setOnClickListener(onClickListener);


        return view;
    }

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
}
