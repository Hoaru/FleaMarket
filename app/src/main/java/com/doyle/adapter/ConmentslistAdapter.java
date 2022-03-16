package com.doyle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doyle.activity.fleamarket.R;
import com.doyle.util.AsyncBitmapLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by 30579 on 2018/2/23.
 */

public class ConmentslistAdapter extends BaseAdapter {
    private AsyncBitmapLoader asyncBitmapLoader;
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    public ConmentslistAdapter(Context context, List<Map<String, Object>> list){
        asyncBitmapLoader=new AsyncBitmapLoader();
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ConmentslistAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView  = LayoutInflater.from(context).inflate(R.layout.layout_conmentlist,parent,false);
            holder = new ConmentslistAdapter.ViewHolder();

            holder.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
            holder.shijian = (TextView) convertView.findViewById(R.id.shijian);
            holder.pinglun = (TextView) convertView.findViewById(R.id.pinglun);
            holder.nicheng = (TextView) convertView.findViewById(R.id.nicheng);

            convertView.setTag(holder);
        }else {
            holder = (ConmentslistAdapter.ViewHolder) convertView.getTag();
        }

        Bitmap bitmap=asyncBitmapLoader.loadBitmap(holder.touxiang, list.get(position).get("touxiang").toString(), new AsyncBitmapLoader.ImageCallBack() {

            @Override
            public void imageLoad(ImageView imageView, Bitmap bitmap) {
                // TODO Auto-generated method stub
                imageView.setImageBitmap(bitmap);
            }
        });
        if(bitmap == null)
        {
            holder.touxiang.setImageResource(R.drawable.touxiang);
        }
        else
        {
            holder.touxiang.setImageBitmap(bitmap);
        }
        holder.touxiang.setTag(list.get(position).get("touxiang").toString());
        holder.nicheng.setText(list.get(position).get("nicheng").toString());
        holder.shijian.setText(list.get(position).get("shijian").toString());
        holder.pinglun.setText(list.get(position).get("pinglun").toString());
        return convertView;
    }
    class ViewHolder{
        TextView nicheng,shijian,pinglun;
        ImageView touxiang;
    }

}
