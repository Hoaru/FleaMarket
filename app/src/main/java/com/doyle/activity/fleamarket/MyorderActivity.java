package com.doyle.activity.fleamarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyorderActivity extends AppCompatActivity {

    LinearLayout trading,tradesuccess,tradefail,trade;
    TextView trading_text,tradesuccess_text,tradefail_text,trade_text;
    ListView list;
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    //定义颜色值
    private int Gray = 0xFF999999;
    private int Green =0xFF45C01A;

    LinearLayout layout_back;
    private OnClickListener listener;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);
        initview();
        initstate();
    }
    public void initview()
    {
        layout_back = findViewById(R.id.back);
        trading = findViewById(R.id.trading);
        tradesuccess = findViewById(R.id.tradesuccess);
        tradefail = findViewById(R.id.tradefail);
        trade = findViewById(R.id.trade);
        trading_text = findViewById(R.id.trading_text);
        tradesuccess_text = findViewById(R.id.tradesuccess_text);
        tradefail_text = findViewById(R.id.tradefail_text);
        trade_text = findViewById(R.id.trade_text);

        list = findViewById(R.id.listview);
        listener = new OnClickListener();

        trade.setOnClickListener(listener);
        trading.setOnClickListener(listener);
        tradesuccess.setOnClickListener(listener);
        tradefail.setOnClickListener(listener);

        setTrading();
    }
    public void initstate()
    {
        trading_text.setTextColor(Green);
        trade_text.setTextColor(Gray);
        tradesuccess_text.setTextColor(Gray);
        tradefail_text.setTextColor(Gray);
    }
    public void cleancookie()
    {
        trading_text.setTextColor(Gray);
        trade_text.setTextColor(Gray);
        tradesuccess_text.setTextColor(Gray);
        tradefail_text.setTextColor(Gray);
    }
    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.trading:      //待发货订单
                     setTrading();
                     cleancookie();
                     trading_text.setTextColor(Green);
                     layout_back.setVisibility(View.INVISIBLE);
                    break;
                case R.id.trade:      //待收货订单
                    setTrade();
                    cleancookie();
                    trade_text.setTextColor(Green);
                    layout_back.setVisibility(View.INVISIBLE);
                    break;
                case R.id.tradesuccess:   //交易成功订单
                    setTradesuccess();
                    cleancookie();
                    tradesuccess_text.setTextColor(Green);
                    layout_back.setVisibility(View.INVISIBLE);
                    break;
                case R.id.tradefail:      //交易失败订单
                    setTradefail();
                    cleancookie();
                    tradefail_text.setTextColor(Green);
                    layout_back.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(MyorderActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                layout_back.setVisibility(View.INVISIBLE);
                                JSONArray date = new JSONArray(json.getString("date"));
                                JSONArray guser = new JSONArray(json.getString("guser"));
                                JSONArray goods = new JSONArray(json.getString("goods"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject order = date.getJSONObject(i);
                                    JSONObject user = guser.getJSONObject(i);
                                    JSONObject good = goods.getJSONObject(i);

                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("gid",good.getInt("gid"));
                                    item.put("oid",order.getInt("oid"));
                                    item.put("time", order.getString("time"));
                                    item.put("price", good.getDouble("price"));
                                    item.put("gname", good.getString("gname"));
                                    item.put("nickname", user.getString("nickname"));
                                    item.put("tel", user.getString("tel"));
                                    item.put("address", user.getString("address"));
                                    mData.add(item);
                                }
                            }
                            else layout_back.setVisibility(View.VISIBLE);
                            SimpleAdapter adapter = new SimpleAdapter(MyorderActivity.this,mData,R.layout.layout_orderitem,
                                    new String[]{"oid","time","price","gname","nickname","tel","address"},
                                    new int[]{R.id.order,R.id.time,R.id.price,R.id.gname,R.id.nickname,R.id.tel,R.id.address});
                            adapter.notifyDataSetChanged();
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                        long id) {
                                    Intent intent = new Intent(MyorderActivity.this, TradingActivity.class);
                                    intent.putExtra("goods",Integer.parseInt(mData.get(position).get("gid").toString()));
                                    intent.putExtra("zhuangtai","daifahuo");
                                    startActivity(intent);
                                }
                            });
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MyorderActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                layout_back.setVisibility(View.INVISIBLE);
                                JSONArray date = new JSONArray(json.getString("date"));
                                JSONArray guser = new JSONArray(json.getString("guser"));
                                JSONArray goods = new JSONArray(json.getString("goods"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject order = date.getJSONObject(i);
                                    JSONObject user = guser.getJSONObject(i);
                                    JSONObject good = goods.getJSONObject(i);

                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("gid",good.getInt("gid"));
                                    item.put("oid",order.getInt("oid"));
                                    item.put("time", order.getString("time"));
                                    item.put("price", good.getDouble("price"));
                                    item.put("gname", good.getString("gname"));
                                    item.put("nickname", user.getString("nickname"));
                                    item.put("tel", user.getString("tel"));
                                    item.put("address", user.getString("address"));
                                    mData.add(item);
                                }
                            }
                            else layout_back.setVisibility(View.VISIBLE);
                            SimpleAdapter adapter = new SimpleAdapter(MyorderActivity.this,mData,R.layout.layout_orderitem,
                                    new String[]{"oid","time","price","gname","nickname","tel","address"},
                                    new int[]{R.id.order,R.id.time,R.id.price,R.id.gname,R.id.nickname,R.id.tel,R.id.address});
                            adapter.notifyDataSetChanged();
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                        long id) {
                                    Intent intent = new Intent(MyorderActivity.this, TradingActivity.class);
                                    intent.putExtra("goods",Integer.parseInt(mData.get(position).get("gid").toString()));
                                    intent.putExtra("zhuangtai","daishouhuo");
                                    startActivity(intent);
                                }
                            });
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MyorderActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                layout_back.setVisibility(View.INVISIBLE);
                                JSONArray date = new JSONArray(json.getString("date"));
                                JSONArray guser = new JSONArray(json.getString("guser"));
                                JSONArray goods = new JSONArray(json.getString("goods"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject order = date.getJSONObject(i);
                                    JSONObject user = guser.getJSONObject(i);
                                    JSONObject good = goods.getJSONObject(i);

                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("gid",good.getInt("gid"));
                                    item.put("oid",order.getInt("oid"));
                                    item.put("time", order.getString("time"));
                                    item.put("price", good.getDouble("price"));
                                    item.put("gname", good.getString("gname"));
                                    item.put("nickname", user.getString("nickname"));
                                    item.put("tel", user.getString("tel"));
                                    item.put("address", user.getString("address"));
                                    mData.add(item);
                                }
                            }
                            else layout_back.setVisibility(View.VISIBLE);
                            SimpleAdapter adapter = new SimpleAdapter(MyorderActivity.this,mData,R.layout.layout_orderitem,
                                    new String[]{"oid","time","price","gname","nickname","tel","address"},
                                    new int[]{R.id.order,R.id.time,R.id.price,R.id.gname,R.id.nickname,R.id.tel,R.id.address});
                            adapter.notifyDataSetChanged();
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                        long id) {
                                    Intent intent = new Intent(MyorderActivity.this, TradingActivity.class);
                                    intent.putExtra("goods",Integer.parseInt(mData.get(position).get("gid").toString()));
                                    intent.putExtra("zhuangtai","chenggong");
                                    startActivity(intent);
                                }
                            });
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MyorderActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                layout_back.setVisibility(View.INVISIBLE);
                                JSONArray date = new JSONArray(json.getString("date"));
                                JSONArray guser = new JSONArray(json.getString("guser"));
                                JSONArray goods = new JSONArray(json.getString("goods"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject order = date.getJSONObject(i);
                                    JSONObject user = guser.getJSONObject(i);
                                    JSONObject good = goods.getJSONObject(i);

                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("gid",good.getInt("gid"));
                                    item.put("oid",order.getInt("oid"));
                                    item.put("time", order.getString("time"));
                                    item.put("price", good.getDouble("price"));
                                    item.put("gname", good.getString("gname"));
                                    item.put("nickname", user.getString("nickname"));
                                    item.put("tel", user.getString("tel"));
                                    item.put("address", user.getString("address"));
                                    mData.add(item);
                                }
                            }
                            else layout_back.setVisibility(View.VISIBLE);
                            SimpleAdapter adapter = new SimpleAdapter(MyorderActivity.this,mData,R.layout.layout_orderitem,
                                    new String[]{"oid","time","price","gname","nickname","tel","address"},
                                    new int[]{R.id.order,R.id.time,R.id.price,R.id.gname,R.id.nickname,R.id.tel,R.id.address});
                            adapter.notifyDataSetChanged();
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                        long id) {
                                    Intent intent = new Intent(MyorderActivity.this, TradingActivity.class);
                                    intent.putExtra("goods",Integer.parseInt(mData.get(position).get("gid").toString()));
                                    intent.putExtra("zhuangtai","shibai");
                                    startActivity(intent);
                                }
                            });
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
    public  void setTrading()
    {
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(uid));
                params.put("state",Integer.toString(1));
                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Uidandstate.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    public void setTrade()
    {
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(uid));
                params.put("state",Integer.toString(2));
                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Uidandstate.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    public  void setTradesuccess()
    {
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(uid));
                params.put("state",Integer.toString(3));
                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Uidandstate.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 2;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    public void setTradefail()
    {
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(uid));
                params.put("state",Integer.toString(4));
                String strUrlpath = getResources().getString(R.string.burl) + "TradeAction_Uidandstate.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 3;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
}
