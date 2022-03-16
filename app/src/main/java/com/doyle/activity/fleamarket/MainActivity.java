package com.doyle.activity.fleamarket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.Fragment.F1Fragment;
import com.doyle.Fragment.F2Fragment;
import com.doyle.Fragment.F3Fragment;
import com.doyle.Fragment.F4Fragment;
import com.doyle.Fragment.F5Fragment;
import com.doyle.adapter.FragmentAdapter;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    //定义四个Fragment
    private F1Fragment f1;
    private F2Fragment f2;
    private F3Fragment f3;
    private F4Fragment f4;
    private F5Fragment f5;
    //定义一个ViewPager容器
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private FragmentAdapter mAdapter;
    //依次获得ImageView与TextView
//    private Button mainpage;
//    private Button type;
//    private Button sale;
//    private Button find;
//    private Button me;

    private LinearLayout mainpage_layout,type_layout,sale_layout,find_layout,me_layout;
    private TextView mainpage_text,type_text,sale_text,find_text,me_text;

    private ImageView mainpage_image,type_image,sale_image,find_image,me_image;
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
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();

        initViewPager();
        initViews();
        initState();
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        boolean login = pref.getBoolean("login",false);
        int uid = pref.getInt("uid",0);//第二个参数为默认值
        String name = pref.getString("name","");//第二个参数为默认值
        String pwd = pref.getString("pwd","");//第二个参数为默认值
     //   Toast.makeText(MainActivity.this,"状态"+login+"账号"+name+"密码"+pwd+"ID:"+uid,Toast.LENGTH_LONG).show();

    }
    private void initViewPager()
    {
        fragmentsList = new ArrayList<Fragment>();
        f1 = new F1Fragment();
        f2 = new F2Fragment();
        f3 = new F3Fragment();
        f4 = new F4Fragment();
        f5 = new F5Fragment();
        fragmentsList.add(f1);
        fragmentsList.add(f2);
        fragmentsList.add(f3);
        fragmentsList.add(f4);
        fragmentsList.add(f5);
        mAdapter = new FragmentAdapter(fManager,fragmentsList);
    }
    private void initViews() {
        myclick = new OnClick();
        myPageChange = new PageChangeListener();
        mPager = findViewById(R.id.viewpager);


        mainpage_layout = findViewById(R.id.mainpage_layout);
        mainpage_image = findViewById(R.id.mainpage_image);
        mainpage_text = findViewById(R.id.mainpage_text);

        type_layout = findViewById(R.id.type_layout);
        type_image = findViewById(R.id.type_image);
        type_text = findViewById(R.id.type_text);

        sale_layout = findViewById(R.id.sale_layout);
        sale_image = findViewById(R.id.sale_image);
        sale_text = findViewById(R.id.sale_text);

        find_layout = findViewById(R.id.find_layout);
        find_image = findViewById(R.id.find_image);
        find_text = findViewById(R.id.find_text);

        me_layout = findViewById(R.id.me_layout);
        me_image = findViewById(R.id.me_image);
        me_text = findViewById(R.id.me_text);

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(myPageChange);

        mainpage_layout.setOnClickListener(myclick);
        type_layout.setOnClickListener(myclick);
        sale_layout.setOnClickListener(myclick);
        find_layout.setOnClickListener(myclick);
        me_layout.setOnClickListener(myclick);
    }
    //定义一个设置初始状态的方法
    private void initState()
    {
        mainpage_text.setTextColor(Green);
        mainpage_image.setImageResource(R.drawable.mainpage_botton_green);
        type_text.setTextColor(Gray);
        type_image.setImageResource(R.drawable.fenlei_botton);
        sale_text.setTextColor(Gray);
        sale_image.setImageResource(R.drawable.fashou_botton);
        find_text.setTextColor(Gray);
        find_image.setImageResource(R.drawable.find_botton);
        me_text.setTextColor(Gray);
        me_image.setImageResource(R.drawable.me_botton);
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
        public void onPageSelected(int index){}

    }
    //建立一个清空选中状态的方法
    public void clearChioce()
    {
        mainpage_text.setTextColor(Gray);
        mainpage_image.setImageResource(R.drawable.main_botton);
        type_text.setTextColor(Gray);
        type_image.setImageResource(R.drawable.fenlei_botton);
        sale_text.setTextColor(Gray);
        sale_image.setImageResource(R.drawable.fashou_botton);
        find_text.setTextColor(Gray);
        find_image.setImageResource(R.drawable.find_botton);
        me_text.setTextColor(Gray);
        me_image.setImageResource(R.drawable.me_botton);
    }

    //定义一个底部导航栏图标变化的方法
    public void iconChange(int num)
    {
        switch (num) {
            case R.id.mainpage_layout:case 0:
                mainpage_text.setTextColor(Green);
                mainpage_image.setImageResource(R.drawable.mainpage_botton_green);
                mPager.setCurrentItem(0);
                break;
            case R.id.type_layout:case 1:
                type_text.setTextColor(Green);
                type_image.setImageResource(R.drawable.fenlei_botton_green);
                mPager.setCurrentItem(1);
                break;
            case R.id.sale_layout:case 2:
                sale_text.setTextColor(Green);
                sale_image.setImageResource(R.drawable.fashou_botton_green);
                mPager.setCurrentItem(2);
                break;
            case R.id.find_layout:case 3:
                find_text.setTextColor(Green);
                find_image.setImageResource(R.drawable.find_botton_green);
                mPager.setCurrentItem(3);
                break;
            case R.id.me_layout:case 4:
                me_text.setTextColor(Green);
                me_image.setImageResource(R.drawable.me_botton_green);
                mPager.setCurrentItem(4);
                break;
        }
    }
}
