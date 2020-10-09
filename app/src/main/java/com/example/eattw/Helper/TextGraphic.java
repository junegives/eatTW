package com.example.eattw.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TextGraphic extends GraphicOverlay.Graphic{

    private static final int TEXT_COLOR = Color.BLACK;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint, textPaint;
    private final FirebaseVisionText.Element text;

    String langCode = null;
    RectF rect;
    Canvas canvas;

    public TextGraphic(GraphicOverlay overlay, FirebaseVisionText.Element text){
        super(overlay);

        this.text = text;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setFakeBoldText(true);

        postInvalidate();

    }

    @Override
    public void draw(Canvas canvas) {
        if(text == null)
        {
            throw new IllegalStateException("Attempting to draw a null text");
        }
        this.canvas = canvas;

        rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        //canvas.drawRect(rect, rectPaint);
        identifyLanguage();
    }

    private void identifyLanguage(){
        final String sourceText = text.getText();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und")){
                }
                else{
                    getLanguageCode(s);
                }
            }
        });
    }

    private void getLanguageCode(String s){
        switch (s){
            case "en":
                langCode = "en";
                break;
            case "zh":
                langCode = "zh-TW";
                break;
            default:
                langCode = null;
        }
        if(langCode != null) {
            new TranslateTask0().execute();
        }
    }

    class TranslateTask0 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("TestLog", "TranslateTask0 진입");
            String clientId = "Jd6jt_tX6ZCbOWzKhXn7";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "fgW_AQyxd8";//애플리케이션 클라이언트 시크릿값";
            try {
                //번역문을 UTF-8으로 인코딩합니다.
                String text_input = URLEncoder.encode(text.getText(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

                //파파고 API와 연결을 수행합니다.
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                //번역할 문장을 파라미터로 전송합니다.
                //String postParams = "source="+input_code+"&target="+output_code+"&text=" + text_input;
                String postParams = "source="+langCode+"&target=ko&text=" + text_input;

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();

                //번역 결과를 받아옴
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                String s = response.toString();
                s = s.split("\"")[27];
                Log.d("TraslateTask0", "s = " + s);
                canvas.drawText(s, rect.left, rect.bottom, textPaint);
                Log.d("TranslateTask0", "s = " + s + "rect.left = " + rect.left + "rect.bottom = " + rect.bottom);
            } catch (Exception e) {
                Log.e("SampleHTTP", "Exception in processing response.", e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
