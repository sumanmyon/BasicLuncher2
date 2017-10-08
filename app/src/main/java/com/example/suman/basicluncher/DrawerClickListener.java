package com.example.suman.basicluncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by suman on 10/5/2017.
 */

public class DrawerClickListener implements OnItemClickListener {

    Context mContext;
    Pac[] pacsForAdapter;
    PackageManager pmForListener;

    public DrawerClickListener(Context c, Pac[] pacs, PackageManager pm) {
        mContext = c;
        pacsForAdapter = pacs;
        pmForListener = pm;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        if(MainActivity.appLunchable) {

            //Intent lunchIntent = pmForListener.getLaunchIntentForPackage(pacsForAdapter[pos].name);
            Intent lunchIntent = new Intent(Intent.ACTION_MAIN);
            lunchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cp = new ComponentName(pacsForAdapter[pos].packageName, pacsForAdapter[pos].name);
            lunchIntent.setComponent(cp);
            mContext.startActivity(lunchIntent);
        }
    }
}
