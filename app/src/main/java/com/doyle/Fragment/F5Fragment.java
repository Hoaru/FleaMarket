package com.doyle.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.activity.fleamarket.LoginActivity;
import com.doyle.activity.fleamarket.MainActivity;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class F5Fragment extends Fragment {
    ImageView touxiang;
    TextView nicheng, uid;
    LinearLayout geren,charity,order,mygoods,xiaoxi,setting;
    private OnClickListener listener;


    public F5Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f5, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Initview();

    }

    public void Initview() {
        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        String nickname = pref.getString("nickname","null");
        String name = pref.getString("name","null");
        String image = pref.getString("image","");

        touxiang = getActivity().findViewById(R.id.touxiang);
        nicheng = getActivity().findViewById(R.id.nicheng);
        uid = getActivity().findViewById(R.id.uid);

        geren  = getActivity().findViewById(R.id.geren);
        charity = getActivity().findViewById(R.id.charity);
        order = getActivity().findViewById(R.id.order);
        mygoods = getActivity().findViewById(R.id.mygoods);
        xiaoxi = getActivity().findViewById(R.id.xiaoxi);
        setting = getActivity().findViewById(R.id.setting);

        listener = new OnClickListener();
        if(!image.isEmpty())
        {
            downLoad(getResources().getString(R.string.burl)+image);
        }
        order.setOnClickListener(listener);
        geren.setOnClickListener(listener);
        mygoods.setOnClickListener(listener);
        setting.setOnClickListener(listener);
        xiaoxi.setOnClickListener(listener);
        charity.setOnClickListener(listener);
        nicheng.setText(nickname);
        uid.setText("账号："+name);
    }

    /*
      下载头像图片
     */
    private  void downLoad(final String path)
    {
        new Thread() {
            @Override
            public void run() {
              String back =  HttpUtils.getInputStream(path);
              Message message = new Message();
              message.what = 0;
              message.obj = back;
              handler.sendMessage(message);
            }
        }.start();
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.geren:      //个人资料
                    Intent i = new Intent(getActivity(), MycenterActivity.class);
                    startActivity(i);
                    break;
                case R.id.order:      //订单
                    Intent o = new Intent(getActivity(), MyorderActivity.class);
                    startActivity(o);
                    break;
                case R.id.mygoods://我的商品
                    Intent m = new Intent(getActivity(), MyGoodsActivity.class);
                    startActivity(m);
                    break;
                case R.id.setting://设置
                    Intent x = new Intent(getActivity(), SettingActivity.class);
                    startActivity(x);
                    break;
                case R.id.xiaoxi://设置
                    Intent xiao = new Intent(getActivity(), MessageActivity.class);
                    startActivity(xiao);
                    break;
                case R.id.charity://设置
                    Intent cha = new Intent(getActivity(), MyCharityActivity.class);
                    startActivity(cha);
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
                    if(!msg.obj.toString().trim().isEmpty())
                    {
                        Bitmap bitmap = ImageDeal.String2Bitmap(msg.obj.toString());
                        touxiang.setImageBitmap(bitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
