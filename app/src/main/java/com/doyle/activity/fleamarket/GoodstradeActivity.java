package com.doyle.activity.fleamarket;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.adapter.ConmentslistAdapter;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodstradeActivity extends AppCompatActivity {
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
     ProgressDialog pd;
    LinearLayout layout_back,pinglun_layout;
    private int uid;
    private int gid;
    private String state="1";
    private int guid;
    private String tel;
    private Double jiage;//价格
    private float balance;

    private OnClickListener listener;
    ImageView touxiang,image,share;
    Button commentbtn,lianxi,buy;
    TextView nickname,time,address,gname,
            price,oprice,hownew,getway,type,scannum,detail,commentnum;
    EditText commentedt;
    ListView commentlist;

    Bitmap goodsbitmap,welcomebitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodstrade);
        Intent intent = getIntent();
        gid = intent.getIntExtra("gid",0);
        state = intent.getStringExtra("state");
        initview();
    }
    public void initview()
    {
        pinglun_layout=findViewById(R.id.pinglun_layout);
        if(!state.trim().equals("1"))
            pinglun_layout.setVisibility(View.INVISIBLE);


        layout_back = findViewById(R.id.layout_back);
        share = findViewById(R.id.share);
        touxiang = findViewById(R.id.touxiang);
        image = findViewById(R.id.image);

        commentbtn= findViewById(R.id.commentbtn);
        lianxi = findViewById(R.id.lianxi);
        buy = findViewById(R.id.buy);
        nickname = findViewById(R.id.nickname);
        time = findViewById(R.id.time);
        address = findViewById(R.id.address);
        gname = findViewById(R.id.gname);
        price = findViewById(R.id.price);
        oprice = findViewById(R.id.oprice);
        hownew = findViewById(R.id.hownew);
        getway = findViewById(R.id.getway);
        type = findViewById(R.id.type);
        scannum = findViewById(R.id.scannum);
        detail = findViewById(R.id.detail);
        commentnum = findViewById(R.id.commentnum);
        commentedt = findViewById(R.id.commentedt);
        commentlist = findViewById(R.id.commentlist);

        listener = new OnClickListener();
        lianxi.setOnClickListener(listener);
        buy.setOnClickListener(listener);
        commentbtn.setOnClickListener(listener);
        share.setOnClickListener(listener);

        pd = new ProgressDialog(GoodstradeActivity.this);
        pd.setMessage("下单中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        getdata();
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://获取数据
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(GoodstradeActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back ==1)
                            {
                                JSONObject date = new JSONObject(json.getString("date"));
                                JSONObject user = new JSONObject(json.getString("user"));
                                String con = json.getString("con");
                                if(con.trim().isEmpty())
                                {
                                    commentnum.setText("(0)");
                                }
                                else
                                {
                                    layout_back.setVisibility(View.INVISIBLE);

                                    JSONArray conmentslist = new JSONArray(json.getString("con"));
                                    JSONArray conuserlist = new JSONArray(json.getString("conuser"));
                                    commentnum.setText("("+Integer.toString(conmentslist.length())+")");

                                    for(int i=0;i<conmentslist.length();i++)
                                    {
                                        JSONObject conments = conmentslist.getJSONObject(i);
                                        JSONObject conuser = conuserlist.getJSONObject(i);
                                        Map<String,Object> item = new HashMap<String,Object>();
                                        item.put("touxiang", getResources().getString(R.string.burl)+conuser.getString("image"));
                                        item.put("nicheng", conuser.getString("nickname"));
                                        item.put("shijian", conments.getString("time"));
                                        item.put("pinglun", conments.getString("content"));
                                        mData.add(item);
                                    }
                                }
                                //评论list的adapter
                                ConmentslistAdapter adapter = new ConmentslistAdapter(GoodstradeActivity.this,mData);
                                adapter.notifyDataSetChanged();
                                commentlist.setAdapter(adapter);
                                tel = user.getString("tel");
                                guid = user.getInt("uid");
                                jiage = date.getDouble("price");

                                nickname.setText(user.getString("nickname"));
                                time.setText(date.getString("time"));
                                address.setText(date.getString("address"));
                                gname.setText(date.getString("gname"));
                                price.setText("¥:"+Double.toString(date.getDouble("price")));
                                oprice.setText("原价:¥"+Double.toString(date.getDouble("oprice")));
                                oprice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);

                                hownew.setText(date.getString("hownew"));
                                if(date.getInt("getway")==1) getway.setText("送货上门");
                                else getway.setText("自己来取");
                                type.setText(json.getString("type"));
                                scannum.setText(Integer.toString(date.getInt("scannum")));
                                detail.setText("  "+date.getString("detail"));

                                downLoad(getResources().getString(R.string.burl)+ date.getString("imageurl"),1);
                                downLoad(getResources().getString(R.string.burl)+ user.getString("image"),2);
                            }
                            else
                            {
                                Toast.makeText(GoodstradeActivity.this,"获取商品信息失败",Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1://加载商品图片
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        goodsbitmap = ImageDeal.String2Bitmap(msg.obj.toString());
                        image.setImageBitmap(goodsbitmap);
                    }
                    break;
                case 2://加载头像
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        Bitmap bitmap = ImageDeal.String2Bitmap(msg.obj.toString());
                        touxiang.setImageBitmap(bitmap);
                    }
                    break;
                case 3:
                    if (msg.obj.toString().trim().equals("-1"))
                    {
                        pd.cancel();
                        Toast.makeText(GoodstradeActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==1)
                            {
                                pd.cancel();
                                Toast.makeText(GoodstradeActivity.this,"提交成功,请等待卖家回复您",Toast.LENGTH_SHORT).show();
                                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                    editor.putFloat("balance",(float)(balance-jiage));
                                    editor.commit();
                                finish();
                            }
                            else
                            {
                                pd.cancel();
                                Toast.makeText(GoodstradeActivity.this,"不好意思，宝贝已被抢走！",Toast.LENGTH_SHORT).show();
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
                    if (msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(GoodstradeActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==1)
                            {
                                Toast.makeText(GoodstradeActivity.this,"评论已提交",Toast.LENGTH_SHORT).show();
                                commentedt.setText("");
                                getdata();
                            }
                            else
                            {
                                Toast.makeText(GoodstradeActivity.this,"不好意思，宝贝已被抢走！",Toast.LENGTH_SHORT).show();
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
    public void getdata()
    {
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gid",Integer.toString(gid));
                params.put("state",state);
                String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Findbyid.action";
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
                if(state ==1) message.what = 1;
                else message.what = 2;
                message.obj = back;
                handler.sendMessage(message);
            }
        }.start();
    }

    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.buy:      //买
                    setBuy();
                    break;
                case R.id.commentbtn:      //评论
                    setCommentbtn();
                    break;
                case R.id.lianxi:      //联系
                    setLianxi();
                    break;
                case R.id.share:      //分享
                    setShare();
                    break;
                default:
                    break;
            }
        }
    }
    public void setBuy()
    {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);
        balance = pref.getFloat("balance",0);
        if(guid== uid) Toast.makeText(GoodstradeActivity.this,"不能买自己的宝贝",Toast.LENGTH_SHORT).show();
        else if(jiage> balance){
            Toast.makeText(GoodstradeActivity.this,"余额不足请充值",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GoodstradeActivity.this,MycenterActivity.class);
            startActivity(intent);
        }
        else
        {
            AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(GoodstradeActivity.this);
            normalDialog.setTitle("提示");
            normalDialog.setMessage("你确定要购买吗？");
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
                                    params.put("guid",Integer.toString(guid));
                                    params.put("gid",Integer.toString(gid));
                                    String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Insert.action";
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
            normalDialog.setNegativeButton("取消",
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
    public void setCommentbtn()
    {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);
        if(commentedt.getText().toString().trim().isEmpty())
        {
            Toast.makeText(GoodstradeActivity.this,"评论不能为空哦！",Toast.LENGTH_SHORT).show();
        }
        else
        {
            pd.setMessage("评论中...");
            pd.show();
            new Thread() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("uid",Integer.toString(uid));
                    params.put("guid",Integer.toString(guid));
                    params.put("gid",Integer.toString(gid));
                    params.put("content",commentedt.getText().toString().trim());
                    String strUrlpath = getResources().getString(R.string.burl) + "ConmentsAction_Insert.action";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);
                    Message message = new Message();
                    message.what = 4;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            }.start();
        }
    }
    public void setLianxi()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(GoodstradeActivity.this.checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+tel));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }else{
            }
        }else{
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+tel));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
        }
    }
    public void setShare()
    {

        welcomebitmap= BitmapFactory.decodeResource(getResources(), R.drawable.welcome);

       // shareSingleImage();
        savaBitmap();
        shareMultiplePictureToTimeLine();
    }
    public void savaBitmap()
    {
        File dir = new File("/mnt/sdcard/test1/");
        if(!dir.exists())
        {
            dir.mkdirs();
        }

        File bitmapFile = new File("/mnt/sdcard/test1/test.png" );
        File bitmapFile1 = new File("/mnt/sdcard/test1/test1.png" );
        if(!bitmapFile.exists())
        {
            try
            {
                bitmapFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(!bitmapFile1.exists())
        {
            try
            {
                bitmapFile1.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        FileOutputStream fos,fos1;
        try
        {
            fos = new FileOutputStream(bitmapFile);
            goodsbitmap.compress(Bitmap.CompressFormat.PNG,
                    100, fos);
            fos.close();
            fos1 = new FileOutputStream(bitmapFile1);
            welcomebitmap.compress(Bitmap.CompressFormat.PNG,
                    100, fos1);
            fos1.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void shareSingleImage() {

        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File("/mnt/sdcard/test1/test.png"));
        Log.d("这里是分享share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
    private void shareMultiplePictureToTimeLine() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        imageUris.add(Uri.fromFile(new File("/mnt/sdcard/test1/test1.png")));
        imageUris.add(Uri.fromFile(new File("/mnt/sdcard/test1/test.png")));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.putExtra("Kdescription", "小跳蚤,大用途 快来FleaMarket和我一起充分利用二手物品的价值吧!(来自FleaMarket)");
        startActivity(intent);
    }
}
