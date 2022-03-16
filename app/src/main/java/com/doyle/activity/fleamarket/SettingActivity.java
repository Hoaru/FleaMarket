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
                case R.id.logout:      //注销
                    setLogout();
                    break;
                case R.id.changepwd:      //改密码
                    setChangepwd();
                    break;
                case R.id.aboutus:      //关于我们
                    setAboutus();
                    break;
                case R.id.version:      //版本信息
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
                        Toast.makeText(SettingActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    else if (msg.obj.toString().trim().equals("1"))
                        Toast.makeText(SettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(SettingActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    public void setLogout()
    {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("提示")
                .setMessage("确定要注销么？")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
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
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置取消按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//对话框关闭。
                    }
                }).create();
        alert.show();
    }
    public void setChangepwd()
    {
        final LinearLayout changepwd_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_changepwd,null);
        new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(changepwd_layout)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                   EditText oldpwd = changepwd_layout.findViewById(R.id.oldpwd);
                   final EditText newpwd = changepwd_layout.findViewById(R.id.newpwd);
                   EditText renewpwd = changepwd_layout.findViewById(R.id.renewpwd);
                   if(oldpwd.getText().toString().trim().isEmpty()||newpwd.getText().toString().trim().isEmpty()||renewpwd.getText().toString().trim().isEmpty())
                   Toast.makeText(SettingActivity.this,"有空值啊少年！",Toast.LENGTH_SHORT).show();
                   else if(!GetMD5.GetMD5(oldpwd.getText().toString().trim()).equals(pwd))
                   Toast.makeText(SettingActivity.this,"密码错了啊少年！",Toast.LENGTH_SHORT).show();
                   else if(newpwd.getText().toString().trim().length()<6)
                   Toast.makeText(SettingActivity.this,"密码短了啊少年！",Toast.LENGTH_SHORT).show();
                   else if(!renewpwd.getText().toString().trim().equals(newpwd.getText().toString().trim()))
                   Toast.makeText(SettingActivity.this,"密码不同啊少年！",Toast.LENGTH_SHORT).show();
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
                                        System.out.println("结果为：" + Result);
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
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

    }
    public void setAboutus()
    {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("关于我")
                .setMessage(
                        "这次毕业设计我采用了SSH框架做服务器\n"+
                        "用Android写客户端\n" +
                        "用C#基于.netFormwork框架写管理端\n" +
                        "作者：Doyle_Ning\n" +
                        "微信：3057995\n" +
                        "时间：2018年2月")
                .setPositiveButton("关闭",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//对话框关闭。
                    }
                }).create();
        alert.show();
    }
    public void setVersion()
    {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("版本信息")
                .setMessage("version：1.0.1\n" +
                        "此版本基本完成:\n.界面美化" )
                .setPositiveButton("关闭",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//对话框关闭。
                    }
                }).create();
        alert.show();
    }
}
