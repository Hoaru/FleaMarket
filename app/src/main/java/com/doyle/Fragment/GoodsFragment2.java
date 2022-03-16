package com.doyle.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.doyle.activity.fleamarket.GoodsdetailActivity;
import com.doyle.activity.fleamarket.R;
import com.doyle.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class GoodsFragment2 extends Fragment {
    ListView mListView;
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();;
    LinearLayout backlayout;
    private int uid;


    public GoodsFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goods_fragment2, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backlayout = getActivity().findViewById(R.id.back);
        mData.clear();
        getdata();
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                        Toast.makeText(getActivity(),"请检查网络",Toast.LENGTH_SHORT).show();
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                final JSONArray date = new JSONArray(json.getString("date"));
                                for(int i=0;i<date.length();i++)
                                {
                                    JSONObject goods = date.getJSONObject(i);
                                    System.out.println("结果："+goods.getString("gname")+" "+goods.getString("gid")
                                            +"  "+goods.getString("price")+"  "+goods.getString("time"));

                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("gid",goods.getString("gid"));
                                    item.put("gname", goods.getString("gname"));
                                    item.put("price", goods.getString("price"));
                                    item.put("time", goods.getString("time"));
                                    mData.add(item);
                                }
                                mListView = getActivity().findViewById(R.id.listview2);
                                SimpleAdapter adapter = new SimpleAdapter(getActivity(),mData,R.layout.layout_goods,
                                        new String[]{"gid","gname","price","time"},new int[]{R.id.gid,R.id.gname,R.id.price,R.id.time});
                                adapter.notifyDataSetChanged();
                                backlayout.setVisibility(View.INVISIBLE);
                                mListView.setAdapter(adapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        try
                                        {
                                            int Goods = date.getJSONObject(position).getInt("gid");
                                            Intent intent = new Intent(getActivity(), GoodsdetailActivity.class);
                                            intent.putExtra("state",2);
                                            intent.putExtra("goods",Goods);
                                            startActivity(intent);

                                        }catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }else backlayout.setVisibility(View.VISIBLE);
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

        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        uid = pref.getInt("uid",0);

        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",Integer.toString(uid));
                params.put("state","1");
                String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_Findbyuid.action";
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
