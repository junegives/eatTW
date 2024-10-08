package com.example.eattw.Account;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    //서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://mafio.ga/eatTW/Register.php";
    private Map<String, String> map;

    public RegisterRequest(String userID, String userPW, String userNick, String userPic, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPW", userPW);
        map.put("userNick", userNick);
        map.put("userPic", userPic);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
