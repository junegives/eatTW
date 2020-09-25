package com.example.eattw;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;

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

    private TextView output_translate;

    String sourceText = null;
    String input = null;
    String output = null;

    @Nullable
    @Override
    //Fragment는 onCreateView로 생성
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_translate, container, false);


        btn_picture_go = (Button)view.findViewById(R.id.btn_picture_go);
        btn_picture_go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ImgTranslateActivity.class);
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
            }
        });
        btn_input_chinese = (Button)view.findViewById(R.id.btn_input_chinese);
        btn_input_chinese.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_spinner.setVisibility(View.GONE);
                input_language.setText("중국어");
            }
        });
        btn_input_english = (Button)view.findViewById(R.id.btn_input_english);
        btn_input_english.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_spinner.setVisibility(View.GONE);
                input_language.setText("영어");
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
            }
        });
        btn_output_chinese = (Button)view.findViewById(R.id.btn_output_chinese);
        btn_output_chinese.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output_spinner.setVisibility(View.GONE);
                output_language.setText("중국어");
            }
        });
        btn_output_english = (Button)view.findViewById(R.id.btn_output_english);
        btn_output_english.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output_spinner.setVisibility(View.GONE);
                output_language.setText("영어");
            }
        });

        input_pronounce = (ImageButton)view.findViewById(R.id.btn_inputpronounce);
        input_pronounce.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

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
                getLanguageCode();
                return false;
            }
        });

        output_pronounce = (ImageButton)view.findViewById(R.id.btn_outputpronounce);
        output_pronounce.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            }
        });
        output_save = (ImageButton)view.findViewById(R.id.btn_outputsave);
        output_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        output_translate = (TextView)view.findViewById(R.id.output_translate);

        return view;
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
