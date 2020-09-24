package com.example.eattw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.eattw.Helper.GraphicOverlay;
import com.example.eattw.Helper.TextGraphic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PicActivity extends AppCompatActivity {

    CameraView cameraView;
    AlertDialog waitingDialog;
    GraphicOverlay graphicOverlay;
    Button btn_Capture;
    Button btn_ReCapture;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        waitingDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("잠시만 기다려주세요")
                .setContext(this)
                .build();

        cameraView = (CameraView)findViewById(R.id.camera_view);
        graphicOverlay = (GraphicOverlay)findViewById(R.id.graphic_overlay);
        btn_Capture = (Button)findViewById(R.id.btn_capture);
        btn_ReCapture = (Button)findViewById(R.id.btn_recapture);
        btn_Capture.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();

                btn_Capture.setVisibility(View.GONE);
                btn_ReCapture.setVisibility(View.VISIBLE);
            }
        });

        btn_ReCapture.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();

                btn_Capture.setVisibility(View.VISIBLE);
                btn_ReCapture.setVisibility(View.GONE);
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                recognizeText(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void recognizeText(Bitmap bitmap) {
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

        graphicOverlay.clear();

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
