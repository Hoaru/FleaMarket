package com.doyle.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.doyle.activity.fleamarket.CharitylistActivity;
import com.doyle.activity.fleamarket.GoodslistActivity;
import com.doyle.activity.fleamarket.GoodstradeActivity;
import com.doyle.activity.fleamarket.MycenterActivity;
import com.doyle.activity.fleamarket.R;
import com.doyle.util.AsyncBitmapLoader;
import com.doyle.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class F1Fragment extends Fragment implements ViewPager.OnPageChangeListener{
    Button search;
    EditText searchcontext;
    private OnClickListener listener;
    LinearLayout layout1,layout2,layout3,layout4,layout5,layout6,newmore;
    LinearLayout jilayout1,jilayout2,jilayout3,jimore;
    LinearLayout Botton1,Botton2,Botton3,Botton4;
    Boolean iscrossCreat = false;
    int jiid1,jiid2,jiid3;
    //热销榜
    TextView textView11,textView12,textView21,textView22,textView31,textView32;

    //最新发布控件
    TextView gname [] = new TextView[6];
    TextView hownew [] = new TextView[6];
    TextView getway [] = new TextView[6];
    TextView price [] = new TextView[6];
    TextView time [] = new TextView[6];
    ImageView image [] = new ImageView[6];
    int newgid[] = new int[6];
    int n=0;
    //轮播
    private ViewPager vp;
    private LinearLayout ll_point;
    private TextView tv_desc;
    private int[] imageResIds; //存放图片资源id的数组
    private String[] imageResUrl; //存放图片资源id的数组
    private ArrayList<ImageView> imageViews; //存放图片的集合
    private String[] contentDescs; //图片内容描述
    private int lastPosition;//位置
    private boolean isRunning = true; //viewpager是否在自动轮询

    public F1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f1, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iscrossCreat = true;
        Thread getemrgent = new GetEmergentThread();
        Thread getnew = new GetNewThread();
        initview();
        getemrgent.start();
        getnew.start();
        getdata();
    }
    //Handle处理
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString().trim().equals("-1"))
                    {
                        initmorendata();
                        initadapter();
                        Thread lunbo = new LunboThread();
                        lunbo.start();
                    }
                    else {
                        try {
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if (back == 1) {
                                imageResUrl = new String[5];
                                contentDescs = new String[5];
                                JSONArray lunbo = new JSONArray(json.getString("data"));
                                for(int i=0;i<lunbo.length();i++)
                                {
                                    JSONObject lun =lunbo.getJSONObject(i);
                                    imageResUrl[i] = lun.getString("imageurl");
                                    contentDescs[i] = lun.getString("name");
                                }
                                initdata();
                            }
                            else
                            {
                                initmorendata();
                                initadapter();
                                Thread lunbo = new LunboThread();
                                lunbo.start();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    n++;
                    Bitmap bitmap=(Bitmap) msg.obj;
                    ImageView iv=new ImageView(getActivity());
                    View pointView;

                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(bitmap);
                    //把图片添加到集合里
                    imageViews.add(iv);

                    //加小白点，指示器（这里的小圆点定义在了drawable下的选择器中了，也可以用小图片代替）
                    pointView = new View(getActivity());
                    pointView.setBackgroundResource(R.drawable.point_selector); //使用选择器设置背景
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, 8);
                    if (n != 0){
                        //如果不是第一个点，则设置点的左边距
                        layoutParams.leftMargin = 10;
                    }
                    pointView.setEnabled(false); //默认都是暗色的
                    ll_point.addView(pointView, layoutParams);

                    if(n==5)
                    {
                        initadapter();
                        Thread lunbo = new LunboThread();
                        lunbo.start();
                    }
                    break;
                case 2:
                    if(!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                final JSONArray data = new JSONArray(json.getString("data"));
                                final JSONArray type = new JSONArray(json.getString("type"));

                                textView11.setText("【"+type.getString(0)+"】");
                                textView12.setText(data.getJSONObject(0).getString("detail").toString().trim());
                                jiid1 = data.getJSONObject(0).getInt("gid");
                                textView21.setText("【"+type.getString(1)+"】");
                                textView22.setText(data.getJSONObject(1).getString("detail").toString());
                                jiid2 = data.getJSONObject(1).getInt("gid");
                                textView31.setText("【"+type.getString(2)+"】");
                                textView32.setText(data.getJSONObject(2).getString("detail").toString());
                                jiid3 = data.getJSONObject(2).getInt("gid");
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    if(!msg.obj.toString().trim().equals("-1"))
                     {
                        try{
                            JSONObject json = new JSONObject(msg.obj.toString().trim());
                            int back = json.getInt("code");
                            if(back == 1)
                            {
                                final JSONArray data = new JSONArray(json.getString("data"));

                                for(int i=0;i<6;i++)
                                {
                                    newgid[i] =data.getJSONObject(i).getInt("gid");
                                    gname[i].setText(data.getJSONObject(i).getString("gname"));
                                    hownew[i].setText(data.getJSONObject(i).getString("hownew"));
                                    price[i].setText("￥"+Double.toString(data.getJSONObject(i).getDouble("price")));
                                    time[i].setText(data.getJSONObject(i).getString("time").toString());
                                    if(data.getJSONObject(i).getInt("getway")==1)
                                       getway[i].setText("送货上门");
                                    else getway[i].setText("自己来取");
                                    AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
                                    Bitmap newbitmap=asyncBitmapLoader.loadBitmap(image[i], getResources().getString(R.string.burl)+data.getJSONObject(i).getString("imageurl"), new AsyncBitmapLoader.ImageCallBack() {

                                        @Override
                                        public void imageLoad(ImageView imageView, Bitmap bitmap) {
                                            // TODO Auto-generated method stub
                                            imageView.setImageBitmap(bitmap);
                                        }
                                    });
                                    if(newbitmap == null)
                                    {
                                        image[i].setImageResource(R.drawable.goodsmoren);
                                    }
                                    else
                                    {
                                        image[i].setImageBitmap(newbitmap);
                                    }
                                }
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
    @Override
    public void onStart() {
        super.onStart();
        if (!iscrossCreat)
        {
            Thread lunbo = new LunboThread();
            lunbo.start();
        }
    }
    public void initview()
    {
        //初始化轮播
        //初始化放小圆点的控件
        ll_point = (LinearLayout) getActivity().findViewById(R.id.ll_point);
        //初始化ViewPager控件
        vp = (ViewPager) getActivity().findViewById(R.id.vp);
        //设置ViewPager的滚动监听

        vp.setOnPageChangeListener(this);
        //显示图片描述信息的控件
        tv_desc = (TextView) getActivity().findViewById(R.id.tv_desc);

        search = getActivity().findViewById(R.id.search);
        searchcontext = getActivity().findViewById(R.id.searchcontext);
        //急
        textView11 = getActivity().findViewById(R.id.text11);
        textView12 = getActivity().findViewById(R.id.text12);
        textView21 = getActivity().findViewById(R.id.text21);
        textView22 = getActivity().findViewById(R.id.text22);
        textView31 = getActivity().findViewById(R.id.text31);
        textView32 = getActivity().findViewById(R.id.text32);
        //最新发布
        gname[0] = getActivity().findViewById(R.id.gname1);
        gname[1] = getActivity().findViewById(R.id.gname2);
        gname[2] = getActivity().findViewById(R.id.gname3);
        gname[3] = getActivity().findViewById(R.id.gname4);
        gname[4] = getActivity().findViewById(R.id.gname5);
        gname[5] = getActivity().findViewById(R.id.gname6);

        hownew[0] = getActivity().findViewById(R.id.hownew1);
        hownew[1] = getActivity().findViewById(R.id.hownew2);
        hownew[2] = getActivity().findViewById(R.id.hownew3);
        hownew[3] = getActivity().findViewById(R.id.hownew4);
        hownew[4] = getActivity().findViewById(R.id.hownew5);
        hownew[5] = getActivity().findViewById(R.id.hownew6);

        getway[0] = getActivity().findViewById(R.id.getway1);
        getway[1] = getActivity().findViewById(R.id.getway2);
        getway[2] = getActivity().findViewById(R.id.getway3);
        getway[3] = getActivity().findViewById(R.id.getway4);
        getway[4] = getActivity().findViewById(R.id.getway5);
        getway[5] = getActivity().findViewById(R.id.getway6);

        price[0] = getActivity().findViewById(R.id.price1);
        price[1] = getActivity().findViewById(R.id.price2);
        price[2] = getActivity().findViewById(R.id.price3);
        price[3] = getActivity().findViewById(R.id.price4);
        price[4] = getActivity().findViewById(R.id.price5);
        price[5] = getActivity().findViewById(R.id.price6);

        time[0] = getActivity().findViewById(R.id.time1);
        time[1] = getActivity().findViewById(R.id.time2);
        time[2] = getActivity().findViewById(R.id.time3);
        time[3] = getActivity().findViewById(R.id.time4);
        time[4] = getActivity().findViewById(R.id.time5);
        time[5] = getActivity().findViewById(R.id.time6);

        image[0] = getActivity().findViewById(R.id.image1);
        image[1] = getActivity().findViewById(R.id.image2);
        image[2] = getActivity().findViewById(R.id.image3);
        image[3] = getActivity().findViewById(R.id.image4);
        image[4] = getActivity().findViewById(R.id.image5);
        image[5] = getActivity().findViewById(R.id.image6);

        layout1 = getActivity().findViewById(R.id.layout1);
        layout2 = getActivity().findViewById(R.id.layout2);
        layout3 = getActivity().findViewById(R.id.layout3);
        layout4 = getActivity().findViewById(R.id.layout4);
        layout5 = getActivity().findViewById(R.id.layout5);
        layout6 = getActivity().findViewById(R.id.layout6);
        newmore = getActivity().findViewById(R.id.newmore);

        jilayout1 = getActivity().findViewById(R.id.jilayout1);
        jilayout2 = getActivity().findViewById(R.id.jilayout2);
        jilayout3 = getActivity().findViewById(R.id.jilayout3);
        jimore = getActivity().findViewById(R.id.jimore);

        Botton1 = getActivity().findViewById(R.id.botton1);
        Botton2 = getActivity().findViewById(R.id.botton2);
        Botton3 = getActivity().findViewById(R.id.botton3);
        Botton4 = getActivity().findViewById(R.id.botton4);


        listener = new OnClickListener();
        layout1.setOnClickListener(listener);
        layout2.setOnClickListener(listener);
        layout3.setOnClickListener(listener);
        layout4.setOnClickListener(listener);
        layout5.setOnClickListener(listener);
        layout6.setOnClickListener(listener);
        newmore.setOnClickListener(listener);
        jilayout1.setOnClickListener(listener);
        jilayout2.setOnClickListener(listener);
        jilayout3.setOnClickListener(listener);
        jimore.setOnClickListener(listener);
        Botton1.setOnClickListener(listener);
        Botton2.setOnClickListener(listener);
        Botton3.setOnClickListener(listener);
        Botton4.setOnClickListener(listener);


        search.setOnClickListener(listener);
    }
    public class GetEmergentThread extends Thread {
        HttpUtils httpUtils = new HttpUtils();
        public void run(){
            Map<String, String> params = new HashMap<String, String>();
            params.put("state","1");
            String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_FindEmergentgoods.action";
            String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
            System.out.println("加急的结果为：" + Result);
            Message message = new Message();
            message.what = 2;
            message.obj = Result;
            handler.sendMessage(message);
        }
    }
    public class GetNewThread extends Thread {
        HttpUtils httpUtils = new HttpUtils();
        public void run(){
            Map<String, String> params = new HashMap<String, String>();
            params.put("state","2");
            String strUrlpath = getResources().getString(R.string.burl) + "PublishGoodsAction_FindEmergentgoods.action";
            String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
            System.out.println("最新的结果为：" + Result);
            Message message = new Message();
            message.what = 3;
            message.obj = Result;
            handler.sendMessage(message);
        }
    }
    public class LunboThread extends Thread {
        public void run() {
            isRunning = true;
            while (isRunning) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //在子线程中开启子线程
                            //往下翻一页（setCurrentItem方法用来设置ViewPager的当前页）
                            vp.setCurrentItem(vp.getCurrentItem() + 1);
                        }
                    });
                }
            }
        }
    }
    public void getdata()
    {
        //线程
        new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lid","1");
                String strUrlpath = getResources().getString(R.string.burl) + "LunboAction_Find.action";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 0;
                message.obj = Result;
                handler.sendMessage(message);
            }
        }.start();
    }
    public void initdata()
    {
        n=0;
        //保存图片资源的集合
        imageViews = new ArrayList<>();
        for(int i=0;i<imageResUrl.length;i++){
            getImageFromNet(imageResUrl[i]);
        }
    }
    //下载图片
    private void getImageFromNet(final String imagePath) {
        // TODO Auto-generated method stub
        new Thread(){
            public void run() {
                try {
                    URL url=new URL(getResources().getString(R.string.burl) + imagePath);
                    HttpURLConnection con=(HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(10*1000);
                    InputStream is=con.getInputStream();
                    //把流转换为bitmap
                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    Message message=new Message();
                    message.what=1;
                    message.obj=bitmap;
                    //把这个bitmap发送到hanlder那里去处理
                    handler.sendMessage(message);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            };
        }.start();

    }
    public void initmorendata()
    {
        //初始化填充ViewPager的图片资源
        imageResIds = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.c};
        //图片的描述信息
        contentDescs = new String[]{
                "厦门特大香烟走私犯外逃14年今被遣返",
                "“机器换人”大潮之下 那些普通工人在想什么？",
                "外媒关注中国\"最美野长城毁容\":文物保护观念落后",
                "男子涉嫌骗取公司1亿资金理财 潜逃境外8天被抓获",
                "国家南海博物馆完成封顶 打造中国南海地标式建筑"
        };
        //保存图片资源的集合
        imageViews = new ArrayList<>();


        ImageView imageView;
        View pointView;
        //循环遍历图片资源，然后保存到集合中
        for (int i = 0; i < imageResIds.length; i++){
            //添加图片到集合中
            imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(imageResIds[i]);
            imageViews.add(imageView);

            //加小白点，指示器（这里的小圆点定义在了drawable下的选择器中了，也可以用小图片代替）
            pointView = new View(getActivity());
            pointView.setBackgroundResource(R.drawable.point_selector); //使用选择器设置背景
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, 8);
            if (i != 0){
                //如果不是第一个点，则设置点的左边距
                layoutParams.leftMargin = 10;
            }
            pointView.setEnabled(false); //默认都是暗色的
            ll_point.addView(pointView, layoutParams);
        }
    }
    public void initadapter()
    {
        ll_point.getChildAt(0).setEnabled(true); //初始化控件时，设置第一个小圆点为亮色
        tv_desc.setText(contentDescs[0]); //设置第一个图片对应的文字
        lastPosition = 0; //设置之前的位置为第一个
        vp.setAdapter(new MyPagerAdapter());
        //设置默认显示中间的某个位置（这样可以左右滑动），这个数只有在整数范围内，可以随便设置
        vp.setCurrentItem(5000000); //显示5000000这个位置的图片
    }
    /*
     自定义适配器，继承自PagerAdapter
    */
    class MyPagerAdapter extends PagerAdapter {

        //返回显示数据的总条数，为了实现无限循环，把返回的值设置为最大整数
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //指定复用的判断逻辑，固定写法：view == object
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //当创建新的条目，又反回来，判断view是否可以被复用(即是否存在)
            return view == object;
        }

        //返回要显示的条目内容
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //container  容器  相当于用来存放imageView
            //从集合中获得图片
            int newPosition = position % 5; //数组中总共有5张图片，超过数组长度时，取摸，防止下标越界
            ImageView imageView = imageViews.get(newPosition);
            //把图片添加到container中
            container.addView(imageView);
            //把图片返回给框架，用来缓存
            return imageView;
        }

        //销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //object:刚才创建的对象，即要销毁的对象
            container.removeView((View) object);
        }
    }
    private class OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.search:
                    if(searchcontext.getText().toString().trim().isEmpty())
                        Toast.makeText(getActivity(),"内容不能为空",Toast.LENGTH_SHORT).show();
                    else
                    {
                        Intent intent = new Intent(getActivity(), GoodslistActivity.class);
                        intent.putExtra("name",searchcontext.getText().toString().trim());
                        intent.putExtra("searchway","1");
                        startActivity(intent);
                    }
                    break;
                case R.id.layout1:
                    Intent intent1 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent1.putExtra("state","1");
                    intent1.putExtra("gid",newgid[0]);
                    startActivity(intent1);
                    break;
                case R.id.layout2:
                    Intent intent2 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent2.putExtra("state","1");
                    intent2.putExtra("gid",newgid[1]);
                    startActivity(intent2);
                    break;
                case R.id.layout3:
                    Intent intent3 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent3.putExtra("state","1");
                    intent3.putExtra("gid",newgid[2]);
                    startActivity(intent3);
                    break;
                case R.id.layout4:
                    Intent intent4 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent4.putExtra("state","1");
                    intent4.putExtra("gid",newgid[3]);
                    startActivity(intent4);
                    break;
                case R.id.layout5:
                    Intent intent5 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent5.putExtra("state","1");
                    intent5.putExtra("gid",newgid[4]);
                    startActivity(intent5);
                    break;
                case R.id.layout6:
                    Intent intent6 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent6.putExtra("state","1");
                    intent6.putExtra("gid",newgid[5]);
                    startActivity(intent6);
                    break;
                case R.id.newmore:
                    Intent intentmore = new Intent(getActivity(), GoodslistActivity.class);
                    intentmore.putExtra("name","");
                    intentmore.putExtra("searchway","3");
                    startActivity(intentmore);
                    break;
                case R.id.jilayout1:
                    Intent intent7 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent7.putExtra("state","1");
                    intent7.putExtra("gid",jiid1);
                    startActivity(intent7);
                    break;
                case R.id.jilayout2:
                    Intent intent8 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent8.putExtra("state","1");
                    intent8.putExtra("gid",jiid2);
                    startActivity(intent8);
                    break;
                case R.id.jilayout3:
                    Intent intent9 = new Intent(getActivity(), GoodstradeActivity.class);
                    intent9.putExtra("state","1");
                    intent9.putExtra("gid",jiid3);
                    startActivity(intent9);
                    break;
                case R.id.jimore:
                    Intent jiintent = new Intent(getActivity(), GoodslistActivity.class);
                    jiintent.putExtra("name","");
                    jiintent.putExtra("searchway","4");
                    startActivity(jiintent);
                    break;
                case R.id.botton1:
                    Intent b1intent = new Intent(getActivity(), GoodslistActivity.class);
                    b1intent.putExtra("name","");
                    b1intent.putExtra("searchway","6");
                    startActivity(b1intent);
                    break;
                case R.id.botton2:
                    Intent b2intent = new Intent(getActivity(), GoodslistActivity.class);
                    b2intent.putExtra("name","");
                    b2intent.putExtra("searchway","3");
                    startActivity(b2intent);
                    break;
                case R.id.botton3:
                    Intent b3intent = new Intent(getActivity(), GoodslistActivity.class);
                    b3intent.putExtra("name","");
                    b3intent.putExtra("searchway","5");
                    startActivity(b3intent);
                    break;
                case R.id.botton4:
                    Intent b4intent = new Intent(getActivity(), CharitylistActivity.class);
                    startActivity(b4intent);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        iscrossCreat = false;
        isRunning = false;

    }

    //--------------以下是设置ViewPager的滚动监听所需实现的方法--------
    //页面滑动
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //新的页面被选中
    @Override
    public void onPageSelected(int position) {
        //当前的位置可能很大，为了防止下标越界，对要显示的图片的总数进行取余
        int newPosition = position % 5;
        //设置描述信息
        tv_desc.setText(contentDescs[newPosition]);
        //设置小圆点为高亮或暗色
        ll_point.getChildAt(lastPosition).setEnabled(false);
        ll_point.getChildAt(newPosition).setEnabled(true);
        lastPosition = newPosition; //记录之前的点
    }

    //页面滑动状态发生改变
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
