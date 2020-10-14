package com.example.eattw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class WritePostActivity extends AppCompatActivity {

    private static final String TAG = "WritePostActivity";

    private Spinner spinner;
    private EditText et_title;
    private EditText et_content;
    private String category;

    private LinearLayout image1;
    private LinearLayout image2;
    private LinearLayout image3;
    private LinearLayout image4;
    private LinearLayout image5;

    private ImageView iv_image1;
    private ImageView iv_image2;
    private ImageView iv_image3;
    private ImageView iv_image4;
    private ImageView iv_image5;

    private EditText et_image1;
    private EditText et_image2;
    private EditText et_image3;
    private EditText et_image4;
    private EditText et_image5;

    private ScrollView scrollView;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    AlertDialog waitingDialog;

    //권한 요청을 할 때 발생하는 창에 대한 결과값을 받기 위해 지정해주는 정수
    private final int GALLERY_PERMISSIONS = 123;

    //요청할 권한을 배열로 저장 ( 외부저장소 읽기, 외부저장소 쓰기)
    private String[] PERMISSIONS_GALLERY = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //onActivityResult를 위해 사용할 requestCode
    private static final int PICK_FROM_GALLERY = 2;

    //사진 경로
    ArrayList<String> imageList = new ArrayList<>();
    //사진에 대한 설명
    ArrayList<String> desList = new ArrayList<>();

    private PostInfo postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        firebaseAuth = firebaseAuth.getInstance();

        spinner = (Spinner) findViewById(R.id.spinner_select_category);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);

        scrollView = findViewById(R.id.scrollView);

        image1 = (LinearLayout) findViewById(R.id.image1);
        image2 = (LinearLayout) findViewById(R.id.image2);
        image3 = (LinearLayout) findViewById(R.id.image3);
        image4 = (LinearLayout) findViewById(R.id.image4);
        image5 = (LinearLayout) findViewById(R.id.image5);

        iv_image1 = (ImageView) findViewById(R.id.iv_image1);
        iv_image2 = (ImageView) findViewById(R.id.iv_image2);
        iv_image3 = (ImageView) findViewById(R.id.iv_image3);
        iv_image4 = (ImageView) findViewById(R.id.iv_image4);
        iv_image5 = (ImageView) findViewById(R.id.iv_image5);

        et_image1 = (EditText) findViewById(R.id.et_image1);
        et_image2 = (EditText) findViewById(R.id.et_image2);
        et_image3 = (EditText) findViewById(R.id.et_image3);
        et_image4 = (EditText) findViewById(R.id.et_image4);
        et_image5 = (EditText) findViewById(R.id.et_image5);

        findViewById(R.id.btn_write_close).setOnClickListener(onClickListener);
        findViewById(R.id.btn_write_done).setOnClickListener(onClickListener);
        findViewById(R.id.btn_write_image).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete1).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete2).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete3).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete4).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete5).setOnClickListener(onClickListener);

        waitingDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("글 업로드 중")
                .setContext(this)
                .build();

        //수정
        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        //postInit();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_write_close:
                    finish();
                    break;
                case R.id.btn_write_done:
                    //Toast.makeText(WritePostActivity.this, "글 제목 : " + et_title.getText() + "\n글 내용 : " + et_content.getText(), Toast.LENGTH_SHORT).show();
                    desList = new ArrayList<>();
                    waitingDialog.show();
                    storageUpload();
                    break;
                case R.id.btn_write_image:
                    //권한 설정되어 있는지 먼저 확인
                    //권한이 허용되어있지 않다면 권한요청
                    if (!hasPermissions(WritePostActivity.this, PERMISSIONS_GALLERY)) {
                        ActivityCompat.requestPermissions(WritePostActivity.this, PERMISSIONS_GALLERY, GALLERY_PERMISSIONS);
                    }

                    //권한이 허용되어 있다면 다음 화면 진행
                    else if (imageList.size() >= 5) {
                        Toast.makeText(WritePostActivity.this, "사진은 5개까지 업로드 가능 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        gotoGallery();
                    }
                    break;
                case R.id.btn_delete1:
                    showAlert(1);
                    break;
                case R.id.btn_delete2:
                    showAlert(2);
                    break;
                case R.id.btn_delete3:
                    showAlert(3);
                    break;
                case R.id.btn_delete4:
                    showAlert(4);
                    break;
                case R.id.btn_delete5:
                    showAlert(5);
                    break;
            }
        }
    };

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void gotoGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "다중 선택은 '포토'를 선택하세요."), PICK_FROM_GALLERY);
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        } else {
            Log.e("kitkat under", "..");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(WritePostActivity.this, "이미지 불러오기 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == PICK_FROM_GALLERY) {
            //Toast.makeText(WritePostActivity.this, "이미지 불러오기 성공!", Toast.LENGTH_SHORT).show();

            // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
            if (data.getClipData() == null) {
                Log.i("1. single choice", String.valueOf(data.getData()));
                addImage(data.getData());
            } else {
                ClipData clipData = data.getClipData();
                Log.i("clipdata", String.valueOf(clipData.getItemCount()));
                if (clipData.getItemCount() > 5 || clipData.getItemCount() + imageList.size() > 5) {
                    Toast.makeText(WritePostActivity.this, "사진은 5개까지 업로드 가능 합니다.\n현재 총 " + imageList.size() + "장", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 멀티 선택에서 하나만 선택했을 경우
                else if (clipData.getItemCount() == 1) {
                    Log.i("2. clipdata choice", String.valueOf(clipData.getItemAt(0).getUri()));
                    Log.i("2. single choice", clipData.getItemAt(0).getUri().getPath());
                    addImage(clipData.getItemAt(0).getUri());

                } else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 5) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        addImage(clipData.getItemAt(i).getUri());
                    }
                }
            }
        }
    }

    private void addImage(Uri imageURI) {
        imageList.add(imageURI.toString());
        if (imageList.size() == 1) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.GONE);
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image1.setImageURI(imageURI);

        } else if (imageList.size() == 2) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image2.setImageURI(imageURI);
        } else if (imageList.size() == 3) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image3.setImageURI(imageURI);
        } else if (imageList.size() == 4) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.GONE);

            iv_image4.setImageURI(imageURI);
        } else if (imageList.size() == 5) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);

            iv_image5.setImageURI(imageURI);
        }

        scrollView.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void deleteImg(int id) {
        imageList.remove(id - 1);
        //Toast.makeText(this, ""+imageList.size(), Toast.LENGTH_SHORT).show();

        if (imageList.size() == 0) {
            image1.setVisibility(View.GONE);
            image2.setVisibility(View.GONE);
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image1.setImageResource(0);
        }

        if (imageList.size() == 1) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.GONE);
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image2.setImageResource(0);

            iv_image1.setImageURI(Uri.parse(imageList.get(0)));
        } else if (imageList.size() == 2) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image3.setImageResource(0);

            iv_image1.setImageURI(Uri.parse(imageList.get(0)));
            iv_image2.setImageURI(Uri.parse(imageList.get(1)));
        } else if (imageList.size() == 3) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.GONE);
            image5.setVisibility(View.GONE);

            iv_image4.setImageResource(0);

            iv_image1.setImageURI(Uri.parse(imageList.get(0)));
            iv_image2.setImageURI(Uri.parse(imageList.get(1)));
            iv_image3.setImageURI(Uri.parse(imageList.get(2)));
        } else if (imageList.size() == 4) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.GONE);

            iv_image5.setImageResource(0);

            iv_image1.setImageURI(Uri.parse(imageList.get(0)));
            iv_image2.setImageURI(Uri.parse(imageList.get(1)));
            iv_image3.setImageURI(Uri.parse(imageList.get(2)));
            iv_image4.setImageURI(Uri.parse(imageList.get(3)));
        }
    }

    private void storageUpload() {
        final String title = et_title.getText().toString();
        final String content = et_content.getText().toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

        //final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

        if (title.length() > 0 && content.length() > 0) {
            user = firebaseAuth.getCurrentUser();

            if (imageList.size() == 0) {
//                Log.d("사진 없다", img.size() + "\n사진 : " + img.toString());
                PostInfo postInfo = new PostInfo(category, title, content, user.getUid(), imageList, desList, new Date());
                uploader(postInfo);
            } else {
                int i = 0;
                for (i = 0; i < imageList.size(); i++) {
                    Uri file = Uri.parse(imageList.get(i));

                    final StorageReference riversRef = storageRef.child("images/" + user.getUid() + "/" + file.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(file);

                    final int finalI = i;
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.d("이미지 업로드", "실패");
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return riversRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d("TestImageDownload", "성공: " + downloadUri);
                                if (finalI == 0) {
                                    imageList.set(0, downloadUri.toString());
                                    desList.add(et_image1.getText().toString());
//                                    ImgInfo imgInfo = new ImgInfo(downloadUri.toString(), et_image1.getText().toString());
//                                    img.add(imgInfo);
//                                    Log.d("사진 다넣었다", img.size() + "\n사진 : " + img.toString());
                                    Log.d("사진 넣는중!!", "하나");
                                } else if (finalI == 1) {
                                    imageList.set(1, downloadUri.toString());
                                    desList.add(et_image2.getText().toString());
//                                    ImgInfo imgInfo = new ImgInfo(downloadUri.toString(), et_image2.getText().toString());
//                                    img.add(imgInfo);
//                                    Log.d("사진 다넣었다", img.size() + "\n사진 : " + img.toString());
                                    Log.d("사진 넣는중!!", "둘");
                                } else if (finalI == 2) {
                                    imageList.set(2, downloadUri.toString());
                                    desList.add(et_image3.getText().toString());
//                                    ImgInfo imgInfo = new ImgInfo(downloadUri.toString(), et_image3.getText().toString());
//                                    img.add(imgInfo);
//                                    Log.d("사진 다넣었다", img.size() + "\n사진 : " + img.toString());
                                    Log.d("사진 넣는중!!", "셋");
                                } else if (finalI == 3) {
                                    imageList.set(3, downloadUri.toString());
                                    desList.add(et_image4.getText().toString());
//                                    ImgInfo imgInfo = new ImgInfo(downloadUri.toString(), et_image4.getText().toString());
//                                    img.add(imgInfo);
//                                    Log.d("사진 다넣었다", img.size() + "\n사진 : " + img.toString());
                                    Log.d("사진 넣는중!!", "넷");
                                } else if (finalI == 4) {
                                    imageList.set(4, downloadUri.toString());
                                    desList.add(et_image5.getText().toString());
//                                    ImgInfo imgInfo = new ImgInfo(downloadUri.toString(), et_image5.getText().toString());
//                                    img.add(imgInfo);
//                                    Log.d("사진 다넣었다", img.size() + "\n사진 : " + img.toString());
                                    Log.d("사진 넣는중!!", "다섯");
                                }
                                //마지막 사진까지 다 for문 돌렸을 때
                                if (finalI == imageList.size() - 1) {
//                                    Log.d("사진 다넣었다진짜로", img.size() + "\n사진 : " + img.toString());
                                    PostInfo postInfo = new PostInfo(category, title, content, user.getUid(), imageList, desList, new Date());
                                    uploader(postInfo);
                                }
                            } else {
                                Log.d("TestImageDownload", "실패");
                            }
                        }
                    });
                }
            }
        } else if (title.length() <= 0) {
            waitingDialog.dismiss();
            et_title.setError("제목을 입력하세요");
            et_title.requestFocus();
        } else {
            waitingDialog.dismiss();
            et_content.setError("내용을 입력하세요");
            et_content.requestFocus();
        }
    }

    private void uploader(PostInfo postInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(category).add(postInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        waitingDialog.dismiss();
        finish();
    }

    private void showAlert(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WritePostActivity.this);
        builder.setTitle("삭제하시겠습니까?");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImg(id);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();

    }
}
