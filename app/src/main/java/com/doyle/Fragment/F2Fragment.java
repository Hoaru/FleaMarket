package com.doyle.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.activity.fleamarket.GoodslistActivity;
import com.doyle.activity.fleamarket.MainActivity;
import com.doyle.activity.fleamarket.MyGoodsActivity;
import com.doyle.activity.fleamarket.MycenterActivity;
import com.doyle.activity.fleamarket.MyorderActivity;
import com.doyle.activity.fleamarket.PublishActivity;
import com.doyle.activity.fleamarket.R;
import com.doyle.activity.fleamarket.SettingActivity;
import com.doyle.util.HttpUtils;
import com.doyle.util.ImageDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class F2Fragment extends Fragment {
    LinearLayout fenlei1,fenlei2,fenlei3;
    LinearLayout fenlei4,fenlei5,fenlei6,fenlei7,fenlei8,fenlei9,fenlei10,fenlei11;
    TextView fenlei1_text,fenlei2_text,fenlei3_text,fenlei4_text1,fenlei5_text1,fenlei6_text1,fenlei7_text1,fenlei8_text1,fenlei9_text1,fenlei10_text1,fenlei11_text1;
    TextView fenlei4_text2,fenlei5_text2,fenlei6_text2,fenlei7_text2,fenlei8_text2,fenlei9_text2,fenlei10_text2,fenlei11_text2;
    private OnClickListener listener;
    public F2Fragment() {
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
        return inflater.inflate(R.layout.fragment_f2, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Initview();
        getdata();
    }
    public void Initview()
    {
        fenlei1 = getActivity().findViewById(R.id.fenlei1);
        fenlei2 = getActivity().findViewById(R.id.fenlei2);
        fenlei3 = getActivity().findViewById(R.id.fenlei3);
        fenlei4 = getActivity().findViewById(R.id.fenlei4);
        fenlei5 = getActivity().findViewById(R.id.fenlei5);
        fenlei6 = getActivity().findViewById(R.id.fenlei6);
        fenlei7 = getActivity().findViewById(R.id.fenlei7);
        fenlei8 = getActivity().findViewById(R.id.fenlei8);
        fenlei9 = getActivity().findViewById(R.id.fenlei9);
        fenlei10 = getActivity().findViewById(R.id.fenlei10);
        fenlei11 = getActivity().findViewById(R.id.fenlei11);

        fenlei1_text = getActivity().findViewById(R.id.fenlei1_text);
        fenlei2_text = getActivity().findViewById(R.id.fenlei2_text);
        fenlei3_text = getActivity().findViewById(R.id.fenlei3_text);

        fenlei4_text1 = getActivity().findViewById(R.id.fenlei4_text1);
        fenlei5_text1 = getActivity().findViewById(R.id.fenlei5_text1);
        fenlei6_text1 = getActivity().findViewById(R.id.fenlei6_text1);
        fenlei7_text1 = getActivity().findViewById(R.id.fenlei7_text1);
        fenlei8_text1 = getActivity().findViewById(R.id.fenlei8_text1);
        fenlei9_text1 = getActivity().findViewById(R.id.fenlei9_text1);
        fenlei10_text1 = getActivity().findViewById(R.id.fenlei10_text1);
        fenlei11_text1 = getActivity().findViewById(R.id.fenlei11_text1);
        fenlei4_text2 = getActivity().findViewById(R.id.fenlei4_text2);
        fenlei5_text2 = getActivity().findViewById(R.id.fenlei5_text2);
        fenlei6_text2 = getActivity().findViewById(R.id.fenlei6_text2);
        fenlei7_text2 = getActivity().findViewById(R.id.fenlei7_text2);
        fenlei8_text2 = getActivity().findViewById(R.id.fenlei8_text2);
        fenlei9_text2 = getActivity().findViewById(R.id.fenlei9_text2);
        fenlei10_text2 = getActivity().findViewById(R.id.fenlei10_text2);
        fenlei11_text2 = getActivity().findViewById(R.id.fenlei11_text2);

        listener = new OnClickListener();

        fenlei1.setOnClickListener(listener);
        fenlei2.setOnClickListener(listener);
        fenlei3.setOnClickListener(listener);
        fenlei4.setOnClickListener(listener);
        fenlei5.setOnClickListener(listener);
        fenlei6.setOnClickListener(listener);
        fenlei7.setOnClickListener(listener);
        fenlei8.setOnClickListener(listener);
        fenlei9.setOnClickListener(listener);
        fenlei10.setOnClickListener(listener);
        fenlei11.setOnClickListener(listener);


    }
    public void getdata()
    {
        //线程
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                String strUrlpath = getResources().getString(R.string.burl) + "LunboAction_Fenlei.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("分类结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    private class OnClickListener implements View.OnClickListener {
        Intent intent = new Intent(getActivity(), GoodslistActivity.class);
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fenlei1:intent.putExtra("name",fenlei1_text.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei2:
                    intent.putExtra("name",fenlei2_text.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei3:
                    intent.putExtra("name",fenlei3_text.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei4:
                    intent.putExtra("name",fenlei4_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei5:
                    intent.putExtra("name",fenlei5_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei6:
                    intent.putExtra("name",fenlei6_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei7:
                    intent.putExtra("name",fenlei7_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei8:
                    intent.putExtra("name",fenlei8_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei9:
                    intent.putExtra("name",fenlei9_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei10:
                    intent.putExtra("name",fenlei10_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
                    break;
                case R.id.fenlei11:
                    intent.putExtra("name",fenlei11_text1.getText());
                    intent.putExtra("searchway","2");
                    startActivity(intent);
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
                    {
                        Toast.makeText(getActivity(),"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try
                        {
                            JSONObject json =new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back==0)
                            {
                                Toast.makeText(getActivity(),"加载失败！",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONArray list = new JSONArray(json.getString("data"));
                                fenlei1_text.setText(list.getJSONObject(0).getString("tname"));
                                fenlei2_text.setText(list.getJSONObject(1).getString("tname"));
                                fenlei3_text.setText(list.getJSONObject(2).getString("tname"));
                                fenlei4_text1.setText(list.getJSONObject(3).getString("tname"));
                                fenlei5_text1.setText(list.getJSONObject(4).getString("tname"));
                                fenlei6_text1.setText(list.getJSONObject(5).getString("tname"));
                                fenlei7_text1.setText(list.getJSONObject(6).getString("tname"));
                                fenlei8_text1.setText(list.getJSONObject(7).getString("tname"));
                                fenlei9_text1.setText(list.getJSONObject(8).getString("tname"));
                                fenlei10_text1.setText(list.getJSONObject(9).getString("tname"));
                                fenlei11_text1.setText(list.getJSONObject(10).getString("tname"));
                                fenlei4_text2.setText(list.getJSONObject(3).getString("smalltype"));
                                fenlei5_text2.setText(list.getJSONObject(4).getString("smalltype"));
                                fenlei6_text2.setText(list.getJSONObject(5).getString("smalltype"));
                                fenlei7_text2.setText(list.getJSONObject(6).getString("smalltype"));
                                fenlei8_text2.setText(list.getJSONObject(7).getString("smalltype"));
                                fenlei9_text2.setText(list.getJSONObject(8).getString("smalltype"));
                                fenlei10_text2.setText(list.getJSONObject(9).getString("smalltype"));
                                fenlei11_text2.setText(list.getJSONObject(10).getString("smalltype"));
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
