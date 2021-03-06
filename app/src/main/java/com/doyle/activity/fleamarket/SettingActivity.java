package com.doyle.activity.fleamarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.doyle.Fragment.F5Fragment;
import com.doyle.util.GetMD5;
import com.doyle.util.HttpUtils;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.os.LocaleListCompat.create;

public class SettingActivity extends AppCompatActivity {
    LinearLayout aboutus,version,changepwd;
    Button logout;
    private OnClickListener listener;
    private int uid;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initview();
    }
    private  void initview()
    {
        aboutus = findViewById(R.id.aboutus);
        version = findViewById(R.id.version);
        changepwd = findViewById(R.id.changepwd);
        logout = findViewById(R.id.logout);
        listener = new OnClickListener();

        changepwd.setOnClickListener(listener);
        aboutus.setOnClickListener(listener);
        changepwd.setOnClickListener(listener);
        logout.setOnClickListener(listener);
        version.setOnClickListener(listener);

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);
        pwd = pref.getString("pwd","");
    }
    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.logout:      //??????
                    setLogout();
                    break;
                case R.id.changepwd:      //?????????
                    setChangepwd();
                    break;
                case R.id.aboutus:      //????????????
                    setAboutus();
                    break;
                case R.id.version:      //????????????
                    setVersion();
                    break;
                default:
                    break;
            }
        }
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(SettingActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    else if (msg.obj.toString().trim().equals("1"))
                        Toast.makeText(SettingActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(SettingActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    public void setLogout()
    {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("??????")
                .setMessage("?????????????????????")
                .setPositiveButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = SettingActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("login",false);
                        editor.commit();
                        Intent i = new Intent(SettingActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                })
                .setNegativeButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//??????????????????
                    }
                }).create();
        alert.show();
    }
    public void setChangepwd()
    {
        final LinearLayout changepwd_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_changepwd,null);
        new AlertDialog.Builder(this)
                .setTitle("????????????")
                .setView(changepwd_layout)
                .setPositiveButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                   EditText oldpwd = changepwd_layout.findViewById(R.id.oldpwd);
                   final EditText newpwd = changepwd_layout.findViewById(R.id.newpwd);
                   EditText renewpwd = changepwd_layout.findViewById(R.id.renewpwd);
                   if(oldpwd.getText().toString().trim().isEmpty()||newpwd.getText().toString().trim().isEmpty()||renewpwd.getText().toString().trim().isEmpty())
                   Toast.makeText(SettingActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
                   else if(!GetMD5.GetMD5(oldpwd.getText().toString().trim()).equals(pwd))
                   Toast.makeText(SettingActivity.this,"????????????????????????",Toast.LENGTH_SHORT).show();
                   else if(newpwd.getText().toString().trim().length()<6)
                   Toast.makeText(SettingActivity.this,"????????????????????????",Toast.LENGTH_SHORT).show();
                   else if(!renewpwd.getText().toString().trim().equals(newpwd.getText().toString().trim()))
                   Toast.makeText(SettingActivity.this,"????????????????????????",Toast.LENGTH_SHORT).show();
                   else
                    {
                          new Thread() {
                                    @Override
                                    public void run() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("uid",Integer.toString(uid));
                                        params.put("pwd",GetMD5.GetMD5(newpwd.getText().toString().trim()));
                                        String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_Updatepwd.action";
                                        String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                        System.out.println("????????????" + Result);
                                        Message message = new Message();
                                        message.what = 0;
                                        if(!Result.trim().equals("-1"))
                                        {
                                            try {
                                                JSONObject json = new JSONObject(Result.trim());
                                                int back = json.getInt("code");
                                                if(back==1)
                                                {
                                                    message.obj = 1;
                                                    SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("pwd",GetMD5.GetMD5(newpwd.getText().toString().trim()));
                                                    editor.commit();
                                                    pwd = GetMD5.GetMD5(newpwd.getText().toString().trim());
                                                }
                                                else message.obj=0;
                                            }catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                        else message.obj = -1;
                                        handler.sendMessage(message);
                                    }
                                }.start();
                        }
                    }
                })
                .setNegativeButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

    }
    public void setAboutus()
    {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("?????????")
                .setMessage(
                        "??????????????????????????????SSH??????????????????\n"+
                        "???Android????????????\n" +
                        "???C#??????.netFormwork??????????????????\n" +
                        "?????????Doyle_Ning\n" +
                        "?????????3057995\n" +
                        "?????????2018???2???")
                .setPositiveButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//??????????????????
                    }
                }).create();
        alert.show();
    }
    public void setVersion()
    {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("????????????")
                .setMessage("version???1.0.1\n" +
                        "?????????????????????:\n.????????????" )
                .setPositiveButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//??????????????????
                    }
                }).create();
        alert.show();
    }
}
