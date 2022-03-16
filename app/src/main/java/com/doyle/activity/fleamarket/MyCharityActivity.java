package com.doyle.activity.fleamarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class MyCharityActivity extends AppCompatActivity {
    int uid;
    SimpleAdapter adapter;
    LinearLayout backlayout;

    ListView mListView;
    ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_charity);
        backlayout = findViewById(R.id.back);
        getdata();
    }

    public void getdata() {
        SharedPreferences pref = MyCharityActivity.this.getSharedPreferences("data", MODE_PRIVATE);
        uid = pref.getInt("uid", 0);

        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(uid));
                String strUrlpath = getResources().getString(R.string.burl) + "CharityAction_Delete.action";
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
                    if (msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MyCharityActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    else {
                        try {
                            final JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if (back == 1) {
                                System.out.println("进来没");
                                final JSONArray date = new JSONArray(json.getString("data"));
                                for (int i = 0; i < date.length(); i++) {
                                    JSONObject charity = date.getJSONObject(i);
                                    Map<String, Object> item = new HashMap<String, Object>();
                                    if (charity.getInt("state") == 1)
                                        item.put("image", R.drawable.youxiao);
                                    else item.put("image", R.drawable.shixiao);
                                    item.put("state", charity.getInt("state"));
                                    item.put("cid", charity.getInt("cid"));
                                    item.put("time", charity.getString("endtime"));
                                    item.put("name", charity.getString("name"));
                                    mData.add(item);
                                }
                                mListView = findViewById(R.id.charitylist);

                                backlayout.setVisibility(View.INVISIBLE);

                                adapter = new SimpleAdapter(MyCharityActivity.this, mData, R.layout.mycharitylist_item,
                                        new String[]{"image", "name", "time"}, new int[]{R.id.image, R.id.title, R.id.time});
                                mListView.setAdapter(adapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        if(Integer.parseInt(mData.get(position).get("state").toString())==1)
                                        {
                                            Intent intent = new Intent(MyCharityActivity.this,CharitydetailActivity.class);
                                            intent.putExtra("cid",Integer.parseInt(mData.get(position).get("cid").toString()));
                                            startActivity(intent);
                                        }
                                        else
                                            Toast.makeText(MyCharityActivity.this, "该项目还未生效或已经失效！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else backlayout.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
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
