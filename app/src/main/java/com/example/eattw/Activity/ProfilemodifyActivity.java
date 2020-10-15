package com.example.eattw.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eattw.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProfilemodifyActivity extends AppCompatActivity {

    private CircleImageView image_profile;
    private EditText et_nick;
    private Button btn_nick_clear;
    private Button btn_profile;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    AlertDialog waitingDialog;

    //권한 요청을 할 때 발생하는 창에 대한 결과값을 받기 위해 지정해주는 정수
    private final int MULTIPLE_PERMISSIONS = 123;

    //요청할 권한을 배열로 저장 ( 카메라, 외부저장소 읽기, 외부저장소 쓰기)
    private String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };

    private String[] PERMISSIONS_GALLERY = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //onActivityResult를 위해 사용할 requestCode
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int CROP_FROM_CAMERA = 3;

    //사진 경로
    Uri photoUri = null;

    //사진이 변경되었는지 체크
    boolean photochanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilemodify);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        image_profile = (CircleImageView)findViewById(R.id.image_profile);
        et_nick = (EditText)findViewById(R.id.et_nick);
        btn_nick_clear = (Button)findViewById(R.id.btn_nick_clear);
        btn_profile = (Button)findViewById(R.id.btn_profile);

        waitingDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("잠시만 기다려주세요")
                .setContext(this)
                .build();

        if(user.getPhotoUrl() != null){
            Glide.with(ProfilemodifyActivity.this)
                    .load(user.getPhotoUrl())
                    .into(image_profile);
            //image_profile.setImageURI(user.getPhotoUrl());
            photoUri = user.getPhotoUrl();
        }
        image_profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //카메라, 갤러리, 프로필 삭제를 선택할 수 있는 다이얼로그 띄움
                showDialog();
            }
        });

        if(user.getDisplayName() != null){
            et_nick.setText(user.getDisplayName());
        }
        btn_nick_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_nick.setText("");
                et_nick.requestFocus();
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(et_nick.getText().toString().length() == 0){
                    Log.d("profile", "닉네임 공백");
                    Toast.makeText(ProfilemodifyActivity.this, "사용할 닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                    et_nick.requestFocus();
                }
                else {

                    waitingDialog.show();
                    uploadFirebase();

                }
            }
        });

    }

    public boolean hasPermissions(Context context, String... permissions){
        if(context != null && permissions != null){
            for (String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    private void showDialog(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfilemodifyActivity.this);

        bottomSheetDialog.setContentView(R.layout.camera_gallery_dialog);
        bottomSheetDialog.show();

        final Button btn_camera_go = (Button)bottomSheetDialog.findViewById(R.id.btn_camera_go);
        final Button btn_gallery_go = (Button)bottomSheetDialog.findViewById(R.id.btn_gallery_go);
        final Button btn_img_delete = (Button)bottomSheetDialog.findViewById(R.id.btn_img_delete);

        btn_camera_go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //권한 설정되어 있는지 먼저 확인
                //권한이 허용되어있지 않다면 권한요청
                if(!hasPermissions(ProfilemodifyActivity.this, PERMISSIONS_CAMERA)){
                    ActivityCompat.requestPermissions(ProfilemodifyActivity.this, PERMISSIONS_CAMERA, MULTIPLE_PERMISSIONS);
                }

                //권한이 허용되어 있다면 다음 화면 진행
                else{
                    bottomSheetDialog.dismiss();
                    takePhoto();
                }
            }
        });

        btn_gallery_go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //권한 설정되어 있는지 먼저 확인
                //권한이 허용되어있지 않다면 권한요청
                if(!hasPermissions(ProfilemodifyActivity.this, PERMISSIONS_GALLERY)){
                    ActivityCompat.requestPermissions(ProfilemodifyActivity.this, PERMISSIONS_GALLERY, MULTIPLE_PERMISSIONS);
                }

                //권한이 허용되어 있다면 다음 화면 진행
                else{
                    bottomSheetDialog.dismiss();
                    gotoGallery();
                }
            }
        });

        btn_img_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                image_profile.setImageResource(R.drawable.mandoo_profile);
                photoUri = null;
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void takePhoto(){
        //사진을 찍기 위하여 설정합니다.
        Log.d("이미지", "takePhoto()함수 실행");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            //Toast.makeText(ProfilemodifyActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if(photoFile != null){
            //photoUri = Uri.fromFile(photoFile);
            Log.d("이미지", "photoFile != null");
            photoUri = FileProvider.getUriForFile(ProfilemodifyActivity.this,
                    "com.example.eattw.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
            Log.d("이미지", "photoUri에 집어넣음");
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        Log.d("이미지", "createImageFile()함수 실행");

        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP"+timeStamp+"_";

        // 이미지가 저장될 폴더 이름
        // test라는 경로에 이미지를 저장하기 위함
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/test/");
        if(!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        Log.d("이미지", "createImageFile()함수 종료");
        return image;
    }

    private void gotoGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            //Toast.makeText(ProfilemodifyActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == PICK_FROM_GALLERY) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(ProfilemodifyActivity.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        } else if (requestCode == CROP_FROM_CAMERA) {
            try {
                // bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 256, 256);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축


                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.

                image_profile.setImageBitmap(thumbImage);
                Log.d("이미지 경로(thumbImage)", thumbImage.toString());

                photochanged = true;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }

    //모든 작업에 있어 사전에 FALG_GRANT_WRITE_URI_PERMISSION과 READ 퍼미션을 줘야 uri를 활용한 작업에 지장을 받지 않는다는 것이 핵심입니다.
    public void cropImage() {
        Log.d("TestImageUpload","cropImage() 진입");
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Log.d("TestImageUpload","cropImage()의 if(size==0){} 진입");
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d("TestImageUpload","cropImage()의 if(size==0){}에 대한 else{} 진입");
            //Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 4);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(ProfilemodifyActivity.this,
                    "com.example.eattw.provider", tempFile);

            Log.d("TestImageUpload","photoUri"+photoUri.toString());

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(res.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);

        }
    }

    private void uploadFirebase(){
        //파이어베이스 업로드
        // Create a storage reference from our app
        Log.d("TestImageUpload","uploadFirebase() 진입");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

        if(photoUri == null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(et_nick.getText().toString())
                    .setPhotoUri(null)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        waitingDialog.dismiss();
                        Log.d("profile", "User profile updated.");
                        finish();
                    }
                }
            });
        }
        else {
            if(photochanged != false) {
                Uri file = photoUri;
                Log.d("TestImageUpload", "Uri file : " + file.toString());
                Log.d("TestImageUpload", "file.getLastPathSegment() : " + file.getLastPathSegment());
                final StorageReference riversRef = storageRef.child("images/" + user.getUid() + "/" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

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
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(et_nick.getText().toString())
                                    .setPhotoUri(downloadUri)
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        waitingDialog.dismiss();
                                        Log.d("profile", "User profile updated.");
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                            waitingDialog.dismiss();
                            Toast.makeText(ProfilemodifyActivity.this, "프로필 설정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("TestImageDownload", "실패");
                        }
                    }
                });
            }
            else{
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(et_nick.getText().toString())
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            waitingDialog.dismiss();
                            Log.d("profile", "User profile updated.");
                            finish();
                        }
                    }
                });
            }
        }
    }
}