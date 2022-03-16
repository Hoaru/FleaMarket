package com.doyle.activity.fleamarket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Toast;

import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;
import com.doyle.util.RealPathFromUriUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class CharityActivity extends AppCompatActivity {

    Button cancel,enter;
    EditText detail,title,need;
    ImageView image;
    String updateimage;
    private OnClickListener listener;
    int uid;

    ProgressDialog pd;


    private Bitmap headImage = null;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //图片路径
    private String filename; //图片名称
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);
        initview();

    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pd.cancel();
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(CharityActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else if (msg.obj.toString().trim().equals("1"))
                    {
                        Toast.makeText(CharityActivity.this,"发布成功！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(CharityActivity.this,"发布失败！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public  void initview()
    {
        SharedPreferences pref = CharityActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);


        listener = new OnClickListener();

        cancel = findViewById(R.id.cancel);
        enter = findViewById(R.id.enter);

        detail = findViewById(R.id.detail);
        title = findViewById(R.id.title);
        need = findViewById(R.id.need);
        image = findViewById(R.id.image);

        cancel.setOnClickListener(listener);
        enter.setOnClickListener(listener);
        image.setOnClickListener(listener);

        pd = new ProgressDialog(CharityActivity.this);
        pd.setMessage("发布中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
    }
    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.image:   //头像
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                    startActivityForResult(intent,SELECT_PIC);
                    break;
                case R.id.cancel:
                    finish();
                    break;
                case R.id.enter:
                    upload();
                    break;
                default:
                    break;
            }
        }
    }

    public void upload()
    {
        if(headImage==null)
        {
            Toast.makeText(CharityActivity.this,"少年传照片啊",Toast.LENGTH_SHORT).show();
        }
        else if(title.getText().toString().trim().isEmpty())
        {
            Toast.makeText(CharityActivity.this,"少年给这个项目取个名字啊",Toast.LENGTH_SHORT).show();
        }
        else if(detail.getText().toString().trim().length()<50)
        {
            Toast.makeText(CharityActivity.this,"请填写不少于50字的描述",Toast.LENGTH_SHORT).show();
        }
        else if(need.getText().toString().trim().isEmpty())
        {
            Toast.makeText(CharityActivity.this,"需求不能为空！",Toast.LENGTH_SHORT).show();
        }
        else
        {
            AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(CharityActivity.this);
            normalDialog.setTitle("提示");
            normalDialog.setMessage("公益项目将持续10天自动结束，你确定发布吗？");
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
                                    params.put("name",title.getText().toString().trim());
                                    params.put("content",detail.getText().toString().trim());
                                    params.put("imageurl", updateimage);
                                    params.put("need", need.getText().toString().trim());
                                    String strUrlpath = getResources().getString(R.string.burl) + "CharityAction_Insert.action";
                                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                    System.out.println("结果为：" + Result);
                                    Message message = new Message();
                                    message.what = 0;
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
                intent1.putExtra("aspectX", 2);
                intent1.putExtra("aspectY", 1);
                intent1.putExtra("outputX", 600);
                intent1.putExtra("outputY", 300);
                intent1.putExtra("return-data", false);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent1.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent1.putExtra("noFaceDetection", true);
                startActivityForResult(intent1, CROP_PHOTO);
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
                    updateimage = ImageDeal.Bitmap2String(headImage);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
