package com.doyle.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.doyle.activity.fleamarket.CharitydetailActivity;
import com.doyle.activity.fleamarket.GoodslistActivity;
import com.doyle.activity.fleamarket.GoodstradeActivity;
import com.doyle.activity.fleamarket.MessageActivity;
import com.doyle.activity.fleamarket.MyCharityActivity;
import com.doyle.activity.fleamarket.MyGoodsActivity;
import com.doyle.activity.fleamarket.MycenterActivity;
import com.doyle.activity.fleamarket.MyorderActivity;
import com.doyle.activity.fleamarket.R;
import com.doyle.activity.fleamarket.SettingActivity;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class F4Fragment extends Fragment {

    LinearLayout gaoping,ji,gong;
    private OnClickListener listener;
    int gid1,gid2;
    int cid;
    public F4Fragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f4, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gid1 = 0;
        gid2 = 0;
        cid = 0;
        getdata();
        gong = getActivity().findViewById(R.id.gong);
        gaoping = getActivity().findViewById(R.id.gaoping);
        ji = getActivity().findViewById(R.id.ji);
        listener = new OnClickListener();
        gong.setOnClickListener(listener);
        gaoping.setOnClickListener(listener);
        ji.setOnClickListener(listener);

    }
    public void getdata()
    {
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                String strUrlpath = getResources().getString(R.string.burl) + "LunboAction_Insert.action";
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
                        Toast.makeText(getActivity(),"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                gid1 = json.getInt("gid1");
                                gid2 = json.getInt("gid2");
                                cid = json.getInt("cid");
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
    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.gaoping:      //高评
                    Intent intent = new Intent(getActivity(),GoodstradeActivity.class);
                    intent.putExtra("gid",gid1);
                    intent.putExtra("state","1");
                    startActivity(intent);
                    break;
                case R.id.gong:      //公益
                    Intent g = new Intent(getActivity(),CharitydetailActivity.class);
                    g.putExtra("cid",cid);
                    startActivity(g);
                    break;
                case R.id.ji://急
                    Intent j = new Intent(getActivity(),GoodstradeActivity.class);
                    j.putExtra("gid",gid2);
                    j.putExtra("state","1");
                    startActivity(j);
                    break;
                default:
                    break;
            }
        }
    }
}
