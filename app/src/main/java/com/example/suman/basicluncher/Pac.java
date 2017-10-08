package com.example.suman.basicluncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

/**
 * Created by suman on 10/7/2017.
 */

public class Pac implements Serializable{
    transient Drawable icon;  //TODo to serizable Drawable must be transient
    String name;
    String packageName;
    String label;
    int x,y;
    String iconLocation;
    boolean lanscape;

    public void cacheIcon(){
        if(iconLocation == null){
            new File(MainActivity.activity.getApplicationInfo().dataDir+"/cachedApps/").mkdirs();
        }

        if(icon != null){
            iconLocation = MainActivity.activity.getApplicationInfo().dataDir+"/cachedApps/"+packageName+name;
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(iconLocation);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(fos != null){
                Tools.drawableToBitmap(icon).compress(Bitmap.CompressFormat.PNG,100,fos);
                try{
                    fos.flush();
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                iconLocation = null;
            }
        }
    }

    public Bitmap getCachedIcon(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;

        if(iconLocation != null){
            File cachedIcon = new File(iconLocation);
            if(cachedIcon.exists()){
                return BitmapFactory.decodeFile(cachedIcon.getAbsolutePath(), options);
            }
        }
        return null;
    }

    public void addToHome(Context mContext, RelativeLayout homeViewForAdapter){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;

        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);

        if(icon == null){
            icon = new BitmapDrawable(mContext.getResources(), getCachedIcon());
        }
        ((ImageView)ll.findViewById(R.id.icon_image)).setImageDrawable(icon);
        ((TextView)ll.findViewById(R.id.icon_text)).setText(label);

        ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new AppTouchListener());
                //v.setOnLongClickListener(null);
                return false;
            }
        });
        ll.setTag(this);
        ll.setOnClickListener(new AppClickListener(mContext));
        homeViewForAdapter.addView(ll, 0, lp);
    }

    public void deleteIcon(){
        if(iconLocation != null) {
            new File(iconLocation).delete();
        }
    }
}
