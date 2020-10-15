package com.example.eattw.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.eattw.Helper.GraphicOverlay;
import com.example.eattw.Helper.TextGraphic;
import com.example.eattw.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ImgTranslateActivity extends AppCompatActivity {

    private ImageView imageView;
    private AlertDialog waitingDialog;
    private GraphicOverlay graphicOverlay;
    private ImageButton btn_ReCapture;

    //권한 요청을 할 때 발생하는 창에 대한 결과값을 받기 위해 지정해주는 정수
    private final int PERMISSION_CODE = 12;

    //요청할 권한을 배열로 저장 ( 카메라, 외부저장소 읽기, 외부저장소 쓰기)
    private String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };

    //onActivityResult를 위해 사용할 requestCode
    private static final int CAPTURE = 1;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_translate);

        //권한 설정되어 있는지 먼저 확인
        //권한이 허용되어있지 않다면 권한요청
        if(!hasPermissions(ImgTranslateActivity.this, PERMISSIONS_CAMERA)){
            ActivityCompat.requestPermissions(ImgTranslateActivity.this, PERMISSIONS_CAMERA, PERMISSION_CODE);
        }

        //권한이 허용되어 있다면 다음 화면 진행
        else{
            takePhoto();
        }

        waitingDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("잠시만 기다려주세요")
                .setContext(this)
                .build();

        imageView = (ImageView)findViewById(R.id.Image_view);
        graphicOverlay = (GraphicOverlay)findViewById(R.id.graphic_overlay);
        btn_ReCapture = (ImageButton)findViewById(R.id.btn_recapture);

        btn_ReCapture.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                graphicOverlay.clear();
                takePhoto();
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

    private void takePhoto(){
        //사진을 찍기 위하여 설정합니다.
        Log.d("이미지", "takePhoto()함수 실행");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            recognizeText(imageBitmap);
        }
    }

    private void recognizeText(Bitmap bitmap) {
        waitingDialog.show();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionCloudTextRecognizerOptions options =
                new FirebaseVisionCloudTextRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList("zh"))
                        .build();

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getCloudTextRecognizer(options);

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        drawTextResult(firebaseVisionText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EDMT_ERROR", e.getMessage());

            }
        });
    }

    private void drawTextResult(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        waitingDialog.dismiss();

        if(blocks.size() == 0)
        {
            Toast.makeText(this, "텍스트를 찾지 못하였습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        //graphicOverlay.clear();

        for(int i = 0; i < blocks.size(); i++)
        {
            Log.d("blocks.size()", blocks.size()+"");
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for(int j=0; j<lines.size(); j++)
            {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();

                for(int k = 0; k<elements.size(); k++)
                {
                    TextGraphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k));
                    graphicOverlay.add(textGraphic);
                    Log.d("textGraphic", textGraphic+"");
                }
            }
        }
    }
}
