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

public class SignupActivity extends AppCompatActivity {

    private EditText et_email_signup;
    private EditText et_password_signup;
    private EditText et_password_check_signup;
    private Button btn_signup;
    private Button btn_login_go;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = firebaseAuth.getInstance();

        et_email_signup = findViewById(R.id.et_email_signup);
        et_password_signup = findViewById(R.id.et_password_signup);
        et_password_check_signup = findViewById(R.id.et_password_check_signup);
        btn_signup = findViewById(R.id.btn_signup);
        btn_login_go = findViewById(R.id.btn_login_go);

        btn_signup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final String email = et_email_signup.getText().toString().trim();
                final String password = et_password_signup.getText().toString().trim();
                final String password_check = et_password_check_signup.getText().toString().trim();
                //trim() = 공백인 부분을 제거함

                if(!password.equals(password_check)){
                    Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    et_password_signup.requestFocus();
                }
                else if(email.equals("")){
                    Toast.makeText(SignupActivity.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    et_email_signup.requestFocus();
                }
                else if(password.equals("")){
                    Toast.makeText(SignupActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    et_password_signup.requestFocus();
                }
                else if(password_check.equals("")){
                    Toast.makeText(SignupActivity.this, "비밀번호 확인란을 입력하세요", Toast.LENGTH_SHORT).show();
                    et_password_check_signup.requestFocus();
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //성공했을 때
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    //실패했을 때
                                    else{
                                        Toast.makeText(SignupActivity.this, "가입 실패", Toast.LENGTH_SHORT).show();
                                        et_email_signup.requestFocus();
                                        return;
                                    }
                                }
                            });
                }
            }
        });

        btn_login_go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
