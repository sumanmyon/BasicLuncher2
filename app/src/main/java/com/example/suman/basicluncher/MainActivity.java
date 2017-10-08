package com.example.suman.basicluncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {    //TODO remember here to make activity instead of AppcompactActivity

//    class Pac{
//
//    }

    static Activity activity;

    Pac[] pacs;
    PackageManager pm;
    static boolean appLunchable = true;

    DrawerAdapter drawerAdapterObject;
    GridView drawerGrid;
    SlidingDrawer slidingDrawer;
    RelativeLayout homeView;

    AppWidgetManager mAppWidgetManager;
    LauncherAppWidgetHost mAppWidgetHost;
    int REQUEST_CREATE_APPWIDGET = 900;
    int REQUEST_CREATE_SHORTCUT = 700;
    int numWidgets;

    SharedPreferences globalPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        drawerGrid = (GridView) findViewById(R.id.content);
        slidingDrawer = (SlidingDrawer)findViewById(R.id.drawer);
        homeView = (RelativeLayout)findViewById(R.id.home_view);

        pm =getPackageManager();
        //set_Pacs(true);
        new LoadApps().execute();
        addAppsToHome();

        //TODO draging app in home set appLunch to true
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                appLunchable = true;
            }
        });

        //TODO for widget
        homeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);

                String[] items={getResources().getString(R.string.widget),
                                getResources().getString(R.string.shortcut),
                                getResources().getString(R.string.theme)};
                b.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                selectWidget();
                                break;
                            case 1:
                                selectShortcut();
                                break;
                            case 2:
                                selectTheme();
                                break;
                        }
                    }
                });

                AlertDialog d = b.create();
                d.show();

                return false;
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        registerReceiver(new PacReceiver(), intentFilter);
    }

    void selectTheme(){
        Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        Intent filter = new Intent(Intent.ACTION_MAIN);
        filter.addCategory("com.anddoes.launcher.THEME");
        intent.putExtra(Intent.EXTRA_INTENT, filter);
        startActivityForResult(intent, R.id.REQUEST_PICK_THEME);
    }
    void selectShortcut(){
        Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        intent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        startActivityForResult(intent, R.id.REQUEST_PICK_SHORTCUT);
    }

    //TODO below 5 methods is for widgets
    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }
    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            }
            else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
            }
            else if(requestCode == R.id.REQUEST_PICK_SHORTCUT){
                configureShortcut(data);
            }
            else if(requestCode == REQUEST_CREATE_SHORTCUT){
                createShortcut(data);
            }
            else if(requestCode == R.id.REQUEST_PICK_THEME){
                globalPrefs.edit().putString("theme", data.getComponent().getPackageName()).commit();
                set_Pacs(false);
            }
        }
        else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void configureShortcut(Intent data){
        startActivityForResult(data, REQUEST_CREATE_SHORTCUT);
    }

    public void createShortcut(Intent intent){
        Intent.ShortcutIconResource iconResource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        Bitmap icon                              = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
        String shortcutLabel                     = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Intent shortIntent                       = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

        if (icon==null){
            if (iconResource!=null){
                Resources resources =null;
                try {
                    resources = pm.getResourcesForApplication(iconResource.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    if(resources.getDrawable(id) instanceof StateListDrawable) {
                        Drawable d = ((StateListDrawable)resources.getDrawable(id)).getCurrent();
                        icon = ((BitmapDrawable)d).getBitmap();
                    }else
                        icon = ((BitmapDrawable)resources.getDrawable(id)).getBitmap();
                }
            }
        }


        if (shortcutLabel!=null && shortIntent!=null && icon!=null){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 100;
            lp.topMargin = (int) 100;

            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);

            ((ImageView)ll.findViewById(R.id.icon_image)).setImageBitmap(icon);
            ((TextView)ll.findViewById(R.id.icon_text)).setText(shortcutLabel);

            ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setOnTouchListener(new AppTouchListener());
                    return false;
                }
            });
            ll.setOnClickListener(new ShortcutClickListener(this));
            ll.setTag(shortIntent);
            homeView.addView(ll, lp);
        }

    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }


    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(homeView.getWidth()/3, homeView.getHeight()/3);
        lp.leftMargin = numWidgets * (homeView.getWidth()/3);

        //TODO long click on widgets
        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setBackgroundColor(Color.GRAY);

                return false;
            }
        });
        homeView.addView(hostView, lp);
        slidingDrawer.bringToFront();
        numWidgets++;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    @Override
    public void onBackPressed() {

    }

    public class LoadApps extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
            pacs = new Pac[pacsList.size()];

            for(int i=0; i<pacsList.size(); i++){
                pacs[i] = new Pac();
                pacs[i].icon = pacsList.get(i).loadIcon(pm);
                pacs[i].packageName = pacsList.get(i).activityInfo.packageName;
                pacs[i].name = pacsList.get(i).activityInfo.name;
                pacs[i].label = pacsList.get(i).loadLabel(pm).toString();
            }

            new SortApps().exchangeSort(pacs);
            themePacs();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(drawerAdapterObject == null) {
                drawerAdapterObject = new DrawerAdapter(MainActivity.this, pacs);
                drawerGrid.setAdapter(drawerAdapterObject);
                drawerGrid.setOnItemClickListener(new DrawerClickListener(activity, pacs, pm));
                drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(activity, slidingDrawer, homeView, pacs));
            }else {
                drawerAdapterObject.pacsForAdapter = pacs;
                drawerAdapterObject.notifyDataSetInvalidated();
            }
        }
    }

    public void addAppsToHome(){
        AppSerializableData data = SerializationTools.loadSerializedData();
        if(data != null){
            for (Pac pacToAddToHome : data.apps){
                pacToAddToHome.addToHome(this, homeView);
            }
        }
    }

    public void set_Pacs(boolean init){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
        pacs = new Pac[pacsList.size()];

        for(int i=0; i<pacsList.size(); i++){
            pacs[i] = new Pac();
            pacs[i].icon = pacsList.get(i).loadIcon(pm);
            pacs[i].packageName = pacsList.get(i).activityInfo.packageName;
            pacs[i].name = pacsList.get(i).activityInfo.name;
            pacs[i].label = pacsList.get(i).loadLabel(pm).toString();
        }

        new SortApps().exchangeSort(pacs);
        themePacs();

        if(init) {
            drawerAdapterObject = new DrawerAdapter(MainActivity.this, pacs);
            drawerGrid.setAdapter(drawerAdapterObject);
            drawerGrid.setOnItemClickListener(new DrawerClickListener(this, pacs, pm));
            drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(this, slidingDrawer, homeView, pacs));
        }else {
            drawerAdapterObject.pacsForAdapter = pacs;
            drawerAdapterObject.notifyDataSetInvalidated();
        }
    }

    @SuppressWarnings("deprecation")
    public void themePacs() {
        //theming vars-----------------------------------------------
        final int ICONSIZE = Tools.numtodp(65, MainActivity.this);
        Resources themeRes = null;
        String resPacName =globalPrefs.getString("theme", "");
        String iconResource = null;
        int intres=0;
        int intresiconback = 0;
        int intresiconfront = 0;
        int intresiconmask = 0;
        float scaleFactor = 1.0f;

        Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        p.setAntiAlias(true);

        Paint origP = new Paint(Paint.FILTER_BITMAP_FLAG);
        origP.setAntiAlias(true);

        Paint maskp= new Paint(Paint.FILTER_BITMAP_FLAG);
        maskp.setAntiAlias(true);
        maskp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (resPacName.compareTo("")!=0){
            try{themeRes =pm.getResourcesForApplication(resPacName);}catch(Exception e){};
            if (themeRes!=null){
                String[] backAndMaskAndFront =ThemeTools.getIconBackAndMaskResourceName(themeRes,resPacName);
                if (backAndMaskAndFront[0]!=null)
                    intresiconback=themeRes.getIdentifier(backAndMaskAndFront[0],"drawable",resPacName);
                if (backAndMaskAndFront[1]!=null)
                    intresiconmask=themeRes.getIdentifier(backAndMaskAndFront[1],"drawable",resPacName);
                if (backAndMaskAndFront[2]!=null)
                    intresiconfront=   themeRes.getIdentifier(backAndMaskAndFront[2],"drawable",resPacName);
            }
        }

        BitmapFactory.Options uniformOptions = new BitmapFactory.Options();
        uniformOptions.inScaled=false;
        uniformOptions.inDither=false;
        uniformOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Canvas origCanv;
        Canvas canvas;
        scaleFactor=ThemeTools.getScaleFactor(themeRes,resPacName);
        Bitmap back=null;
        Bitmap mask=null;
        Bitmap front=null;
        Bitmap scaledBitmap = null;
        Bitmap scaledOrig = null;
        Bitmap orig = null;

        if (resPacName.compareTo("")!=0 && themeRes!=null){
            try{
                if (intresiconback!=0)
                    back =BitmapFactory.decodeResource(themeRes,intresiconback,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconmask!=0)
                    mask = BitmapFactory.decodeResource(themeRes,intresiconmask,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconfront!=0)
                    front = BitmapFactory.decodeResource(themeRes,intresiconfront,uniformOptions);
            }catch(Exception e){}
        }
        //theming vars-----------------------------------------------
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        for(int I=0;I<pacs.length;I++) {
            if (themeRes!=null){
                iconResource=null;
                intres=0;
                iconResource=ThemeTools.getResourceName(themeRes, resPacName, "ComponentInfo{"+pacs[I].packageName+"/"+pacs[I].name+"}");
                if (iconResource!=null){
                    intres = themeRes.getIdentifier(iconResource,"drawable",resPacName);
                }

                if (intres!=0){//has single drawable for app
                    pacs[I].icon = new BitmapDrawable(BitmapFactory.decodeResource(themeRes,intres,uniformOptions));
                }else{
                    orig=Bitmap.createBitmap(pacs[I].icon.getIntrinsicWidth(), pacs[I].icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    pacs[I].icon.setBounds(0, 0, pacs[I].icon.getIntrinsicWidth(), pacs[I].icon.getIntrinsicHeight());
                    pacs[I].icon.draw(new Canvas(orig));

                    scaledOrig =Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    scaledBitmap = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(scaledBitmap);
                    if (back!=null){
                        canvas.drawBitmap(back, Tools.getResizedMatrix(back, ICONSIZE, ICONSIZE), p);
                    }

                    origCanv=new Canvas(scaledOrig);
                    orig=Tools.getResizedBitmap(orig, ((int)(ICONSIZE*scaleFactor)), ((int)(ICONSIZE*scaleFactor)));
                    origCanv.drawBitmap(orig, scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2 ,scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2, origP);

                    if (mask!=null){
                        origCanv.drawBitmap(mask,Tools.getResizedMatrix(mask, ICONSIZE, ICONSIZE), maskp);
                    }

                    if (back!=null){
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);
                    }else
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);

                    if (front!=null)
                        canvas.drawBitmap(front,Tools.getResizedMatrix(front, ICONSIZE, ICONSIZE), p);

                    pacs[I].icon = new BitmapDrawable(scaledBitmap);
                }
            }
        }


        front=null;
        back=null;
        mask=null;
        scaledOrig=null;
        orig=null;
        scaledBitmap=null;
        canvas=null;
        origCanv=null;
        p=null;
        maskp=null;
        resPacName=null;
        iconResource=null;
        intres=0;
    }

    public class PacReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //set_Pacs(false);
            new LoadApps().execute();
        }
    }

}
