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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SingupActivity";
    private EditText et_email_signup;
    private EditText et_password_signup;
    private EditText et_password_check_signup;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = firebaseAuth.getInstance();

        et_email_signup = findViewById(R.id.et_email_signup);
        et_password_signup = findViewById(R.id.et_password_signup);
        et_password_check_signup = findViewById(R.id.et_password_check_signup);

        findViewById(R.id.btn_signup).setOnClickListener(onClickListener);
        findViewById(R.id.btn_login_go).setOnClickListener(onClickListener);
    }


    //클릭 이벤트
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_signup:
                    signup();
                    break;
                case R.id.btn_login_go:
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    private void signup() {
        final String email = et_email_signup.getText().toString().trim();
        final String password = et_password_signup.getText().toString().trim();
        final String password_check = et_password_check_signup.getText().toString().trim();
        //trim() = 공백인 부분을 제거함

        if (!password.equals(password_check)) {
            et_password_check_signup.setError("비밀번호가 일치하지 않습니다.");
            et_password_check_signup.requestFocus();
        } else if (email.equals("")) {
            et_email_signup.setError("이메일을 입력하세요");
            et_email_signup.requestFocus();
        } else if (password.equals("")) {
            et_password_signup.setError("비밀번호를 입력하세요");
            et_password_signup.requestFocus();
        } else if (password_check.equals("")) {
            et_password_check_signup.setError("비밀번호 확인란을 입력하세요");
            et_password_check_signup.requestFocus();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //성공했을 때
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(SignupActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            //실패했을 때
                            else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(SignupActivity.this, "가입 실패. 인터넷 연결을 확인하세요", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    et_email_signup.setError("이미 사용중인 이메일입니다.");
                                    et_email_signup.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e){
                                    et_email_signup.setError("이메일 형식이 올바르지 않습니다.");
                                    et_email_signup.requestFocus();
                                }
                                catch (Exception e) {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
}
