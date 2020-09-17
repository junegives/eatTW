package com.example.eattw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email_login;
    private EditText et_password_login;
    private Button btn_login;
    private Button btn_signup_go;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = firebaseAuth.getInstance();

        et_email_login = findViewById(R.id.et_email_login);
        et_password_login = findViewById(R.id.et_password_login);
        btn_login = findViewById(R.id.btn_login);
        btn_signup_go = findViewById(R.id.btn_signup_go);

        btn_login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //trim() = 공백 제거 함수
                String email = et_email_login.getText().toString().trim();
                String password = et_password_login.getText().toString().trim();

                if(email.equals("")){
                    Toast.makeText(LoginActivity.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    et_email_login.requestFocus();
                }
                else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    et_password_login.requestFocus();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //성공했을 때
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    //실패했을 때
                                    else {
                                        Toast.makeText(LoginActivity.this, "이메일 혹은 패스워드 입력이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                        et_email_login.requestFocus();
                                    }
                                }
                            });
                }
            }
        });

        btn_signup_go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}