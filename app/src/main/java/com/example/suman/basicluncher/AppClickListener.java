package com.example.suman.basicluncher;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by suman on 10/6/2017.
 */

public class AppClickListener implements View.OnClickListener {

    Context mContext;

    public AppClickListener(Context ctx) {
        mContext = ctx;

    }

    @Override
    public void onClick(View v) {
//        String[] data;
//        data = (String[]) v.getTag();
        Pac data;
        data = (Pac) v.getTag();
        Intent lunchIntent = new Intent(Intent.ACTION_MAIN);
        lunchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        ComponentName cp = new ComponentName(data[0], data[1]);
        ComponentName cp = new ComponentName(data.packageName, data.name);
        lunchIntent.setComponent(cp);
        mContext.startActivity(lunchIntent);
    }
}
