package com.doyle.activity.fleamarket;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.doyle.adapter.CharitylistAdapter;
import com.doyle.adapter.GoodslistAdapter;
import com.doyle.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CharitylistActivity extends AppCompatActivity {
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    ListView charitylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charitylist);
        getdata();
    }
    public void getdata()
    {
        mData.clear();
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                String strUrlpath = getResources().getString(R.string.burl) + "CharityAction_Find.action";
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
                        Toast.makeText(CharitylistActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                final JSONArray data = new JSONArray(json.getString("data"));
                                for(int i=0;i<data.length();i++)
                                {
                                    JSONObject charity = data.getJSONObject(i);
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("cid", charity.getInt("cid"));
                                    item.put("name", charity.getString("name"));
                                    item.put("content", "     "+charity.getString("content"));
                                    item.put("imageurl", getResources().getString(R.string.burl)+charity.getString("imageurl"));
                                    item.put("joinnum", charity.getInt("joinnum"));
                                    item.put("scannum", charity.getInt("scannum"));
                                    mData.add(item);
                                }
                                CharitylistAdapter adapter = new CharitylistAdapter(CharitylistActivity.this,mData);
                                adapter.notifyDataSetChanged();
                                charitylist = findViewById(R.id.charitylist);
                                charitylist.setAdapter(adapter);
                                charitylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        Intent intent = new Intent(CharitylistActivity.this,CharitydetailActivity.class);
                                        intent.putExtra("cid",Integer.parseInt(mData.get(position).get("cid").toString()));
                                        startActivity(intent);

                                    }
                                });
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
}
