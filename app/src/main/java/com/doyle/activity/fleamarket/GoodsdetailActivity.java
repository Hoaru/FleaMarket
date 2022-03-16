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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GoodsdetailActivity extends AppCompatActivity {
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    ProgressDialog pd;
    LinearLayout layout_back;
    ListView listView;
    TextView commentnum;

    Button delete,update,change;
    ImageView image;
    EditText name,price,detail;
    private int goods;
    private String goodsimageurl;
    private String goodsname;
    private Double goodsprice;
    private String goodsdetai;
    private int goodsgid;

    private Bitmap headImage = null;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //图片路径
    private String filename; //图片名称
    private    CharSequence []its = {"拍照","从相册选择"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_goodsdetail);
        goods = intent.getIntExtra("goods",0);
        init();

    }
    public void init()
    {
        listView = findViewById(R.id.commentlist);
        commentnum = findViewById(R.id.commentnum);
        layout_back = findViewById(R.id.layout_back);

        pd = new ProgressDialog(GoodsdetailActivity.this);
        pd.setMessage("发布中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        delete = findViewById(R.id.delete);
        update = findViewById(R.id.update);
        change = findViewById(R.id.change);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        detail = findViewById(R.id.detail);
        getgoodsinfo(goods,1);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int code = json.getInt("code");
                            if(code==1)
                            {
                                JSONObject data = new JSONObject(json.getString("date"));
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
                                ConmentslistAdapter adapter = new ConmentslistAdapter(GoodsdetailActivity.this,mData);
                                adapter.notifyDataSetChanged();
                                listView.setAdapter(adapter);
                                goodsgid = data.getInt("gid");
                                goodsname = data.getString("gname");
                                goodsprice = data.getDouble("price");
                                goodsdetai = data.getString("detail");
                                goodsimageurl = data.getString("imageurl");
                                name.setText(goodsname);
                                price.setText(Double.toString(goodsprice));
                                detail.setText(goodsdetai);
                                downLoad(getResources().getString(R.string.burl)+ goodsimageurl);

                            }else
                            {
                                Toast.makeText(GoodsdetailActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case  1:
                    if(!msg.obj.toString().trim().equals("-1"))
                    {
                        if(!msg.obj.toString().trim().isEmpty())
                        {
                            Toast.makeText(GoodsdetailActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            goodsimageurl = msg.toString().trim();
                        }
                        else
                        {
                            Toast.makeText(GoodsdetailActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();

                    }
                    break;
                case 2:
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        Bitmap bitmap = ImageDeal.String2Bitmap(msg.obj.toString());
                        image.setImageBitmap(bitmap);
                    }
                    break;
                case 3:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==1)
                            {
                                update.setVisibility(View.INVISIBLE);
                                name.setEnabled(false);
                                price.setEnabled(false);
                                detail.setEnabled(false);
                                change.setText("修改");
                                Toast.makeText(GoodsdetailActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(GoodsdetailActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
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
                    {
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try
                        {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==1)
                            {
                                Toast.makeText(GoodsdetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(GoodsdetailActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
    public void getgoodsinfo(final int gid,final int state)
    {
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gid",Integer.toString(gid));
                params.put("state",Integer.toString(state));
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
    public void imagechange(View view)
    {
        alertWay();
    }
    public void setDelete(View view)
    {
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(GoodsdetailActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否确认删除商品？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        new Thread() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("gid",Integer.toString(goodsgid));
                                String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Delete.action";
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
    public void setUpdate(View view)
    {
        DecimalFormat decimalFormat =new DecimalFormat("0.00");

        if(name.getText().toString().trim().isEmpty())
        {
            Toast.makeText(GoodsdetailActivity.this,"名字不能为空",Toast.LENGTH_SHORT).show();
        }
        else if(price.getText().toString().isEmpty()||Double.parseDouble(price.getText().toString())<1)
        {
            Toast.makeText(GoodsdetailActivity.this,"价值不能低于1元",Toast.LENGTH_SHORT).show();
        }
        else if (detail.getText().toString().trim().length()<6)
        {
            Toast.makeText(GoodsdetailActivity.this,"少年描述得不够详细啊",Toast.LENGTH_SHORT).show();
        }
        else
        {
            final String Price =decimalFormat.format(Double.parseDouble(price.getText().toString()));

            AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(GoodsdetailActivity.this);
            normalDialog.setTitle("提示");
            normalDialog.setMessage("是否保存修改的内容?");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           pd.show();
                            new Thread() {
                                @Override
                                public void run() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("gid",Integer.toString(goodsgid));
                                    params.put("gname",name.getText().toString().trim());
                                    params.put("price",Price);
                                    params.put("detail",detail.getText().toString().trim());
                                    String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Update.action";
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
    }
    public void setChange(View view)
    {
        if(change.getText().toString().trim().equals("修改"))
        {
            update.setVisibility(View.VISIBLE);
            name.setEnabled(true);
            price.setEnabled(true);
            detail.setEnabled(true);
            change.setText("取消");
        }
        else{
            update.setVisibility(View.INVISIBLE);
            name.setEnabled(false);
            price.setEnabled(false);
            detail.setEnabled(false);
            change.setText("修改");
        }
    }
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
                message.what = 2;
                message.obj = back;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 修改图片方式
     */
    private void alertWay()
    {
        new AlertDialog.Builder(GoodsdetailActivity.this)
                .setTitle("更换图片")
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
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                intent.putExtra("crop", "true");
                                intent.putExtra("return-data", true);

                                //设置宽高比例
                                intent.putExtra("aspectX", 1);
                                intent.putExtra("aspectY", 1);
                                //设置裁剪图片宽高
                                intent.putExtra("outputX", 450);
                                intent.putExtra("outputY", 450);

                                intent.putExtra("noFaceDetection", true); // no face detection
                                intent.putExtra("scale", true);

                                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
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
                imageUri = data.getData();
                try {
                    String[] pojo = { MediaStore.Images.Media.DATA };

                    Cursor cursor = managedQuery(imageUri, pojo, null, null, null);
                    if (cursor != null) {
                        ContentResolver cr = this.getContentResolver();
                        int colunm_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String path = cursor.getString(colunm_index);
                        /***
                         * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
                         * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
                         */
                        if (path.endsWith("jpg") || path.endsWith("png")) {
                            filename = path;
                            Bitmap bitmap = BitmapFactory.decodeStream(cr
                                    .openInputStream(imageUri));
                            headImage = bitmap;
                            System.out.println("图片大小为"+headImage.getByteCount()/1024+
                                    "KB宽度为"+headImage.getHeight()+"高度为："+headImage.getWidth());
                            updatePicture();
                            image.setImageBitmap(headImage); //将剪裁后照片显示出来
                        } else {
                            alert();
                        }
                    } else {
                        alert();
                    }

                } catch (Exception e) {
                }
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
                    Toast.makeText(GoodsdetailActivity.this, "剪裁图片", Toast.LENGTH_SHORT).show();
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
                    updatePicture();
                    image.setImageBitmap(headImage); //将剪裁后照片显示出来
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
                int uid = pref.getInt("uid",0);
                Map<String, String> params = new HashMap<String, String>();
                params.put("gid",Integer.toString(goodsgid));
                params.put("uid",Integer.toString(uid));
                params.put("imageurl",ImageDeal.Bitmap2String(headImage));
                String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Updateimage.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    private void alert() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        filename = null;
                    }
                }).create();
        dialog.show();
    }
}
