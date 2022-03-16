package com.doyle.activity.fleamarket;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    int uid;
    SimpleAdapter adapter;
    LinearLayout backlayout ;

    ListView mListView;
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        backlayout = findViewById(R.id.back);
        getdata();
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MessageActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            final JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                final JSONArray date = new JSONArray(json.getString("data"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject goods = date.getJSONObject(i);
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    if (goods.getInt("state")==1)
                                        item.put("image",R.drawable.message_close);
                                    else item.put("image",R.drawable.message_open);
                                    item.put("mid",goods.getInt("mid"));
                                    item.put("title", goods.getString("title"));
                                    item.put("content", goods.getString("content"));
                                    item.put("time", goods.getString("time"));
                                    mData.add(item);
                                }
                                mListView = findViewById(R.id.messagelist);

                                backlayout.setVisibility(View.INVISIBLE);

                                adapter = new SimpleAdapter(MessageActivity.this,mData,R.layout.message_list_item,
                                        new String[]{"image","title","time"},new int[]{R.id.image,R.id.title,R.id.time});
                                mListView.setAdapter(adapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        final int mid = Integer.parseInt(mData.get(position).get("mid").toString());
                                        final LinearLayout message_detail_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.message_detail_layout,null);
                                        TextView title = message_detail_layout.findViewById(R.id.detailtitle);
                                        TextView time = message_detail_layout.findViewById(R.id.detailtime);
                                        TextView content = message_detail_layout.findViewById(R.id.detailcontent);
                                        title.setText(mData.get(position).get("title").toString());
                                        time.setText(mData.get(position).get("time").toString());
                                        content.setText(mData.get(position).get("content").toString());
                                        //一点开就进行状态更改
                                        changestate(mid,2);

                                        new AlertDialog.Builder(MessageActivity.this)
                                                .setView(message_detail_layout)
                                                .setNegativeButton("删除消息",new DialogInterface.OnClickListener() {//设置确定按钮
                                                    @Override//处理取消按钮点击事件
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        changestate(mid,0);
                                                    }
                                                })
                                                .create()
                                                .show();
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
                case 1:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(MessageActivity.this,"请检查网络链接",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            final JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                getdata();
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                default:
                    break;
            }
        }
    };
    public void getdata()
    {
        mData.clear();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        SharedPreferences pref = MessageActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);

        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(uid));
                String strUrlpath = getResources().getString(R.string.burl) + "MessageAction_Findbyuid.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    public void changestate(final int mid,final int state)
    {
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mid",Integer.toString(mid));
                params.put("state",Integer.toString(state));
                String strUrlpath = getResources().getString(R.string.burl) + "MessageAction_Findbymid.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
}
