package com.example.eattw.Activity;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eattw.Item.UserInfo;
import com.example.eattw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends AppCompatActivity implements ReceiverThread.OnReceiveListener, View.OnClickListener {

    private ImageView btn_back;

    private UserInfo userInfo;
    private TextView top_nick;

    private UserInfo myInfo;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    private String myName;

    private ListView lv_message;
    private EditText et_message;
    private ImageButton btn_send;
    SenderThread mThread1;

    private Socket mSocket = null;
    private ChatAdapter mAdapter;
    private ArrayList<Chat> mChatDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            myInfo = new UserInfo(user.getUid(),
                    user.getDisplayName(),
                    user.getPhotoUrl().toString());
        }
        else{
            myInfo = new UserInfo(user.getUid(),
                    user.getDisplayName(),
                    "");
        }
        myName = myInfo.getNickname();

        top_nick = (TextView) findViewById(R.id.top_nick);
        top_nick.setText(userInfo.getNickname());

        lv_message = (ListView) findViewById(R.id.lv_message);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        mChatDataList = new ArrayList<>();
        mAdapter = new ChatAdapter(mChatDataList);
        lv_message.setAdapter(mAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //아이피
                    mSocket = new Socket("172.20.10.3", 7777);
                    // 두번째 파라메터로는 본인의 닉네임을 적어줍니다.
                    mThread1 = new SenderThread(mSocket, myName);
                    ReceiverThread thread2 = new ReceiverThread(mSocket);

                    thread2.setOnReceiveListener(ChattingActivity.this);

                    mThread1.start();
                    thread2.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        mThread1.sendMessage(et_message.getText().toString());
        et_message.setText("");
    }

    @Override
    @WorkerThread
    public void onReceive(final String message) {

        String[] split = message.split(">");

        // ~~가 입장하셨습니다 처리 무시
        if (split.length < 2) {
            return;
        }

        String nickname = split[0];
        String msg = split[1];

        final Chat chat = new Chat();
        if (myName.equals(nickname)) {
            // 나
            chat.isMe = true;
        } else {
            // 남
            chat.isMe = false;
        }
        chat.nickname = nickname;
        chat.message = msg;

        // UI 스레드로 실행
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 메세지 갱신
                mChatDataList.add(chat);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            if(mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public static class Chat {
        public String message;
        public String nickname;
        public boolean isMe;
    }

    private static class ChatAdapter extends BaseAdapter {

        private final List<Chat> mData;

        public ChatAdapter(List<Chat> list) {
            mData = list;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat, parent, false);

                viewHolder.layoutMe = (LinearLayout) convertView.findViewById(R.id.layout_me);
                viewHolder.layoutYou = (LinearLayout) convertView.findViewById(R.id.layout_you);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname_text);
                viewHolder.bubbleYou = (TextView) convertView.findViewById(R.id.bubble_you_text);
                viewHolder.bubbleMe = (TextView) convertView.findViewById(R.id.bubble_me_text);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Chat chat = mData.get(position);

            if (chat.isMe) {
                viewHolder.bubbleMe.setText(chat.message);

                viewHolder.layoutMe.setVisibility(View.VISIBLE);
                viewHolder.layoutYou.setVisibility(View.GONE);
            } else {
                viewHolder.bubbleYou.setText(chat.message);
                viewHolder.nickname.setText(chat.nickname);

                viewHolder.layoutMe.setVisibility(View.GONE);
                viewHolder.layoutYou.setVisibility(View.VISIBLE);
            }

            convertView.setEnabled(false);

            return convertView;
        }
    }

    private static class ViewHolder {
        LinearLayout layoutMe;
        LinearLayout layoutYou;
        TextView nickname;
        TextView bubbleYou;
        TextView bubbleMe;
    }
}

//메시지의 수신을 담당하는 스레드
class ReceiverThread extends Thread {

    interface OnReceiveListener {
        void onReceive(String message);
    }

    OnReceiveListener mListener;

    public void setOnReceiveListener(OnReceiveListener listener) {
        mListener = listener;
    }

    Socket socket;

    public ReceiverThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String str = reader.readLine();

                if (mListener != null) {
                    mListener.onReceive(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// 메시지의 발신을 담당하는 스레드
class SenderThread extends Thread {
    Socket socket;
    String name;
    String uid;
    private PrintWriter mWriter;

    public SenderThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mWriter.println(message);
                mWriter.flush();
            }
        }).start();
    }

    @Override
    public void run() {

        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            mWriter = new PrintWriter(socket.getOutputStream());

            // 제일 먼저 서버로 대화명을 송신합니다.
            mWriter.println(name);
            mWriter.flush();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}