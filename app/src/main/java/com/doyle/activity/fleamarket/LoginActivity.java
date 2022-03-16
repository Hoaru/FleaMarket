package com.doyle.activity.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.doyle.util.GetMD5;
import com.doyle.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText ed_name;
    private EditText ed_pwd;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(LoginActivity.this);
        ed_name = findViewById(R.id.name);
        ed_pwd = findViewById(R.id.pwd);
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(LoginActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    int back=0;
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        pd.cancel();
                        Toast.makeText(LoginActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try{
                            JSONObject code = new JSONObject(msg.obj.toString().trim());
                            back = code.getInt("code");
                            if(back==0)
                            {
                                pd.cancel();
                                Toast.makeText(LoginActivity.this,"账号或密码错误！",Toast.LENGTH_SHORT).show();
                            }
                            else if(back==1)
                            {
                                JSONObject data = new JSONObject(code.getString("data"));
                                SharedPreferences pref = LoginActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putInt("uid",data.getInt("uid"));
                                editor.putString("name",data.getString("name"));
                                editor.putString("pwd",data.getString("pwd"));
                                editor.putString("image",data.getString("image"));
                                editor.putString("address",data.getString("address"));
                                editor.putFloat("balance",(float) data.getDouble("balance"));
                                editor.putInt("sex",data.getInt("sex"));
                                editor.putString("nickname",data.getString("nickname"));
                                editor.putString("tel",data.getString("tel"));
                                editor.putInt("vip",data.getInt("vip"));
                                editor.putInt("state",data.getInt("state"));
                                editor.putBoolean("login",true);
                                editor.commit();

                                pd.cancel();
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public void L_signup(View v)
    {
        Intent i = new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(i);
    }
    public void L_login(View v)
    {
        if(ed_name.getText().toString().trim().isEmpty())
        {
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
        else if(ed_pwd.getText().toString().trim().isEmpty())
        {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
        else
        {
            pd.setMessage("登陆中...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            new Thread() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", ed_name.getText().toString());
                    params.put("pwd", GetMD5.GetMD5(ed_pwd.getText().toString()));


                    String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_Login.action";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);

                    Message message = new Message();
                    message.what = 2;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            }.start();
        }
    }

}
