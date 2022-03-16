package com.doyle.activity.fleamarket;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.util.GetMD5;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;
import com.doyle.util.RealPathFromUriUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MycenterActivity extends AppCompatActivity {
    //UI和监听器
    LinearLayout touxiang_layout;
    ImageView touxiang;
    TextView  uid,balance,vip;
    EditText nicheng,tel,dizhi;
    Button bianji,quxiao,chongzhi,tixian;
    RadioButton men,women;

    private OnClickListener listener;

    private Bitmap headImage = null;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //图片路径
    private String filename; //图片名称
    private    CharSequence []its = {"拍照","从相册选择"};

    //返回值
    String back;
    //提取本地值
    private String nickname;
    private String name;
    private String image;
    private String address;
    private float Balance;
    private String Tel;
    private int Vip;
    private int sex;
    private int Uid;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycenter);

        initView();
    }
    private void initView()
    {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        nickname = pref.getString("nickname","null");
        name = pref.getString("name","null");
        pwd = pref.getString("pwd","null");
        image = pref.getString("image","");
        address = pref.getString("address","null");
        Balance = pref.getFloat("balance",0);
        sex = pref.getInt("sex",0);
        Tel = pref.getString("tel","null");
        Vip = pref.getInt("vip",0);
        Uid = pref.getInt("uid",0);


        listener = new OnClickListener();

        touxiang = findViewById(R.id.touxiang);
        touxiang_layout = findViewById(R.id.touxiang_layout);

        uid = findViewById(R.id.uid);
        balance = findViewById(R.id.balance);
        vip = findViewById(R.id.vip);

        nicheng = findViewById(R.id.nicheng);
        tel = findViewById(R.id.tel);
        dizhi = findViewById(R.id.dizhi);

        bianji = findViewById(R.id.bianji);
        quxiao = findViewById(R.id.quxiao);
        chongzhi = findViewById(R.id.chongzhi);
        tixian  = findViewById(R.id.tixian);

        men=findViewById(R.id.male_rb);
        women =findViewById(R.id.famale_rb);

       // touxiang.setOnClickListener(listener);
        touxiang_layout.setOnClickListener(listener);

        if(!image.isEmpty())
        {
            downLoad(getResources().getString(R.string.burl)+ image);
        }
        uid.setText(name);
        balance.setText(Float.toString(Balance));
        vip.setText(Integer.toString(Vip));

        nicheng.setText(nickname);
        tel.setText(Tel);
        dizhi.setText(address);

        if(sex ==0) men.setChecked(true);
        else women.setChecked(true);

        bianji.setOnClickListener(listener);
        quxiao.setOnClickListener(listener);
        chongzhi.setOnClickListener(listener);
        tixian.setOnClickListener(listener);

        getdata();
    }

    /**
     * 点击事件监听类
     */
    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.touxiang_layout:      //头像
                    alertWay();
                    break;
                case R.id.bianji:      //编辑
                    if(bianji.getText().equals("编辑")) bianji();
                    else baocun();
                    break;
                case R.id.quxiao:      //取消
                     quxiao();
                    break;
                case R.id.chongzhi:      //充值
                    chongzhi();
                    break;
                case R.id.tixian:      //提现
                    tixian();
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
                    back = msg.obj.toString();
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MycenterActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        else Toast.makeText(MycenterActivity.this,"修改成功",Toast.LENGTH_SHORT).show();

                    }
                    else Toast.makeText(MycenterActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        Bitmap bitmap = ImageDeal.String2Bitmap(msg.obj.toString());
                        touxiang.setImageBitmap(bitmap);
                    }
                 break;
                case  2:
                    if(msg.obj.toString().trim().equals("-1")||msg.obj.toString().trim().isEmpty())
                        Toast.makeText(MycenterActivity.this,"充值失败",Toast.LENGTH_SHORT).show();
                    else{
                        Toast.makeText(MycenterActivity.this,"充值成功",Toast.LENGTH_SHORT).show();
                        balance.setText(msg.obj.toString().trim());
                    }
                 break;
                case 3:
                    if(msg.obj.toString().trim().equals("-1")||msg.obj.toString().trim().isEmpty())
                        Toast.makeText(MycenterActivity.this,"提现失败",Toast.LENGTH_SHORT).show();
                    else{
                        Toast.makeText(MycenterActivity.this,"提现成功",Toast.LENGTH_SHORT).show();
                        balance.setText(msg.obj.toString().trim());
                    }
                 break;
                case 4:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MycenterActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==0) Toast.makeText(MycenterActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                            else
                            {
                                JSONObject data = new JSONObject(json.getString("data"));
                                SharedPreferences pref = MycenterActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("address",data.getString("address"));
                                editor.putInt("sex",data.getInt("sex"));
                                editor.putString("nickname",data.getString("nickname"));
                                editor.putString("tel",data.getString("tel"));
                                editor.commit();
                                bianji.setText("编辑");
                                quxiao.setVisibility(View.INVISIBLE);
                                nicheng.setEnabled(false);
                                tel.setEnabled(false);
                                dizhi.setEnabled(false);
                                men.setEnabled(false);
                                women.setEnabled(false);
                                Toast.makeText(MycenterActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                 break;
                case 5:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(MycenterActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try{
                            JSONObject code = new JSONObject(msg.obj.toString().trim());
                            int  back = code.getInt("code");
                            if(back==0)
                            {
                                Toast.makeText(MycenterActivity.this,"账号或密码错误！",Toast.LENGTH_SHORT).show();
                            }
                            else if(back==1)
                            {
                                JSONObject data = new JSONObject(code.getString("data"));
                                SharedPreferences pref = MycenterActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putFloat("balance",(float) data.getDouble("balance"));
                                editor.commit();
                                Balance = (float) data.getDouble("balance");
                                balance.setText(Float.toString(Balance));
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

    /*获取用户账户信息*/
    public void getdata()
    {
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("pwd", pwd);
                String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_Login.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("登录结果为：" + Result);
                Message message = new Message();
                message.what = 5;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }

    /*
      下载头像图片
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
    /**
     * 头像上传方式
     */
    private void alertWay()
    {
        new AlertDialog.Builder(MycenterActivity.this)
                .setTitle("更换头像")
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
                    Toast.makeText(MycenterActivity.this, "剪裁图片", Toast.LENGTH_SHORT).show();
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
                    headImage = ImageDeal.toRoundBitmap(bitmap);
                    System.out.println("图片大小为"+headImage.getByteCount()/1024+
                            "KB宽度为"+headImage.getHeight()+"高度为："+headImage.getWidth());
                    updatePicture();
                    touxiang.setImageBitmap(headImage); //将剪裁后照片显示出来
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public  void updatePicture()//上传图片
    {
        new Thread() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(Uid));
                params.put("image",ImageDeal.Bitmap2String(headImage));
                String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_updateimage.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                SharedPreferences.Editor editor = pref.edit();
                if(!Result.trim().isEmpty()&&!Result.trim().equals("-1"))
                {
                    editor.putString("image",Result.trim());
                    editor.commit();
                }
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }


    public void bianji()//编辑按钮响应事件
    {
        bianji.setText("保存");
        quxiao.setVisibility(View.VISIBLE);
        nicheng.setEnabled(true);
        tel.setEnabled(true);
        dizhi.setEnabled(true);
        men.setEnabled(true);
        women.setEnabled(true);

    }
    public void quxiao()//取消按钮响应事件
    {
        bianji.setText("编辑");
        quxiao.setVisibility(View.INVISIBLE);
        nicheng.setEnabled(false);
        tel.setEnabled(false);
        dizhi.setEnabled(false);
        men.setEnabled(false);
        women.setEnabled(false);
    }
    public void baocun()//保存按钮响应事件
    {
        if(nicheng.getText().toString().trim().isEmpty())
        {
            Toast.makeText(MycenterActivity.this,"这个昵称不霸气啊",Toast.LENGTH_SHORT).show();
        }
        else if(tel.getText().toString().trim().isEmpty())
        {
            Toast.makeText(MycenterActivity.this,"哪有这种电话号码",Toast.LENGTH_SHORT).show();
        }
        else if(dizhi.getText().toString().trim().isEmpty())
        {
            Toast.makeText(MycenterActivity.this,"地址不能隐身哦",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(women.isChecked()) sex=1;
            else sex=0;
            new Thread() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("uid",Integer.toString(Uid));
                    params.put("nickname",nicheng.getText().toString());
                    params.put("sex",Integer.toString(sex));
                    params.put("tel",tel.getText().toString());
                    params.put("address",dizhi.getText().toString());
                    String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_UpdateInfo.action";
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
    public void chongzhi()//充值按钮响应事件
    {
        final LinearLayout chongzhi_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_chongzhi,null);
        new AlertDialog.Builder(this)
                .setTitle("充值")
                .setView(chongzhi_layout)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        DecimalFormat decimalFormat =new DecimalFormat("0.00");
                        EditText jine = chongzhi_layout.findViewById(R.id.jine);
                        if(jine.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(MycenterActivity.this,"空的别提交啊",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            final String monney =decimalFormat.format(Double.parseDouble(jine.getText().toString()));
                            System.out.println("看一下输入的金额"+monney);
                            if(monney.trim().equals("0.00"))
                            {
                                Toast.makeText(MycenterActivity.this,"搞事情是不是",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                  new Thread() {
                                     @Override
                                     public void run() {
                                         Map<String, String> params = new HashMap<String, String>();
                                         params.put("uid",Integer.toString(Uid));
                                         params.put("balance",monney);
                                         params.put("state","1");
                                         String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_Moneychange.action";
                                         String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                         System.out.println("结果为：" + Result);
                                         SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                                         SharedPreferences.Editor editor = pref.edit();
                                       if(!Result.trim().isEmpty()&&!Result.trim().equals("-1"))
                                       {
                                          editor.putFloat("balance",(float) Double.parseDouble(Result.trim()));
                                          editor.commit();
                                       }
                                        Message message = new Message();
                                        message.what = 2;
                                        message.obj = Result;
                                        handler.sendMessage(message);
                                    }
                                }.start();
                            }
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
    public void tixian()//提现按钮响应事件
    {
       final LinearLayout chongzhi_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_chongzhi,null);
        new AlertDialog.Builder(this)
                .setTitle("提现")
                .setView(chongzhi_layout)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        DecimalFormat decimalFormat =new DecimalFormat("0.00");
                        EditText jine = chongzhi_layout.findViewById(R.id.jine);
                        if(jine.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(MycenterActivity.this,"空的别提交啊",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                            Balance = pref.getFloat("balance",0);
                            final String monney =decimalFormat.format(Double.parseDouble(jine.getText().toString()));
                            System.out.println("看一下输入的金额"+monney);
                            if(monney.trim().equals("0.00"))
                            {
                                Toast.makeText(MycenterActivity.this,"搞事情是不是",Toast.LENGTH_SHORT).show();
                            }
                            else if(Double.parseDouble(jine.getText().toString())>Balance)
                            {
                                Toast.makeText(MycenterActivity.this,"醒醒吧你没这么多钱",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("uid",Integer.toString(Uid));
                                        params.put("balance",monney);
                                        params.put("state","2");
                                        String strUrlpath = getResources().getString(R.string.burl) + "SignupAndLoginAction_Moneychange.action";
                                        String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                        System.out.println("结果为：" + Result);
                                        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        if(!Result.trim().isEmpty()&&!Result.trim().equals("-1"))
                                        {
                                            editor.putFloat("balance",(float) Double.parseDouble(Result.trim()));
                                            editor.commit();
                                        }
                                        Message message = new Message();
                                        message.what = 3;
                                        message.obj = Result;
                                        handler.sendMessage(message);
                                    }
                                }.start();
                            }
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

}
