package com.doyle.activity.fleamarket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.adapter.CharitylistAdapter;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CharitydetailActivity extends AppCompatActivity {
    int cid;
    int uid;
    ProgressDialog pd;
    ImageView image;
    TextView title,content,need,scannum,joinnum,address,tel,uname,time,endtime;
    Button join;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charitydetail);
        Intent intent = getIntent();
        cid = intent.getIntExtra("cid",0);
        initview();
        getdata();
    }
    public void initview()
    {
        SharedPreferences pref = CharitydetailActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        need = findViewById(R.id.need);
        scannum = findViewById(R.id.scannum);
        join = findViewById(R.id.join);
        joinnum = findViewById(R.id.joinnum);
        address = findViewById(R.id.address);
        tel = findViewById(R.id.tel);
        uname = findViewById(R.id.uname);
        time = findViewById(R.id.time);
        endtime = findViewById(R.id.endtime);

        pd = new ProgressDialog(CharitydetailActivity.this);
        pd.setMessage("发布中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
    }
    public void getdata()
    {
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cid",Integer.toString(cid));
                String strUrlpath = getResources().getString(R.string.burl) + "CharityAction_Findbyid.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(CharitydetailActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                JSONObject data = new JSONObject(json.getString("data"));
                                JSONObject user = new JSONObject(json.getString("user"));
                                content.setText("    "+data.getString("content"));
                                title.setText(data.getString("name"));
                                need.setText(data.getString("need"));
                                time.setText(data.getString("time"));
                                endtime.setText(data.getString("endtime"));
                                scannum.setText(Integer.toString(data.getInt("scannum")));
                                joinnum.setText(Integer.toString(data.getInt("joinnum")));
                                uname.setText(user.getString("nickname"));
                                address.setText(user.getString("address"));
                                tel.setText(user.getString("tel"));
                                downLoad(getResources().getString(R.string.burl)+data.getString("imageurl"));

                            }
                            else
                            {
                                Toast.makeText(CharitydetailActivity.this,"该项目已经失效！",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 1:
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        Bitmap bitmap = ImageDeal.String2Bitmap(msg.obj.toString());
                        image.setImageBitmap(bitmap);
                    }
                    break;
                case 2:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(CharitydetailActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                Toast.makeText(CharitydetailActivity.this,"申请成功,你也可以主动联系哦！",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CharitydetailActivity.this,"你已经加入过或该项目已失效！",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    };
    /*
  下载图片
 */
    private  void downLoad(final String path)
    {
        new Thread() {
            @Override
            public void run() {
                String back =  HttpUtils.getInputStream(path);
                Message message = new Message();
                message.what = 1;
                message.obj = back;
                handler.sendMessage(message);
            }
        }.start();
    }
    public void setJoin(View view)
    {
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CharitydetailActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("我们会将你的联系信息发送给发起者,你确定加入该公益项目吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        new Thread() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("uid",Integer.toString(uid));
                                params.put("cid",Integer.toString(cid));
                                String strUrlpath = getResources().getString(R.string.burl) + "CharityAction_Update.action";
                                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                System.out.println("结果为：" + Result);
                                Message message = new Message();
                                message.what = 2;
                                message.obj = Result;
                                handler.sendMessage(message);
                            }
                        }.start();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
}
