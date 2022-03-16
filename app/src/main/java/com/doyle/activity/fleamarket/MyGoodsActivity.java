package com.doyle.activity.fleamarket;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doyle.Fragment.GoodsFragment1;
import com.doyle.Fragment.GoodsFragment2;
import com.doyle.Fragment.GoodsFragment3;
import com.doyle.Fragment.GoodsFragment4;
import com.doyle.Fragment.GoodsFragment5;
import com.doyle.adapter.FragmentAdapter;
import com.doyle.adapter.FragmentAdapter_goods;

import java.util.ArrayList;

public class MyGoodsActivity extends FragmentActivity {
    //建立5个Fragment
    private GoodsFragment1 goodsFragment11;
    private GoodsFragment2 goodsFragment12;
    private GoodsFragment3 goodsFragment13;
    private GoodsFragment4 goodsFragment14;
    private GoodsFragment5 goodsFragment15;

    //定义一个ViewPager容器
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private FragmentAdapter_goods mAdapter;
    //依次获得ImageView与TextView
    private LinearLayout check;
    private LinearLayout sale;
    private LinearLayout trade;
    private LinearLayout success;
    private LinearLayout failure;
    private TextView check_text,sale_text,trade_text,success_text,failure_text;
    //定义颜色值
    private int Gray = 0xFF999999;
    private int Green =0xFF45C01A;
    //定义FragmentManager对象
    public FragmentManager fManager;
    //定义一个Onclick全局对象
    public OnClick myclick;
    public PageChangeListener myPageChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goods);
        fManager = getSupportFragmentManager();
        initViewPager();
        initViews();
        initState();

    }
    private void initViewPager()
    {
        fragmentsList = new ArrayList<Fragment>();
        goodsFragment11 = new GoodsFragment1();
        goodsFragment12 = new GoodsFragment2();
        goodsFragment13 = new GoodsFragment3();
        goodsFragment14 = new GoodsFragment4();
        goodsFragment15 = new GoodsFragment5();


        fragmentsList.add(goodsFragment11);
        fragmentsList.add(goodsFragment12);
        fragmentsList.add(goodsFragment13);
        fragmentsList.add(goodsFragment14);
        fragmentsList.add(goodsFragment15);
        mAdapter = new FragmentAdapter_goods(fManager,fragmentsList);
    }
    private void initViews() {
        myclick = new OnClick();
        myPageChange = new PageChangeListener();
        mPager = findViewById(R.id.goodsviewpager);

        check = findViewById(R.id.check);
        trade = findViewById(R.id.trade);
        sale = findViewById(R.id.sale);
        success = findViewById(R.id.success);
        failure = findViewById(R.id.failure);

        check_text = findViewById(R.id.check_text);
        trade_text = findViewById(R.id.trade_text);
        sale_text = findViewById(R.id.sale_text);
        success_text = findViewById(R.id.success_text);
        failure_text = findViewById(R.id.failure_text);

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(myPageChange);

        check.setOnClickListener(myclick);
        trade.setOnClickListener(myclick);
        sale.setOnClickListener(myclick);
        failure.setOnClickListener(myclick);
        success.setOnClickListener(myclick);
    }
    //定义一个设置初始状态的方法
    private void initState()
    {
        check_text.setTextColor(Green);
        trade_text.setTextColor(Gray);
        sale_text.setTextColor(Gray);
        success_text.setTextColor(Gray);
        failure_text.setTextColor(Gray);
        mPager.setCurrentItem(0);
    }

    public class OnClick implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            clearChioce();
            iconChange(view.getId());
        }
    }

    public class PageChangeListener implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
            if(arg0 == 2)
            {
                int i = mPager.getCurrentItem();
                clearChioce();
                iconChange(i);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageSelected(int index){
        }

    }
    //建立一个清空选中状态的方法
    public void clearChioce()
    {
        check_text.setTextColor(Gray);
        trade_text.setTextColor(Gray);
        sale_text.setTextColor(Gray);
        success_text.setTextColor(Gray);
        failure_text.setTextColor(Gray);
    }

    //定义一个底部导航栏图标变化的方法
    public void iconChange(int num)
    {
        switch (num) {
            case R.id.check:case 0:
                check_text.setTextColor(Green);
                mPager.setCurrentItem(0);
                break;
            case R.id.trade:case 2:
                trade_text.setTextColor(Green);
                mPager.setCurrentItem(2);
                break;
            case R.id.sale:case 1:
                sale_text.setTextColor(Green);
                mPager.setCurrentItem(1);
                break;
            case R.id.failure:case 4:
                failure_text.setTextColor(Green);
                mPager.setCurrentItem(4);
                break;
            case R.id.success:case 3:
                success_text.setTextColor(Green);
                mPager.setCurrentItem(3);
                break;
            default:
                break;
        }
    }
}
