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
 * Created by 30579 on 2018/4/28.
 */

public class CharitylistAdapter extends BaseAdapter {
    private AsyncBitmapLoader asyncBitmapLoader;
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    public CharitylistAdapter(Context context, List<Map<String, Object>> list){
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
        final CharitylistAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView  = LayoutInflater.from(context).inflate(R.layout.charitylist_item_layout, parent,false);
            holder = new CharitylistAdapter.ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.scannum = (TextView) convertView.findViewById(R.id.scannum);
            holder.joinnum = (TextView) convertView.findViewById(R.id.joinnum);
            convertView.setTag(holder);
        }else {
            holder = (CharitylistAdapter.ViewHolder) convertView.getTag();
        }

        Bitmap bitmap=asyncBitmapLoader.loadBitmap(holder.image, list.get(position).get("imageurl").toString(), new AsyncBitmapLoader.ImageCallBack() {

            @Override
            public void imageLoad(ImageView imageView, Bitmap bitmap) {
                // TODO Auto-generated method stub
                imageView.setImageBitmap(bitmap);
            }
        });
        if(bitmap == null)
        {
            holder.image.setImageResource(R.drawable.a);
        }
        else
        {
            holder.image.setImageBitmap(bitmap);
        }
        holder.image.setTag(list.get(position).get("imageurl").toString());
        holder.name.setText(list.get(position).get("name").toString());
        holder.content.setText(list.get(position).get("content").toString());
        holder.scannum.setText(list.get(position).get("scannum").toString());
        holder.joinnum.setText(list.get(position).get("joinnum").toString());
        return convertView;
    }
    class ViewHolder{
        TextView name,content,scannum,joinnum;
        ImageView image;
    }
}
