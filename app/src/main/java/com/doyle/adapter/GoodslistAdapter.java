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
 * Created by 30579 on 2018/2/16.
 */

public class GoodslistAdapter extends BaseAdapter {
    private AsyncBitmapLoader asyncBitmapLoader;
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    public GoodslistAdapter(Context context, List<Map<String, Object>> list){
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView  = LayoutInflater.from(context).inflate(R.layout.layout_searchlist, parent,false);
            holder = new ViewHolder();

            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.gname = (TextView) convertView.findViewById(R.id.name);
            holder.detail = (TextView) convertView.findViewById(R.id.detail);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.hownew = (TextView) convertView.findViewById(R.id.hownew);
            holder.getway = (TextView) convertView.findViewById(R.id.getway);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.scannum = (TextView) convertView.findViewById(R.id.scannum);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bitmap bitmap=asyncBitmapLoader.loadBitmap(holder.image, list.get(position).get("image").toString(), new AsyncBitmapLoader.ImageCallBack() {

            @Override
            public void imageLoad(ImageView imageView, Bitmap bitmap) {
                // TODO Auto-generated method stub
                imageView.setImageBitmap(bitmap);
            }
        });
        if(bitmap == null)
        {
            holder.image.setImageResource(R.drawable.touxiang);
        }
        else
        {
            holder.image.setImageBitmap(bitmap);
        }
        holder.image.setTag(list.get(position).get("image").toString());
        holder.gname.setText(list.get(position).get("gname").toString());
        holder.detail.setText(list.get(position).get("detail").toString());
        holder.type.setText(list.get(position).get("type").toString());
        holder.hownew.setText(list.get(position).get("hownew").toString());
        holder.getway.setText(list.get(position).get("getway").toString());
        holder.price.setText(list.get(position).get("price").toString());
        holder.scannum.setText(list.get(position).get("scannum").toString());
        return convertView;
    }
    class ViewHolder{
        TextView gname,detail,type,hownew,getway,price,scannum;
        ImageView image;
    }

}