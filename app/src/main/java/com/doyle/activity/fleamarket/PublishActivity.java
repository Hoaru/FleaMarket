package com.doyle.activity.fleamarket;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;
import com.doyle.util.RealPathFromUriUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {

    int way;

    //手续费
    float shouxu;
    float balance;
    //提示
    ProgressDialog pd;

    Button cancel,enter;
    ImageView image;
    Spinner type,hownew;
    EditText title,price,oprice,detail,address;
    RadioButton song,qu;
    CheckBox jiaji;


    private Bitmap headImage = null;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //图片路径
    private String filename; //图片名称
    private    CharSequence []its = {"拍照","从相册选择"};
    private int uid;
    private int getway=1;
    private int emergent=1;

    //两个Spinner的适配器
    private List<String> type_list;
    private List<String> hownew_list;
    private ArrayAdapter<String> type_adapter;
    private ArrayAdapter<String> hownew_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        Intent intent = getIntent();
        way = intent.getIntExtra("way",0);
        getType();
        initview();
    }
    public void initview()//初始化UI
    {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        uid = pref.getInt("uid", 0);

        pd = new ProgressDialog(PublishActivity.this);
        pd.setMessage("发布中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);


        cancel = findViewById(R.id.cancel);
        enter = findViewById(R.id.enter);
        image = findViewById(R.id.image);
        type = findViewById(R.id.type);
        hownew = findViewById(R.id.hownew);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        oprice = findViewById(R.id.oprice);
        detail = findViewById(R.id.detail);
        address = findViewById(R.id.address);
        song = findViewById(R.id.song);
        qu = findViewById(R.id.qu);
        jiaji = findViewById(R.id.jiaji);
        //已免费送方式进入
        if (way == 2)
        {
            price.setEnabled(false);
            price.setText("0.00");
            jiaji.setEnabled(false);
        }

        //新数据
        hownew_list = new ArrayList<String>();
        hownew_list.add("十成新");
        hownew_list.add("九五新");
        hownew_list.add("九成新");
        hownew_list.add("八五新");
        hownew_list.add("七成新");
        hownew_list.add("六成新");
        hownew_list.add("五成新");
        //适配器
        type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
        hownew_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hownew_list);
        //设置样式
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hownew_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        type.setAdapter(type_adapter);
        hownew.setAdapter(hownew_adapter);
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(PublishActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        try
                        {
                            JSONObject json =new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==0)
                            {
                                Toast.makeText(PublishActivity.this,"加载失败！",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                JSONArray list = new JSONArray(json.getString("date"));
                                for(int i=0;i<list.length();i++)
                                {
                                    JSONObject type = list.getJSONObject(i);
                                    type_list.add(type.getString("tname"));
                                }
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        pd.cancel();
                        Toast.makeText(PublishActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try
                        {
                            JSONObject json =new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==1)
                            {
                                SharedPreferences pref = PublishActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putFloat("balance",(float)(balance-shouxu));
                                editor.commit();
                                pd.cancel();
                                Toast.makeText(PublishActivity.this,"发布成功！",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                pd.cancel();
                                Toast.makeText(PublishActivity.this,"上传失败，稍后再试！",Toast.LENGTH_SHORT).show();
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


    public void getType()
    {
        //类型数据
        type_list = new ArrayList<String>();
        type_list.add("请选择类型...");
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Gettype.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();

    }
    public void publish(View view)//完成（发布）按钮点击事件
    {
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取送货方式
        if (song.isChecked()) getway = 1;
        else getway = 2;
        //获取是否加急
        if(jiaji.isChecked()) emergent = 2;
        else emergent = 1;

        DecimalFormat decimalFormat =new DecimalFormat("0.00");
        if(oprice.getText().toString().trim().isEmpty()||Double.parseDouble(oprice.getText().toString())==0)
            oprice.setText("0");

        if(headImage==null)
        {
            Toast.makeText(PublishActivity.this,"少年不传照片怎么卖啊",Toast.LENGTH_SHORT).show();
        }
        else if(title.getText().toString().trim().isEmpty())
        {
            Toast.makeText(PublishActivity.this,"少年给你的宝贝取个名字啊",Toast.LENGTH_SHORT).show();
        }
        else if(type.getSelectedItem().toString().trim().length()>4)
        {
            Toast.makeText(PublishActivity.this,"请给宝贝分类呗",Toast.LENGTH_SHORT).show();
        }
        else if(way==1&&(price.getText().toString().isEmpty()||Double.parseDouble(price.getText().toString())<1))
        {
            Toast.makeText(PublishActivity.this,"价值低于1元的宝贝不在这里发布哦",Toast.LENGTH_SHORT).show();
        }
        else if (detail.getText().toString().trim().length()<6)
        {
            Toast.makeText(PublishActivity.this,"少年描述得不够详细啊",Toast.LENGTH_SHORT).show();
        }
        else if(address.getText().toString().trim().length()<6)
        {
            Toast.makeText(PublishActivity.this,"售卖地址很关键哦，请填详细点",Toast.LENGTH_SHORT).show();
        }
        else
        {
            final String Price =decimalFormat.format(Double.parseDouble(price.getText().toString()));
            final String Oprice =decimalFormat.format(Double.parseDouble(oprice.getText().toString()));


            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
            balance = pref.getFloat("balance",0);
            //判断是否有足够余额支付交易手续
            //加急*0.02手续费
            if(emergent==2)
            {
                //手续费保留两位小数的double
                shouxu = Float.parseFloat(decimalFormat.format(Double.parseDouble(price.getText().toString())*0.02));
                //不足0.02
                if(balance<shouxu)
                {
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(PublishActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("您的余额不足以支付交易手续："+shouxu+"元，是否前往充值？");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(PublishActivity.this,MycenterActivity.class);
                                    startActivity(intent);
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
                //足够0.02
                else
                {
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(PublishActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("将收取手续费:"+shouxu+"元,你确定发布吗？");
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
                                            params.put("gname",title.getText().toString().trim());
                                            params.put("type",type.getSelectedItem().toString());
                                            params.put("price",Price);
                                            params.put("oprice", Oprice);
                                            params.put("hownew",hownew.getSelectedItem().toString());
                                            params.put("detail",detail.getText().toString().trim());
                                            params.put("getway",Integer.toString(getway));
                                            params.put("imageurl", ImageDeal.Bitmap2String(headImage));
                                            params.put("address", address.getText().toString().trim());
                                            params.put("emergent", Integer.toString(emergent));
                                            params.put("time",sdf.format(d));
                                            String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Insert.action";
                                            String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                            System.out.println("结果为：" + Result);
                                            Message message = new Message();
                                            message.what = 1;
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
            //不加急*0.01手续费
            else
            {
                //手续费保留两位小数的double
                shouxu = Float.parseFloat(decimalFormat.format(Double.parseDouble(price.getText().toString())*0.01));
                //不足0.01
                if(balance<shouxu)
                {
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(PublishActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("您的余额不足以支付交易手续:"+shouxu+"元，是否前往充值？");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(PublishActivity.this,MycenterActivity.class);
                                    startActivity(intent);
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
                //足够0.01
                else
                {
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(PublishActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("将收取手续费:"+shouxu+"元,你确定发布吗？");
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
                                            params.put("gname",title.getText().toString().trim());
                                            params.put("type",type.getSelectedItem().toString());
                                            params.put("price",Price);
                                            params.put("oprice", Oprice);
                                            params.put("hownew",hownew.getSelectedItem().toString());
                                            params.put("detail",detail.getText().toString().trim());
                                            params.put("getway",Integer.toString(getway));
                                            params.put("imageurl", ImageDeal.Bitmap2String(headImage));
                                            params.put("address", address.getText().toString().trim());
                                            params.put("emergent", Integer.toString(emergent));
                                            params.put("time",sdf.format(d));
                                            String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Insert.action";
                                            String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                            System.out.println("结果为：" + Result);
                                            Message message = new Message();
                                            message.what = 1;
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

        }
    }
    public void cancel(View view)//取消按钮点击事件
    {
        finish();
    }
    public void Image(View view)//图片控件点击事件
    {
        new AlertDialog.Builder(PublishActivity.this)
                .setTitle("上传图片")
                .setItems(its, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case 0://拍照
                                //图片名称 时间命名
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                                Date date = new Date(System.currentTimeMillis());
                                filename = format.format(date);
                                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                File outputImage = new File(path,filename+".jpg");
                                try {
                                    if(outputImage.exists()) {
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch(IOException e) {
                                    e.printStackTrace();
                                }
                                //将File对象转换为Uri并启动照相程序
                                imageUri = Uri.fromFile(outputImage);
                                Intent tTntent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
                                tTntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
                                startActivityForResult(tTntent,TAKE_PHOTO); //启动照相
                                break;
                            case 1://从相册选择
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                                startActivityForResult(intent,SELECT_PIC);
                                break;
                        }
                    }
                })
                .create()
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode)
        {
            case SELECT_PIC://相册
                String path = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                File file = new File(path);
                imageUri = Uri.fromFile(file);
                Intent intent1 = new Intent("com.android.camera.action.CROP");
                intent1.setDataAndType(imageUri, "image/*");
                intent1.putExtra("crop", "true");
                intent1.putExtra("aspectX", 1);
                intent1.putExtra("aspectY", 1);
                intent1.putExtra("outputX", 450);
                intent1.putExtra("outputY", 450);
                intent1.putExtra("return-data", false);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent1.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent1.putExtra("noFaceDetection", true);
                startActivityForResult(intent1, CROP_PHOTO);
                break;
            case TAKE_PHOTO://相机
                try {
                    Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    //设置宽高比例
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    //设置裁剪图片宽高
                    intent.putExtra("outputX", 450);
                    intent.putExtra("outputY", 450);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    Toast.makeText(PublishActivity.this, "剪裁图片", Toast.LENGTH_SHORT).show();
                    //广播刷新相册
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(imageUri);
                    this.sendBroadcast(intentBc);
                    startActivityForResult(intent, CROP_PHOTO); //设置裁剪参数显示图片至ImageView
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO:
                try {
                    //图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(imageUri));
                    headImage = bitmap;
                    System.out.println("图片大小为"+headImage.getByteCount()/1024+
                            "KB宽度为"+headImage.getHeight()+"高度为："+headImage.getWidth());
                    image.setImageBitmap(headImage); //将剪裁后照片显示出来
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

}
