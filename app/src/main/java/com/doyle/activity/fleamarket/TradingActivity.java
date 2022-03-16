package com.doyle.activity.fleamarket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.Fragment.F5Fragment;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TradingActivity extends AppCompatActivity {

    ProgressDialog pd;
    private int gid;

    Button fahuo,quxiao,shouhuo;
    ImageView image,uimage,gimage;
    TextView guname,uname,gtel,utel,gaddress,uaddress,gname,price;

    private OnClickListener listener;
    private String zhuangtai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading);
        Intent intent = getIntent();
        gid = intent.getIntExtra("goods",0);
        zhuangtai = intent.getStringExtra("zhuangtai");
        initview();
    }
    public void initview()
    {

        pd = new ProgressDialog(TradingActivity.this);
        pd.setMessage("等待中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        fahuo = findViewById(R.id.fahuo);
        quxiao= findViewById(R.id.quxiao);
        shouhuo = findViewById(R.id.shouhuo);
        image = findViewById(R.id.image);
        uimage = findViewById(R.id.uimage);
        gimage = findViewById(R.id.gimage);
        guname = findViewById(R.id.guname);
        uname = findViewById(R.id.uname);
        gtel = findViewById(R.id.gtel);
        utel = findViewById(R.id.utel);
        gaddress = findViewById(R.id.gaddress);
        uaddress = findViewById(R.id.uaddress);
        gname = findViewById(R.id.gname);
        price = findViewById(R.id.price);
        listener = new OnClickListener();
        if(zhuangtai.equals("daifahuo"))
        {
            getdata(1);
            fahuo.setVisibility(View.INVISIBLE);
            shouhuo.setVisibility(View.INVISIBLE);
        }
        else if(zhuangtai.equals("daishouhuo"))
        {
            getdata(2);
            quxiao.setVisibility(View.INVISIBLE);
            fahuo.setVisibility(View.INVISIBLE);
        }
        else if(zhuangtai.equals("jiaoyizhong"))
        {
            getdata(1);
            shouhuo.setVisibility(View.INVISIBLE);
        }
        else if(zhuangtai.equals("chenggong"))
        {
            getdata(3);
            quxiao.setVisibility(View.INVISIBLE);
            fahuo.setVisibility(View.INVISIBLE);
            shouhuo.setVisibility(View.INVISIBLE);
        }
        else if(zhuangtai.equals("shibai"))
        {
            getdata(4);
            quxiao.setVisibility(View.INVISIBLE);
            fahuo.setVisibility(View.INVISIBLE);
            shouhuo.setVisibility(View.INVISIBLE);
        }


        shouhuo.setOnClickListener(listener);
        fahuo.setOnClickListener(listener);
        quxiao.setOnClickListener(listener);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(TradingActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back ==1)
                            {
                                JSONObject guser = new JSONObject(json.getString("guser"));
                                JSONObject user = new JSONObject(json.getString("user"));
                                JSONObject goods = new JSONObject(json.getString("goods"));
                                guname.setText(guser.getString("nickname"));
                                gtel.setText(guser.getString("tel"));
                                gaddress.setText(guser.getString("address"));

                                uname.setText(user.getString("nickname"));
                                utel.setText(user.getString("tel"));
                                uaddress.setText(user.getString("address"));

                                gname.setText(goods.getString("gname"));
                                price.setText(Double.toString(goods.getDouble("price")));

                                downLoad(getResources().getString(R.string.burl)+guser.getString("image"),1);
                                downLoad(getResources().getString(R.string.burl)+goods.getString("imageurl"),2);
                                downLoad(getResources().getString(R.string.burl)+user.getString("image"),3);
                            }
                            else
                            {
                                Toast.makeText(TradingActivity.this,"订单已失效",Toast.LENGTH_SHORT).show();
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
                        if(msg.arg1==1)
                        gimage.setImageBitmap(bitmap);
                        else if(msg.arg1==2)
                        image.setImageBitmap(bitmap);
                        else uimage.setImageBitmap(bitmap);
                    }
                    break;
                case 2:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(TradingActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else {
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back ==1)
                            {
                                Toast.makeText(TradingActivity.this,"发货成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(TradingActivity.this,"订单已失效",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(TradingActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back ==1) {
                                Toast.makeText(TradingActivity.this,"订单已取消",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(TradingActivity.this,"订单已失效",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 4:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(TradingActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back ==1) {
                                Toast.makeText(TradingActivity.this,"收货成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(TradingActivity.this,"订单已失效",Toast.LENGTH_SHORT).show();
                                finish();
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
    public void getdata(final int state)
    {
        pd.show();
        new Thread() {
            public void run(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("gid",Integer.toString(gid));
                params.put("state",Integer.toString(state));
                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Findtrade.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    /*
  下载图片
 */
    private  void downLoad(final String path,final int state)
    {
        new Thread() {
            @Override
            public void run() {
                String back =  HttpUtils.getInputStream(path);
                Message message = new Message();
                message.what = 1;
                message.arg1 = state;
                message.obj = back;
                handler.sendMessage(message);
            }
        }.start();
    }
    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fahuo:      //确认发货
                    fahuo();
                    break;
                case R.id.quxiao:      //取消订单
                    quxiao();
                    break;
                case R.id.shouhuo:      //确认收货
                    shouhuo();
                    break;
                default:
                    break;
            }
        }
    }
    public void fahuo()
    {
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TradingActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否确认发货？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        new Thread() {
                            public void run(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("gid",Integer.toString(gid));
                                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Fahuo.action";
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
    public void quxiao()
    {
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TradingActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否取消订单?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        new Thread() {
                            public void run(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("gid",Integer.toString(gid));
                                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Quxiao.action";
                                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                System.out.println("结果为：" + Result);
                                Message message = new Message();
                                message.what = 3;
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
    public void shouhuo()
    {

        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TradingActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否确认收货?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        new Thread() {
                            public void run(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("gid",Integer.toString(gid));
                                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Shouhuo.action";
                                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                System.out.println("结果为：" + Result);
                                Message message = new Message();
                                message.what = 4;
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
