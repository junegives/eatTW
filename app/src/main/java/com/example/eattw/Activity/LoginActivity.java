package com.example.eattw.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eattw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText et_email_login;
    private EditText et_password_login;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() != null && user.getDisplayName() != null){
            updateUI();
        }

        et_email_login = findViewById(R.id.et_email_login);
        et_password_login = findViewById(R.id.et_password_login);
        findViewById(R.id.btn_login).setOnClickListener(onClickListener);
        findViewById(R.id.btn_signup_go).setOnClickListener(onClickListener);

    }


    //클릭 이벤트
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    login();
                    break;
                case R.id.btn_signup_go:
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    private void login() {
        String email = et_email_login.getText().toString().trim();
        String password = et_password_login.getText().toString().trim();

        if (email.equals("")) {
            et_email_login.setError("이메일을 입력하세요");
            et_email_login.requestFocus();
        } else if (password.equals("")) {
            et_password_login.setError("비밀번호를 입력하세요");
            et_password_login.requestFocus();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //성공했을 때
                            if (task.isSuccessful()) {
                                updateUI();
                            }
                            //실패했을 때
                            else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(LoginActivity.this, "로그인 실패. 인터넷 연결을 확인하세요", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    et_email_login.setError("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.");
                                    et_email_login.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    et_email_login.setError("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.");
                                    et_email_login.requestFocus();
                                } catch (Exception e) {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }

    private void updateUI(){
        Intent intent = null;
        user = firebaseAuth.getCurrentUser();
        //a = user.getDisplayName();
        //이미 닉네임이 설정되었을 때
        if (user.getDisplayName() == null) {
            Log.d("LoginActivity023", "닉네임 설정하기 go to Initial Activity");
            intent = new Intent(LoginActivity.this, InitialActivity.class);
        }
        //닉네임이 설정되지 않았을 때
        else {
            Log.d("LoginActivity022", user.getUid());
            intent = new Intent(LoginActivity.this, MainActivity.class);
            Toast.makeText(LoginActivity.this, "반갑습니다! " + user.getDisplayName() + "님", Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);
        finish();
    }
}