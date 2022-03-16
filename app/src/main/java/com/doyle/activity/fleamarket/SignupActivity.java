package com.doyle.activity.fleamarket;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.util.GetMD5;
import com.doyle.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText ed_name;
    private EditText ed_pwd;
    private EditText ed_repwd;
    private EditText ed_nickname;
    private EditText ed_tel;

    private TextView tx_tel;
    private TextView tx_nickname;
    private View tx_name;
    private View tx_pwd;
    private View tx_repwd;
    private TextView backlogin;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        pd = new ProgressDialog(SignupActivity.this);
        pd.setMessage("注册中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        ed_name = findViewById(R.id.name);
        ed_pwd = findViewById(R.id.pwd);
        ed_repwd = findViewById(R.id.repwd);
        ed_nickname = findViewById(R.id.nickname);
        ed_tel = findViewById(R.id.tel);

        tx_name = findViewById(R.id.name_tx);
        tx_pwd = findViewById(R.id.pwd_tx);
        tx_repwd = findViewById(R.id.repwd_tx);
        tx_nickname = findViewById(R.id.nickname_tx);
        tx_tel = findViewById(R.id.tel_tx);
        backlogin = findViewById(R.id.backlogin);

        ed_name.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(ed_name.getText().toString().trim().length()<6)
                        tx_name.setVisibility(View.VISIBLE);
                    else   tx_name.setVisibility(View.INVISIBLE);

                } else {
                    // 此处为失去焦点时的处理内容
                    if(ed_name.getText().toString().trim().length()<6)
                        tx_name.setVisibility(View.VISIBLE);
                    else   tx_name.setVisibility(View.INVISIBLE);
                }
            }
        });

        ed_pwd.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(ed_pwd.getText().toString().trim().length()<6)
                        tx_pwd.setVisibility(View.VISIBLE);
                    else tx_pwd.setVisibility(View.INVISIBLE);

                } else {
                    // 此处为失去焦点时的处理内容
                    if(ed_pwd.getText().toString().trim().length()<6)
                        tx_pwd.setVisibility(View.VISIBLE);
                    else  tx_pwd.setVisibility(View.INVISIBLE);
                }
            }
        });

        ed_repwd.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(ed_repwd.getText().toString().equals(ed_pwd.getText().toString()))
                        tx_repwd.setVisibility(View.INVISIBLE);

                    else   tx_repwd.setVisibility(View.VISIBLE);


                } else {
                    // 此处为失去焦点时的处理内容
                    if(ed_repwd.getText().toString().equals(ed_pwd.getText().toString()))
                        tx_repwd.setVisibility(View.INVISIBLE);
                    else tx_repwd.setVisibility(View.VISIBLE);
                }
            }
        });

        ed_nickname.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(ed_nickname.getText().toString().trim().isEmpty()) tx_nickname.setVisibility(View.VISIBLE);
                    else  tx_nickname.setVisibility(View.INVISIBLE);

                } else {
                    // 此处为失去焦点时的处理内容
                    if(ed_nickname.getText().toString().trim().isEmpty()) tx_nickname.setVisibility(View.VISIBLE);
                    else   tx_nickname.setVisibility(View.INVISIBLE);
                }
            }
        });
        ed_tel.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(ed_tel.getText().toString().trim().isEmpty()) tx_tel.setVisibility(View.VISIBLE);
                    else tx_tel.setVisibility(View.INVISIBLE);
                } else {
                    // 此处为失去焦点时的处理内容
                    if(ed_tel.getText().toString().trim().isEmpty()) tx_tel.setVisibility(View.VISIBLE);
                    else tx_tel.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(msg.obj.toString().trim().equals("1"))
                    {
                        pd.cancel();
                        Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else if(msg.obj.toString().trim().equals("0"))
                    {
                        pd.cancel();
                        Toast.makeText(SignupActivity.this, "用户名已被注册", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        pd.cancel();
                        Toast.makeText(SignupActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public  void signup(View v)
    {
        if(ed_name.getText().toString().trim().length()<6||ed_pwd.getText().toString().trim().length()<6||ed_nickname.getText().toString().trim().isEmpty()||ed_tel.getText().toString().trim().isEmpty()||ed_pwd.getText().toString().equals(ed_repwd.getText().toString())==false)
            Toast.makeText(SignupActivity.this,"信息不符合规范！",Toast.LENGTH_SHORT).show();
        else {
            pd.show();
            new Thread() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", ed_name.getText().toString());
                    params.put("pwd", GetMD5.GetMD5(ed_pwd.getText().toString()));
                    params.put("nickname", ed_nickname.getText().toString().trim());
                    params.put("tel", ed_tel.getText().toString());

                    String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_Signup.action";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);

                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            }.start();
        }
    }
    public  void  backlogin(View view)
    {
        this.finish();
    }

}
