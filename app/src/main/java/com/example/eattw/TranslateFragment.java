package com.example.eattw;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class TranslateFragment  extends Fragment {

    private View view;
    private Button btn_picture_go;

    private Button input_language;
    private ImageButton swap_language;
    private Button output_language;

    private LinearLayout input_spinner;
    private Button btn_input_korean;
    private Button btn_input_chinese;
    private Button btn_input_english;

    private LinearLayout output_spinner;
    private Button btn_output_korean;
    private Button btn_output_chinese;
    private Button btn_output_english;

    private ImageButton input_pronounce;
    private ImageButton input_clear;

    private EditText input_translate;

    private ImageButton output_pronounce;
    private ImageButton output_save;
    private ImageButton output_copy;

    private TextView output_translate;

    String sourceText = null;
    String input;
    String output;
    String input_code;
    String output_code;

    public TextToSpeech textToSpeechKorean;
    public TextToSpeech textToSpeechChinese;
    public TextToSpeech textToSpeechEnglish;

    class TranslateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String clientId = "Jd6jt_tX6ZCbOWzKhXn7";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "fgW_AQyxd8";//애플리케이션 클라이언트 시크릿값";
            try {
                //번역문을 UTF-8으로 인코딩합니다.
                String text = URLEncoder.encode(input_translate.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

                //파파고 API와 연결을 수행합니다.
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                //번역할 문장을 파라미터로 전송합니다.
                //String postParams = "source="+input_code+"&target="+output_code+"&text=" + text;
                Log.d("Cooode1", input_code+output_code);
                String postParams = "source="+input_code+"&target="+output_code+"&text=" + text;

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
                output_translate.setText(s);
            } catch (Exception e) {
                Log.e("SampleHTTP", "Exception in processing response.", e);
                e.printStackTrace();
            }
            return null;
        }
    }

   @Nullable
    @Override
    //Fragment는 onCreateView로 생성
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_translate, container, false);


        btn_picture_go = (Button)view.findViewById(R.id.btn_picture_go);
        btn_picture_go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), PicActivity.class);
                startActivity(intent);
            }
        });

        input_spinner = (LinearLayout)view.findViewById(R.id.input_spinner);
        output_spinner = (LinearLayout)view.findViewById(R.id.output_spinner);

        input_language = (Button)view.findViewById(R.id.input_language);
        input_language.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_spinner.setVisibility(View.VISIBLE);
            }
        });

        btn_input_korean = (Button)view.findViewById(R.id.btn_input_korean);
        btn_input_korean.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_spinner.setVisibility(View.GONE);
                input_language.setText("한국어");
                input_code = "ko";
            }
        });
        btn_input_chinese = (Button)view.findViewById(R.id.btn_input_chinese);
        btn_input_chinese.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_spinner.setVisibility(View.GONE);
                input_language.setText("중국어");
                input_code = "zh-TW";
            }
        });
        btn_input_english = (Button)view.findViewById(R.id.btn_input_english);
        btn_input_english.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_spinner.setVisibility(View.GONE);
                input_language.setText("영어");
                input_code = "en";
            }
        });

        swap_language = (ImageButton)view.findViewById(R.id.swap_language);
        swap_language.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input = input_language.getText().toString();
                output = output_language.getText().toString();
                input_language.setText(output);
                output_language.setText(input);
                input = input_language.getText().toString();
                output = output_language.getText().toString();
            }
        });
        output_language = (Button)view.findViewById(R.id.output_language);
        output_language.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output_spinner.setVisibility(View.VISIBLE);
            }
        });

        btn_output_korean = (Button)view.findViewById(R.id.btn_output_korean);
        btn_output_korean.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output_spinner.setVisibility(View.GONE);
                output_language.setText("한국어");
                output_code = "ko";
            }
        });
        btn_output_chinese = (Button)view.findViewById(R.id.btn_output_chinese);
        btn_output_chinese.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output_spinner.setVisibility(View.GONE);
                output_language.setText("중국어");
                output_code = "zh-TW";
            }
        });
        btn_output_english = (Button)view.findViewById(R.id.btn_output_english);
        btn_output_english.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output_spinner.setVisibility(View.GONE);
                output_language.setText("영어");
                output_code = "en";
            }
        });

        input_clear = (ImageButton)view.findViewById(R.id.input_clear);
        input_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_translate.setText("");
            }
        });

        input_translate = (EditText)view.findViewById(R.id.input_translate);
        input_translate.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input_translate.setRawInputType(InputType.TYPE_CLASS_TEXT);
        input_translate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sourceText = input_translate.getText().toString();
                input = input_language.getText().toString();
                output = output_language.getText().toString();
                Log.d("Cooode3", input + output);
                if(input.equals("한국어")){
                    input_code = "ko";
                }
                else if(input.equals("중국어")){
                    input_code = "zh-TW";
                }
                else if(input.equals("영어")){
                    input_code = "en";
                }

                if(output.equals("한국어")){
                    output_code = "ko";
                }
                else if(output.equals("중국어")){
                    output_code = "zh-TW";
                }
                else if(output.equals("영어")){
                    output_code = "en";
                }
                //getLanguageCode();
                if(input_code == output_code){
                    output_translate.setText(sourceText);
                }
                else {
                    new TranslateTask().execute();
                }
                return false;
            }
        });

       output_copy = (ImageButton)view.findViewById(R.id.btn_copy);
       output_copy.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               if(output_translate.getText() != "" | output_translate.getText() != null) {
                   //클립보드 사용 코드
                   ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                   ClipData clipData = ClipData.newPlainText("Translate", output_translate.getText());
                   clipboardManager.setPrimaryClip(clipData);

                   //복사가 되었다면 토스트메시지 노출
                   Toast.makeText(getContext(), "복사되었습니다.", Toast.LENGTH_SHORT).show();
               }

           }
       });

       output_translate = (TextView)view.findViewById(R.id.output_translate);

       input_pronounce = (ImageButton)view.findViewById(R.id.btn_inputpronounce);
       input_pronounce.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               speech(input_translate.getText().toString(), input_language.getText().toString());
               Log.d("발음", "발음할 텍스트 " + input_translate.getText().toString());
           }
       });

       output_pronounce = (ImageButton)view.findViewById(R.id.btn_outputpronounce);
       output_pronounce.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               speech(output_translate.getText().toString(), output_language.getText().toString());
               Log.d("발음", "발음할 텍스트 " + output_translate.getText().toString());
           }
       });

       textToSpeechKorean = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
           @Override
           public void onInit(int status) {
               if (status == TextToSpeech.SUCCESS) {
                   //사용할 언어를 설정
                   int result = textToSpeechKorean.setLanguage(Locale.KOREAN);
                   //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                   if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                       Toast.makeText(getContext(), "지원하지 않는 언어입니다1.", Toast.LENGTH_SHORT).show();
                   }
               }
           }
       });

       textToSpeechChinese = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
           @Override
           public void onInit(int status) {
               if (status == TextToSpeech.SUCCESS) {
                   //사용할 언어를 설정
                   int result = textToSpeechChinese.setLanguage(Locale.TRADITIONAL_CHINESE);
                   //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                   if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                       //Toast.makeText(getContext(), "지원하지 않는 언어입니다2.", Toast.LENGTH_SHORT).show();
                   }
               }
           }
       });

       textToSpeechEnglish = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
           @Override
           public void onInit(int status) {
               if (status == TextToSpeech.SUCCESS) {
                   //사용할 언어를 설정
                   int result = textToSpeechEnglish.setLanguage(Locale.ENGLISH);
                   //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                   if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                       Toast.makeText(getContext(), "지원하지 않는 언어입니다3.", Toast.LENGTH_SHORT).show();
                   }
               }
           }
       });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeechKorean != null) {
            textToSpeechKorean.stop();
            textToSpeechKorean.shutdown();
        }
        else if(textToSpeechEnglish != null){
            textToSpeechEnglish.stop();
            textToSpeechEnglish.shutdown();
        }
        else if(textToSpeechChinese != null){
            textToSpeechChinese.stop();
            textToSpeechChinese.shutdown();
        }
    }

    private void speech(String text, String language){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (language.equals("한국어")) {
                textToSpeechKorean.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else if (language.equals("중국어")) {
                textToSpeechChinese.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else if (language.equals("영어")) {
                textToSpeechEnglish.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
        else {
            if (language.equals("한국어")) {
                textToSpeechKorean.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else if (language.equals("중국어")) {
                textToSpeechChinese.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else if (language.equals("영어")) {
                textToSpeechEnglish.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    private void translateText(int langCode_i, int langCode_o){
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(langCode_i)
                .setTargetLanguage(langCode_o)
                .build();

        final FirebaseTranslator translator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        output_translate.setText(s);
                    }
                });
            }
        });
    }

    private void getLanguageCode(){
        int langCode_i;
        int langCode_o;

        switch(input){
            case "한국어" :
                langCode_i = FirebaseTranslateLanguage.KO;
                break;
            case "중국어" :
                langCode_i = FirebaseTranslateLanguage.ZH;
                break;
            case "영어" :
                langCode_i = FirebaseTranslateLanguage.EN;
                break;
            default:
                langCode_i = 0;
        }

        switch(output){
            case "한국어" :
                langCode_o = FirebaseTranslateLanguage.KO;
                break;
            case "중국어" :
                langCode_o = FirebaseTranslateLanguage.ZH;
                break;
            case "영어" :
                langCode_o = FirebaseTranslateLanguage.EN;
                break;
            default:
                langCode_o = 0;
        }

        translateText(langCode_i, langCode_o);
    }
}
