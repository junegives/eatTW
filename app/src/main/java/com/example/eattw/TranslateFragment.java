package com.example.eattw;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TranslateFragment  extends Fragment {

    private View view;
    private Button btn_picture_go;


    @Nullable
    @Override
    //Fragment는 onCreateView로 생성
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_translate, container, false);

        btn_picture_go = (Button)view.findViewById(R.id.btn_picture_go);
        btn_picture_go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), PicTransActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
