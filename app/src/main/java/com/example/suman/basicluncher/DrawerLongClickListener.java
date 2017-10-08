package com.example.suman.basicluncher;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by suman on 10/6/2017.
 */

public class DrawerLongClickListener implements OnItemLongClickListener {

    Context mContext;
    SlidingDrawer drawerForAdapter;
    RelativeLayout homeViewForAdapter;
    static Pac[] pacsForListener;

    public DrawerLongClickListener(Context ctxt, SlidingDrawer slidingDrawer, RelativeLayout homeView, Pac[] pac) {
        mContext = ctxt;
        drawerForAdapter = slidingDrawer;
        homeViewForAdapter = homeView;
        pacsForListener = pac;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View item, int pos, long l) {

        MainActivity.appLunchable = false;

//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(item.getWidth(), item.getHeight());
//        lp.leftMargin = (int) item.getX();
//        lp.topMargin = (int) item.getY();
//
//        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);
//
//        ((ImageView)ll.findViewById(R.id.icon_image)).setImageDrawable(((ImageView)item.findViewById(R.id.icon_image)).getDrawable());
//        ((TextView)ll.findViewById(R.id.icon_text)).setText(((TextView)item.findViewById(R.id.icon_text)).getText());
//
//        ll.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                v.setOnTouchListener(new AppTouchListener());
//                //v.setOnLongClickListener(null);
//                return false;
//            }
//        });
//        ll.setOnClickListener(new AppClickListener(mContext));

        //TODO to know whic application use these
//        String[] data = new String[2];
//        data[0] = pacsForListener[pos].packageName;
//        data[1] = pacsForListener[pos].name;
//        ll.setTag(data);

        //TODO storing tags
        AppSerializableData objectData = SerializationTools.loadSerializedData();
        if(objectData == null){
            objectData = new AppSerializableData();
        }
        if(objectData.apps == null){
            objectData.apps = new ArrayList<Pac>();
        }
        Pac pacToAdd = pacsForListener[pos];
        pacToAdd.cacheIcon();
//        pacToAdd.x = lp.leftMargin;
//        pacToAdd.y = lp.topMargin;
        pacToAdd.x = (int) item.getX();
        pacToAdd.y = (int) item.getY();

        if(MainActivity.activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            pacToAdd.lanscape = true;
        }else {
            pacToAdd.lanscape = false;
        }
        objectData.apps.add(pacToAdd);
//        ll.setTag(pacToAdd);
        SerializationTools.serializeData(objectData);

        //TODO
//        homeViewForAdapter.addView(ll, lp);
        pacToAdd.addToHome(mContext, homeViewForAdapter);
        drawerForAdapter.animateClose();
        drawerForAdapter.bringToFront();

        return false;
    }
}
