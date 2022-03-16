package com.doyle.activity.fleamarket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;

import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                boolean state = pref.getBoolean("login",false);
                if(state)
                {
                    Intent i = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        },1500);
    }


}
