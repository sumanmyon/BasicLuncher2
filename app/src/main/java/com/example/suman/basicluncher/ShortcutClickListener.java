package com.example.suman.basicluncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by suman on 10/6/2017.
 */

public class ShortcutClickListener implements View.OnClickListener {


    Context mContext;

    public ShortcutClickListener(Context ctx) {
        mContext = ctx;

    }

    @Override
    public void onClick(View v) {
        Intent data;
        data = (Intent) v.getTag();
        mContext.startActivity(data);
    }
}