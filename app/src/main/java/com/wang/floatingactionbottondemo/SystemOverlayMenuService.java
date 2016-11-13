package com.wang.floatingactionbottondemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.Toast;

import com.wang.floatingactionmenu.FloatingActionCircleMenu;
import com.wang.floatingactionmenu.FloatingActionLineMenu2;
import com.wang.floatingactionmenu.FloatingActionMenu;
import com.wang.floatingactionmenu.RotatingDrawable;
import com.wang.floatingactionmenu.SystemOverlayFloatingActionButton;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionLabelClickListener;
import com.wang.floatingactionmenu.util.FabSizeUtil;

/**
 * Created on 2016/11/13.
 * Author: wang
 */

public class SystemOverlayMenuService extends Service implements OnFloatingActionClickListener, OnFloatingActionLabelClickListener {

    private final IBinder mBinder =  new LocalBinder();

    private SystemOverlayFloatingActionButton mAddFab;
    private SystemOverlayFloatingActionButton mAddFab2;

    private FloatingActionCircleMenu mAddMenu;
    private FloatingActionLineMenu2 mAddMenu2;


    public SystemOverlayMenuService() {
    }

    public class LocalBinder extends Binder {
        SystemOverlayMenuService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SystemOverlayMenuService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = new ContextThemeWrapper(this, R.style.AppTheme);

        mAddFab = new SystemOverlayFloatingActionButton.Builder(context)
                .build();
        mAddFab.setImageDrawable(new RotatingDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_new)));

        int sizeMini = FabSizeUtil.getSizeDimension(this, FloatingActionButton.SIZE_MINI);
        int sizeNor = FabSizeUtil.getSizeDimension(this, FloatingActionButton.SIZE_NORMAL);


        mAddMenu = new FloatingActionCircleMenu.Builder(context, true)
                .addSubFABView(context, R.drawable.ic_action_important, FloatingActionButton.SIZE_MINI, sizeMini, sizeMini)
                .addSubFABView(context, R.drawable.ic_action_important, FloatingActionButton.SIZE_NORMAL, 56, 56)
                .addSubFABView(context, R.drawable.ic_action_important, FloatingActionButton.SIZE_NORMAL, sizeNor, sizeNor)
                .setStartAngle(180)
                .setEndAngle(270)
                .setFloatingActionClickListener(this)
                .attachTo(mAddFab)
                .build();


        WindowManager.LayoutParams params2 = SystemOverlayFloatingActionButton.getDefaultSystemWindowParams();
        params2.width = 200;
        params2.height = 200;

        mAddFab2 = new SystemOverlayFloatingActionButton.Builder(context)
                .setPosition(SystemOverlayFloatingActionButton.POSITION_TOP_CENTER)
                .setLayoutParams(params2)
                .build();
        mAddFab2.setImageResource(R.drawable.ic_action_camera);



        mAddMenu2 = new FloatingActionLineMenu2.Builder(context, true)
                .setExpandDirection(FloatingActionLineMenu2.EXPAND_DOWN)
                .setLabelPosition(FloatingActionLineMenu2.LABELS_ON_LEFT_SIDE)
                .addSubFABView(context, R.drawable.ic_action_important, FloatingActionButton.SIZE_NORMAL,sizeNor, sizeNor)
                .addSubFABView(context, R.drawable.ic_action_camera, FloatingActionButton.SIZE_NORMAL, sizeNor, sizeNor)
                .addSubFABView(context, R.drawable.ic_action_location_found, FloatingActionButton.SIZE_NORMAL, sizeNor, sizeNor)
                .setFloatingActionClickListener(this)
                .setLabelClickListener(this)
                .attachTo(mAddFab2)
                .build();
    }


    @Override
    public void onSubClick(int position) {
        Toast.makeText(this, "sub " + position + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMainClick(boolean open) {
        Log.d("CircleMenuActivity", "main " + (open ? "open" : "close") + " click");
//        Toast.makeText(this, "main " + (open ? "open" : "close") + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLabelClick(int position) {
        Toast.makeText(this, "label " + position + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if(mAddMenu != null && mAddMenu.isOpen()) mAddMenu.close(false);
        if(mAddMenu2 != null && mAddMenu2.isOpen()) mAddMenu2.close(false);
        if(mAddFab != null) mAddFab.detach();
        if(mAddFab2 != null) mAddFab2.detach();

        super.onDestroy();
    }
}
