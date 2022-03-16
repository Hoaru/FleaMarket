package com.doyle.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.doyle.activity.fleamarket.CharityActivity;
import com.doyle.activity.fleamarket.GoodslistActivity;
import com.doyle.activity.fleamarket.PublishActivity;
import com.doyle.activity.fleamarket.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class F3Fragment extends Fragment {

    LinearLayout pgoods,gongyi,song,paimai;
    private OnClickListener listener;
    public F3Fragment() {
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
        return inflater.inflate(R.layout.fragment_f3, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Initview();

    }

    public void Initview()
    {
        pgoods = getActivity().findViewById(R.id.pgoods);
        song = getActivity().findViewById(R.id.song);
        gongyi = getActivity().findViewById(R.id.gongyi);
        paimai = getActivity().findViewById(R.id.paimai);
        listener = new OnClickListener();

        pgoods.setOnClickListener(listener);
        song.setOnClickListener(listener);
        gongyi.setOnClickListener(listener);
        paimai.setOnClickListener(listener);
    }
    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.pgoods://发布商品
                    Intent i = new Intent(getActivity(), PublishActivity.class);
                    i.putExtra("way",1);
                    startActivity(i);
                    break;
                case R.id.song://免费送
                    Intent i1 = new Intent(getActivity(), PublishActivity.class);
                    i1.putExtra("way",2);
                    startActivity(i1);
                    break;
                case R.id.gongyi://发起公益
                    Intent i2 = new Intent(getActivity(), CharityActivity.class);
                    startActivity(i2);
                    break;
                case R.id.paimai://发起拍卖
                    Toast.makeText(getActivity(),"该功能待开发中...",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
