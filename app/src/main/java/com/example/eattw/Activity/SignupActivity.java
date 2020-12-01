package com.example.eattw.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SingupActivity";
    private EditText et_email_signup;
    private EditText et_password_signup;
    private EditText et_password_check_signup;
    FirebaseAuth firebaseAuth;

    private AlertDialog waitingDialog;

    private static String IP_ADDRESS = "54.180.82.230";

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

        waitingDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("잠시만 기다려주세요")
                .setContext(this)
                .build();
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
            waitingDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //성공했을 때
                            if (task.isSuccessful()) {

                                InsertData tasking = new InsertData();
                                tasking.execute("http://" + IP_ADDRESS + "/eatTW/insert_user.php", email);

                                Log.d(TAG, "createUserWithEmail:success");
                            }
                            //실패했을 때
                            else {
                                waitingDialog.dismiss();
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(SignupActivity.this, "가입 실패. 인터넷 연결을 확인하세요", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    et_email_signup.setError("이미 사용중인 이메일입니다.");
                                    et_email_signup.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    et_email_signup.setError("이메일 형식이 올바르지 않습니다.");
                                    et_email_signup.requestFocus();
                                } catch (Exception e) {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
    class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(SignupActivity.this,
//                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            waitingDialog.dismiss();
            Toast.makeText(SignupActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            //mTextViewResult.setText(result);
            Log.d(TAG+"_", "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String userID = (String)params[1];
            Log.d("WTF", userID);

            String serverURL = (String)params[0];
            String postParameters = "userID=" + userID;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG+"_", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG+"_", "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}