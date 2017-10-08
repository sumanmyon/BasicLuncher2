package com.example.suman.basicluncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by suman on 10/5/2017.
 */

public class DrawerAdapter extends BaseAdapter {

    Context mContext;
    Pac[] pacsForAdapter;

    public DrawerAdapter(Context c, Pac pacs[]) {
        mContext = c;
        pacsForAdapter = pacs;
    }

    @Override
    public int getCount() {
        return pacsForAdapter.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = li.inflate(R.layout.drawer_item, null);

            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.icon_text);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_image);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
//        ImageView imageView = new ImageView(mContext);
//        imageView.setImageDrawable(pacsForAdapter[pos].icon);
//        imageView.setLayoutParams(new GridView.LayoutParams(90, 90));
//        imageView.setPadding(2,2,2,2);
        viewHolder.icon.setImageDrawable(pacsForAdapter[pos].icon);
        viewHolder.text.setText(pacsForAdapter[pos].label);
        return convertView;
    }

    static class ViewHolder{
        ImageView icon;
        TextView text;
    }
}
