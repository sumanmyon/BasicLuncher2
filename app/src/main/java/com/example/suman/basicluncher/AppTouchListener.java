package com.example.suman.basicluncher;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import static com.example.suman.basicluncher.DrawerLongClickListener.pacsForListener;

/**
 * Created by suman on 10/6/2017.
 */

public class AppTouchListener implements OnTouchListener {

    int leftMargin;
    int topMargin;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//
//                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
                leftMargin = (int) event.getRawX() - v.getWidth()/2;
                topMargin = (int) event.getRawY() - v.getHeight()/2;

                //TODO icon donot go off the screen
                if(leftMargin+v.getWidth() > v.getRootView().getWidth()){
                    leftMargin = v.getRootView().getWidth() - v.getWidth();
                }
                if(leftMargin < 0){
                    leftMargin = 0;
                }

                if(topMargin+v.getHeight() > ((View)v.getParent()).getHeight()){
                    topMargin = ((View)v.getParent()).getHeight() - v.getHeight();
                }
                if(topMargin < 0){
                    topMargin = 0;
                }

                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                v.setLayoutParams(lp);
                break;

//            case MotionEvent.ACTION_DOWN:
//                MainActivity.appLunchable = true;
//                break;

            case MotionEvent.ACTION_UP:
                v.setOnTouchListener(null);
                //v.setOnLongClickListener(null);
                break;
        }
        return true;
    }
}
