package com.wang.floatingactionmenu;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created on 2016/11/13.
 * Author: wang
 */

public class SystemOverlayFloatingActionButton extends FloatingActionButton {

    public static final int POSITION_TOP_CENTER = 1;
    public static final int POSITION_TOP_RIGHT = 2;
    public static final int POSITION_RIGHT_CENTER = 3;
    public static final int POSITION_BOTTOM_RIGHT = 4;
    public static final int POSITION_BOTTOM_CENTER = 5;
    public static final int POSITION_BOTTOM_LEFT = 6;
    public static final int POSITION_LEFT_CENTER = 7;
    public static final int POSITION_TOP_LEFT = 8;


    /**
     * Constructor
     *
     * @param context a reference to the current context
     */
    public SystemOverlayFloatingActionButton(Context context) {
        this(context, getDefaultSystemWindowParams(), POSITION_BOTTOM_RIGHT);
    }


    /**
     * Constructor
     *
     * @param context      a reference to the current context
     * @param layoutParams
     * @param position
     */
    public SystemOverlayFloatingActionButton(Context context, ViewGroup.LayoutParams layoutParams, int position) {
        super(context);

        if (!(context instanceof ContextWrapper)) {
            throw new RuntimeException("Given context must have app design theme, "
                    + "since this FAB is not a systemOverlay.");
        }

        setPosition(position, layoutParams);

        // If no custom backgroundDrawable is specified, use the background drawable of the theme.
        setClickable(true);

        attach(layoutParams);
    }

    /**
     * Sets the position of the button by calculating its Gravity from the position parameter
     *
     * @param position     one of 8 specified positions.
     * @param layoutParams should be either FrameLayout.LayoutParams or WindowManager.LayoutParams
     */
    public void setPosition(int position, ViewGroup.LayoutParams layoutParams) {

        int gravity;
        switch (position) {
            case POSITION_TOP_CENTER:
                gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case POSITION_TOP_RIGHT:
                gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case POSITION_RIGHT_CENTER:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
            case POSITION_BOTTOM_CENTER:
                gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case POSITION_BOTTOM_LEFT:
                gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case POSITION_LEFT_CENTER:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case POSITION_TOP_LEFT:
                gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case POSITION_BOTTOM_RIGHT:
            default:
                gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
        }
        try {
            WindowManager.LayoutParams lp = (WindowManager.LayoutParams) layoutParams;
            lp.gravity = gravity;
            int margin =  getContext().getResources().getDimensionPixelSize(R.dimen.action_button_margin);
            lp.x = margin;
            lp.y = margin;
            setLayoutParams(lp);
        } catch (ClassCastException e) {
            throw new ClassCastException("layoutParams must be an instance of " +
                    "WindowManager.LayoutParams, since this FAB is a systemOverlay");
        }
    }

    /**
     * Attaches it to the content view with specified LayoutParams.
     *
     * @param layoutParams
     */
    public void attach(ViewGroup.LayoutParams layoutParams) {
        try {
            getWindowManager().addView(this, layoutParams);
        } catch (SecurityException e) {
            throw new SecurityException("Your application must have SYSTEM_ALERT_WINDOW " +
                    "permission to create a system window.");
        }
    }

    /**
     * Detaches it from the container view.
     */
    public void detach() {
        getWindowManager().removeView(this);
    }


    public WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public static WindowManager.LayoutParams getDefaultSystemWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, // z-ordering
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    public static class Builder {

        private Context context;
        private ViewGroup.LayoutParams layoutParams;
        private int position;

        public Builder(Context context) {
            this.context = context;
            // Default FloatingActionButton settings
            setLayoutParams(getDefaultSystemWindowParams());
            setPosition(SystemOverlayFloatingActionButton.POSITION_BOTTOM_RIGHT);
        }

        public Builder setLayoutParams(ViewGroup.LayoutParams params) {
            this.layoutParams = params;
            return this;
        }

        public Builder setPosition(int position) {
            this.position = position;
            return this;
        }


        public SystemOverlayFloatingActionButton build() {
            return new SystemOverlayFloatingActionButton(context,
                    layoutParams,
                    position);
        }
    }
}
