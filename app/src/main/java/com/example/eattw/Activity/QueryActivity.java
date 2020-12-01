package com.example.eattw.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eattw.Adapter.CommentAdapter;
import com.example.eattw.Adapter.QueryAdapter;
import com.example.eattw.Item.PostInfo;
import com.example.eattw.Item.QueryItem;
import com.example.eattw.Item.UserInfo;
import com.example.eattw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.Date;

public class QueryActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private String query_type;

    private ImageView btn_back;
    private TextView tv_nickname_query;
    private TextView tv_query_type;
    private TextView tv_query_num;

    private ArrayList<QueryItem> queryList = new ArrayList<>();

    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private RecyclerView recycler_query;
    private QueryAdapter queryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        query_type = (String) getIntent().getSerializableExtra("query");


        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);

        tv_nickname_query = (TextView) findViewById(R.id.tv_nickname_query);
        tv_query_type = (TextView) findViewById(R.id.tv_query_type);
        tv_query_num = (TextView) findViewById(R.id.tv_query_num);

        recycler_query = (RecyclerView) findViewById(R.id.recycler_query);

        initRecycler();
    }

    public void updateUI() {
        tv_nickname_query.setText(userInfo.getNickname());
        queryList.clear();
        switch (query_type) {
            case "post":
                tv_query_type.setText("게시글");
                db.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d("dledledle", task.toString());
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("dledledle", "userRef : " + document.getData().get("userID"));
                                        Log.d("dledledle", "userInfo : " + userInfo.getUserID());
                                        if (document.getData().get("userID")
                                                .equals(userInfo.getUserID())) {
                                            Log.d("dledledle", "samesame");
                                            queryList.add(new QueryItem(
                                                    document.getData().get("title").toString(),
                                                    document.getLong("comments").intValue(),
                                                    new Date(document.getDate("timestamp").getTime())));
                                        }
                                    }
                                    queryAdapter.notifyDataSetChanged();
                                    tv_query_num.setText(String.valueOf(queryList.size()));
                                } else {
                                    Log.d("dledledle", "fail");
                                }
                            }
                        });
                break;
            case "comment":
                tv_query_type.setText("댓글");
                db.collection("Posts/*/Comments").orderBy("timestamp", Query.Direction.DESCENDING).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getData().get("userID").equals(userInfo.getUserID())) {
                                            queryList.add(new QueryItem(
                                                    document.getData().get("comment").toString(),
                                                    0,
                                                    new Date(document.getDate("timestamp").getTime())));
                                        }
                                    }
                                    queryAdapter.notifyDataSetChanged();
                                    tv_query_num.setText(String.valueOf(queryList.size()));
                                } else {
                                }
                            }
                        });
                break;
            case "scrap":
                tv_query_type.setText("스크랩한 게시글");
                break;
            case "review":
                tv_query_type.setText("맛집 후기");
                break;
            case "store":
                tv_query_type.setText("스크랩한 맛집");
                break;
            case "translation":
                tv_query_type.setText("저장한 번역");
                break;
        }
        queryAdapter.notifyDataSetChanged();
        tv_query_num.setText(String.valueOf(queryList.size()));
    }

    public void load() {
        //db.collection("Posts").
    }

    public void initRecycler() {
        recycler_query = findViewById(R.id.recycler_query);
        queryAdapter = new QueryAdapter(queryList, query_type);
        recycler_query.setLayoutManager(new LinearLayoutManager(this));
        recycler_query.setAdapter(queryAdapter);


        queryAdapter.setOnItemClickListener(new QueryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick_mycomment(View modify_layout, int position, TextView modify_to_username, EditText modfiy_message, Button modfiy_ok, Button modify_cancel) {
//                mycomment(modify_layout, position, modify_to_username, modfiy_message, modfiy_ok, modify_cancel);
            }

            public void onItemClick_recomment(View view, int position, int depth, String userID, Date bundleID) {
//                reply(view, position, depth, userID, bundleID);
            }
        });
        updateUI();
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

    // 객체 인텐트 전달 함수
    public void startActivityObject(Class c, String name, UserInfo sendItem) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendItem);
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