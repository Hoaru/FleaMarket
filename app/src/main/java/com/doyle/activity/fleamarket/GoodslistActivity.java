package com.doyle.activity.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.adapter.GoodslistAdapter;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoodslistActivity extends AppCompatActivity {

    LinearLayout moren,renqi,shijian,jiagedi,jiagegao;
    TextView moren_text,renqi_text,shijian_text,jiagedi_text,jiagegao_text;
    ListView goodslist;
    private String searchname;
    private String searchway;
    LinearLayout backlayout;

    ProgressDialog pd ;
    String bitmap;

    //定义颜色值
    private int Black = 0xFF000000;
    private int Red =0xFFFF0000;

    private OnClickListener listener;

    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodslist);
        Intent intent = getIntent();
        searchname = intent.getStringExtra("name");
        searchway = intent.getStringExtra("searchway");
        initview();
        initstate();
    }
    public void initview()
    {
        pd = new ProgressDialog(GoodslistActivity.this);
        pd.setMessage("宝贝加载中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        backlayout = findViewById(R.id.back);

        moren = findViewById(R.id.moren);
        renqi = findViewById(R.id.renqi);
        shijian = findViewById(R.id.shijian);
        jiagedi = findViewById(R.id.jiagedi);
        jiagegao = findViewById(R.id.jiagegao);

        moren_text = findViewById(R.id.moren_text);
        renqi_text = findViewById(R.id.renqi_text);
        shijian_text = findViewById(R.id.shijian_text);
        jiagedi_text = findViewById(R.id.jiagedi_text);
        jiagegao_text = findViewById(R.id.jiagegao_text);

        listener = new OnClickListener();

        moren.setOnClickListener(listener);
        renqi.setOnClickListener(listener);
        shijian.setOnClickListener(listener);
        jiagedi.setOnClickListener(listener);
        jiagegao.setOnClickListener(listener);

        goodslist = findViewById(R.id.goodslist);
        getdata(searchname,"1",searchway);
    }
    public void initstate()
    {
        moren_text.setTextColor(Red);
        renqi_text.setTextColor(Black);
        shijian_text.setTextColor(Black);
        jiagegao_text.setTextColor(Black);
        jiagedi_text.setTextColor(Black);
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        Toast.makeText(GoodslistActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                        pd.cancel();
                    }
                    else
                    {
                        pd.cancel();
                        try{
                                JSONObject json = new JSONObject(msg.obj.toString().trim());
                                int back = json.getInt("code");
                                if(back == 1)
                                {
                                final JSONArray date = new JSONArray(json.getString("date"));
                                final JSONArray type = new JSONArray(json.getString("type"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject goods = date.getJSONObject(i);
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("gid", goods.getString("gid"));
                                    item.put("gname", goods.getString("gname"));
                                    item.put("detail", goods.getString("detail"));
                                    item.put("type", type.get(i));
                                    item.put("hownew", goods.getString("hownew"));
                                    if(goods.getInt("getway")==1) item.put("getway", "送货上门");
                                    else item.put("getway", "自己来取");
                                    item.put("price", goods.getDouble("price"));
                                    item.put("scannum", goods.getInt("scannum"));
                                    item.put("image", getResources().getString(R.string.burl)+goods.getString("imageurl"));
                                    mData.add(item);
                                }
                                GoodslistAdapter adapter = new GoodslistAdapter(GoodslistActivity.this,mData);
                                adapter.notifyDataSetChanged();
                                goodslist = findViewById(R.id.goodslist);
                                goodslist.setAdapter(adapter);
                                backlayout.setVisibility(View.INVISIBLE);
                                goodslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        Intent intent = new Intent(GoodslistActivity.this,GoodstradeActivity.class);
                                               intent.putExtra("gid",Integer.parseInt(mData.get(position).get("gid").toString()));
                                               intent.putExtra("state","1");
                                               startActivity(intent);
                                    }
                                });
                            }
                            else backlayout.setVisibility(View.VISIBLE);
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

    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.moren:
                    clearChioce();
                    moren_text.setTextColor(Red);
                    getdata(searchname,"1",searchway);
                    break;
                case R.id.renqi:
                    clearChioce();
                    renqi_text.setTextColor(Red);
                    getdata(searchname,"2",searchway);
                    break;
                case R.id.shijian:
                    clearChioce();
                    shijian_text.setTextColor(Red);
                    getdata(searchname,"3",searchway);
                    break;
                case R.id.jiagedi:
                    clearChioce();
                    jiagedi_text.setTextColor(Red);
                    getdata(searchname,"4",searchway);
                    break;
                case R.id.jiagegao:
                    clearChioce();
                    jiagegao_text.setTextColor(Red);
                    getdata(searchname,"5",searchway);
                    break;
                default:
                    break;
            }
        }
    }

    //建立一个清空选中状态的方法
    public void clearChioce()
    {
        moren_text.setTextColor(Black);
        renqi_text.setTextColor(Black);
        shijian_text.setTextColor(Black);
        jiagegao_text.setTextColor(Black);
        jiagedi_text.setTextColor(Black);
    }
    public void getdata(final String gname,final String state,final String searchway)
    {
        pd.show();
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gname",gname);
                params.put("state",state);
                params.put("getway",searchway);
                String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Findbygname.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
}
