package com.wang.floatingactionmenu;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.wang.floatingactionmenu.animation.DefaultAnimationHandler;
import com.wang.floatingactionmenu.animation.MenuAnimationHandler;
import com.wang.floatingactionmenu.interfaces.MenuStateChangeListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/10/28.
 * Author: wang
 */

public abstract class FloatingActionMenu implements View.OnClickListener {
    /**
     * Reference to the view (usually a button) to trigger the menu to show
     */
    protected View mainActionView;

    /**
     * List of menu items
     */
    protected List<Item> subActionItems;
    /**
     * Reference to the preferred {@link MenuAnimationHandler} object
     */
    protected MenuAnimationHandler animationHandler;
    /**
     * Reference to a listener that listens open/close actions
     */
    protected MenuStateChangeListener stateChangeListener;
    /**
     * whether the openings and closings should be animated or not
     */
    protected boolean animated;
    /**
     * whether the menu is currently open or not
     */
    protected boolean open;
    /**
     * whether the menu is an overlay for all other activities
     */
    protected boolean systemOverlay;
    /**
     * a simple layout to contain all the sub action views in the system overlay mode
     */
    protected FrameLayout overlayContainer;

    protected OrientationEventListener orientationListener;

    protected OnFloatingActionClickListener mFloatingActionClickListener;

    /**
     * Constructor that takes the parameters collected using {@link BaseBuilder}
     *
     * @param mainActionView
     * @param subActionItems
     * @param animationHandler
     * @param animated
     * @param floatingActionClickListener
     */
    public FloatingActionMenu(final View mainActionView,
                              List<Item> subActionItems,
                              MenuAnimationHandler animationHandler,
                              boolean animated,
                              MenuStateChangeListener stateChangeListener,
                              OnFloatingActionClickListener floatingActionClickListener,
                              final boolean systemOverlay) {
        this.mainActionView = mainActionView;
        this.subActionItems = subActionItems;
        this.animationHandler = animationHandler;
        this.animated = animated;
        this.systemOverlay = systemOverlay;
        // The menu is initially closed.
        this.open = false;

        this.stateChangeListener = stateChangeListener;
        this.mFloatingActionClickListener = floatingActionClickListener;

        // Listen click events on the main action view
        // In the future, touch and drag events could be listened to offer an alternative behaviour
        this.mainActionView.setClickable(true);
        this.mainActionView.setOnClickListener(new FloatingActionMenu.ActionViewClickListener());

        // Do not forget to set the menu as self to our customizable animation handler
        if (animationHandler != null) {
            animationHandler.setMenu(this);
        }

        if (systemOverlay) {
            overlayContainer = new FrameLayout(mainActionView.getContext());
        } else {
            overlayContainer = null; // beware NullPointerExceptions!
        }

        // Find items with undefined sizes
        for (final Item item : subActionItems) {
            item.view.setOnClickListener(this);
            if (item.width == 0 || item.height == 0) {
                if (systemOverlay) {
                    throw new RuntimeException("Sub action views cannot be added without " +
                            "definite width and height.");
                }
                // Figure out the size by temporarily adding it to the Activity content view hierarchy
                // and ask the size from the system
                addViewToCurrentContainer(item.view);
                // Make item view invisible, just in case
                item.view.setAlpha(0);
                // Wait for the right time
                if (item.label != null){
                    addViewToCurrentContainer(item.label);
                    item.label.setAlpha(0);
                    item.label.setOnClickListener(this);
                }
                item.view.post(new FloatingActionMenu.ItemViewQueueListener(item));
            }
        }

        if (systemOverlay) {
            orientationListener = new OrientationEventListener(mainActionView.getContext(), SensorManager.SENSOR_DELAY_UI) {
                private int lastState = -1;

                public void onOrientationChanged(int orientation) {

                    Display display = getWindowManager().getDefaultDisplay();
                    if (display.getRotation() != lastState) {
                        lastState = display.getRotation();

                        //
                        if (isOpen()) {
                            close(false);
                        }
                    }
                }
            };
            orientationListener.enable();
        }
    }

    /**
     * Simply opens the menu by doing necessary calculations.
     *
     * @param animated if true, this action is executed by the current {@link MenuAnimationHandler}
     */
    public void open(boolean animated) {

        // Get the center of the action view from the following function for efficiency
        // populate destination x,y coordinates of Items
        Point center = calculateItemPositions();

        WindowManager.LayoutParams overlayParams = null;

        if (systemOverlay) {
            // If this is a system overlay menu, use the overlay container and place it behind
            // the main action button so that all the views will be added into it.
            attachOverlayContainer();

            overlayParams = (WindowManager.LayoutParams) overlayContainer.getLayoutParams();
        }

        if (animated && animationHandler != null) {
            // If animations are enabled and we have a MenuAnimationHandler, let it do the heavy work
            if (animationHandler.isAnimating()) {
                // Do not proceed if there is an animation currently going on.
                return;
            }

            for (int i = 0; i < subActionItems.size(); i++) {
                // It is required that these Item views are not currently added to any parent
                // Because they are supposed to be added to the Activity content view,
                // just before the animation starts
                if (subActionItems.get(i).view.getParent() != null) {
                    throw new RuntimeException("All of the sub action items have to be independent from a parent.");
                }

                // Initially, place all items right at the center of the main action view
                // Because they are supposed to start animating from that point.
                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subActionItems.get(i).width, subActionItems.get(i).height, Gravity.TOP | Gravity.LEFT);

                if (systemOverlay) {
                    params.setMargins(center.x - overlayParams.x - subActionItems.get(i).width / 2, center.y - overlayParams.y - subActionItems.get(i).height / 2, 0, 0);
                } else {
                    params.setMargins(center.x - subActionItems.get(i).width / 2, center.y - subActionItems.get(i).height / 2, 0, 0);
                }
                addViewToCurrentContainer(subActionItems.get(i).view, params);

                if (subActionItems.get(i).label != null) {
                    final FrameLayout.LayoutParams paramsLabel = new FrameLayout.LayoutParams(subActionItems.get(i).labelWidth, subActionItems.get(i).labelHeight, Gravity.TOP | Gravity.LEFT);

                    if (systemOverlay) {
                        paramsLabel.setMargins(center.x - overlayParams.x - subActionItems.get(i).labelWidth / 2, center.y - overlayParams.y - subActionItems.get(i).labelHeight / 2, 0, 0);
                    } else {
                        paramsLabel.setMargins(center.x - subActionItems.get(i).labelWidth / 2, center.y - subActionItems.get(i).labelHeight / 2, 0, 0);
                    }
                    addViewToCurrentContainer(subActionItems.get(i).label, paramsLabel);
                }
            }
            // Tell the current MenuAnimationHandler to animate from the center
            animationHandler.animateMenuOpening(center);
        } else {
            // If animations are disabled, just place each of the items to their calculated destination positions.
            for (int i = 0; i < subActionItems.size(); i++) {
                // This is currently done by giving them large margins

                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subActionItems.get(i).width, subActionItems.get(i).height, Gravity.TOP | Gravity.LEFT);
                if (systemOverlay) {
                    params.setMargins(subActionItems.get(i).x - overlayParams.x, subActionItems.get(i).y - overlayParams.y, 0, 0);
                    subActionItems.get(i).view.setLayoutParams(params);
                } else {
                    params.setMargins(subActionItems.get(i).x, subActionItems.get(i).y, 0, 0);
                    subActionItems.get(i).view.setLayoutParams(params);
                    // Because they are placed into the main content view of the Activity,
                    // which is itself a FrameLayout
                }
                addViewToCurrentContainer(subActionItems.get(i).view, params);

                if (subActionItems.get(i).label != null){
                    final FrameLayout.LayoutParams paramsLabel = new FrameLayout.LayoutParams(subActionItems.get(i).labelWidth, subActionItems.get(i).labelHeight, Gravity.TOP | Gravity.LEFT);
                    if (systemOverlay) {
                        paramsLabel.setMargins(subActionItems.get(i).labelX - overlayParams.x, subActionItems.get(i).labelY - overlayParams.y, 0, 0);
                        subActionItems.get(i).label.setLayoutParams(paramsLabel);
                    } else {
                        paramsLabel.setMargins(subActionItems.get(i).labelX, subActionItems.get(i).labelY, 0, 0);
                        subActionItems.get(i).label.setLayoutParams(paramsLabel);
                        // Because they are placed into the main content view of the Activity,
                        // which is itself a FrameLayout
                    }
                    addViewToCurrentContainer(subActionItems.get(i).label, paramsLabel);
                }
            }
        }
        // do not forget to specify that the menu is open.
        open = true;
        if (mFloatingActionClickListener != null) {
            mFloatingActionClickListener.onMainClick(true);
        }
        if (stateChangeListener != null) {
            stateChangeListener.onMenuOpened(this);
        }

    }

    /**
     * Closes the menu.
     *
     * @param animated if true, this action is executed by the current {@link MenuAnimationHandler}
     */
    public void close(boolean animated) {
        // If animations are enabled and we have a MenuAnimationHandler, let it do the heavy work
        if (animated && animationHandler != null) {
            if (animationHandler.isAnimating()) {
                // Do not proceed if there is an animation currently going on.
                return;
            }
            animationHandler.animateMenuClosing(getActionViewCenter());
        } else {
            // If animations are disabled, just detach each of the Item views from the Activity content view.
            for (int i = 0; i < subActionItems.size(); i++) {
                removeViewFromCurrentContainer(subActionItems.get(i).view);
                if (subActionItems.get(i).label != null){
                    removeViewFromCurrentContainer(subActionItems.get(i).label);
                }
            }
            detachOverlayContainer();
        }
        // do not forget to specify that the menu is now closed.
        open = false;
        if (mFloatingActionClickListener != null) {
            mFloatingActionClickListener.onMainClick(false);
        }
        if (stateChangeListener != null) {
            stateChangeListener.onMenuClosed(this);
        }
    }

    /**
     * Toggles the menu
     *
     * @param animated if true, the open/close action is executed by the current {@link MenuAnimationHandler}
     */
    public void toggle(boolean animated) {
        if (open) {
            close(animated);
        } else {
            open(animated);
        }
    }

    /**
     * @return whether the menu is open or not
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @return whether the menu is a system overlay or not
     */
    public boolean isSystemOverlay() {
        return systemOverlay;
    }

    public FrameLayout getOverlayContainer() {
        return overlayContainer;
    }

    /**
     * Recalculates the positions of each sub action item on demand.
     */
    public void updateItemPositions() {
        // Only update if the menu is currently open
        if (!isOpen()) {
            return;
        }
        // recalculate x,y coordinates of Items
        calculateItemPositions();

        // Simply update layout params for each item
        for (int i = 0; i < subActionItems.size(); i++) {
            // This is currently done by giving them large margins
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subActionItems.get(i).width, subActionItems.get(i).height, Gravity.TOP | Gravity.LEFT);
            params.setMargins(subActionItems.get(i).x, subActionItems.get(i).y, 0, 0);
            subActionItems.get(i).view.setLayoutParams(params);
        }
    }

    /**
     * Gets the coordinates of the main action view
     * This method should only be called after the main layout of the Activity is drawn,
     * such as when a user clicks the action button.
     *
     * @return a Point containing x and y coordinates of the top left corner of action view
     */
    protected Point getActionViewCoordinates() {
        int[] coords = new int[2];
        // This method returns a x and y values that can be larger than the dimensions of the device screen.
        mainActionView.getLocationOnScreen(coords);

        // So, we need to deduce the offsets.
        if (systemOverlay) {
            coords[1] -= getStatusBarHeight();
        } else {
            Rect activityFrame = new Rect();
            getActivityContentView().getWindowVisibleDisplayFrame(activityFrame);
            coords[0] -= (getScreenSize().x - getActivityContentView().getMeasuredWidth());
            coords[1] -= (activityFrame.height() + activityFrame.top - getActivityContentView().getMeasuredHeight());
        }
        return new Point(coords[0], coords[1]);
    }

    /**
     * Returns the center point of the main action view
     *
     * @return the action view center point
     */
    public Point getActionViewCenter() {
        Point point = getActionViewCoordinates();
        point.x += mainActionView.getMeasuredWidth() / 2;
        point.y += mainActionView.getMeasuredHeight() / 2;
        return point;
    }

    /**
     * Calculates the desired positions of all items.
     *
     * @return getActionViewCenter()
     */
    protected abstract Point calculateItemPositions();


    /**
     * @return a reference to the sub action items list
     */
    public List<Item> getSubActionItems() {
        return subActionItems;
    }

    /**
     * @return a reference to the main action view
     */
    public View getMainActionView() {
        return mainActionView;
    }

    /**
     * Finds and returns the main content view from the Activity context.
     *
     * @return the main content view
     */
    public View getActivityContentView() {
        try {
            return ((Activity) mainActionView.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (ClassCastException e) {
            throw new ClassCastException("Please provide an Activity context for this FloatingActionCircleMenu.");
        }
    }

    /**
     * Intended to use for systemOverlay mode.
     *
     * @return the WindowManager for the current context.
     */
    public WindowManager getWindowManager() {
        return (WindowManager) mainActionView.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    private void addViewToCurrentContainer(View view, ViewGroup.LayoutParams layoutParams) {
        if (systemOverlay) {
            overlayContainer.addView(view, layoutParams);
        } else {
            try {
                if (layoutParams != null) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutParams;
                    ((ViewGroup) getActivityContentView()).addView(view, lp);
                } else {
                    ((ViewGroup) getActivityContentView()).addView(view);
                }
            } catch (ClassCastException e) {
                throw new ClassCastException("layoutParams must be an instance of " +
                        "FrameLayout.LayoutParams.");
            }
        }
    }

    public void attachOverlayContainer() {
        try {
            WindowManager.LayoutParams overlayParams = calculateOverlayContainerParams();

            overlayContainer.setLayoutParams(overlayParams);
            if (overlayContainer.getParent() == null) {
                getWindowManager().addView(overlayContainer, overlayParams);
            }
            getWindowManager().updateViewLayout(mainActionView, mainActionView.getLayoutParams());
        } catch (SecurityException e) {
            throw new SecurityException("Your application must have SYSTEM_ALERT_WINDOW " +
                    "permission to create a system window.");
        }
    }

    private WindowManager.LayoutParams calculateOverlayContainerParams() {
        // calculate the minimum viable size of overlayContainer
        WindowManager.LayoutParams overlayParams = getDefaultSystemWindowParams();
//        int left = 9999, right = 0, top = 9999, bottom = 0;
//        for (int i = 0; i < subActionItems.size(); i++) {
//            int lm = subActionItems.get(i).x - 10;
//            int tm = subActionItems.get(i).y - 10;
//
//            if (lm < left) {
//                left = lm;
//            }
//            if (tm < top) {
//                top = tm;
//            }
//            if (lm + subActionItems.get(i).width + 10 > right) {
//                right = lm + subActionItems.get(i).width + 10;
//            }
//            if (tm + subActionItems.get(i).height + 10 > bottom) {
//                bottom = tm + subActionItems.get(i).height + 10;
//            }
//        }
//        overlayParams.width = right - left;
//        overlayParams.height = bottom - top ;
//        overlayParams.x = left;
//        overlayParams.y = top;
        overlayParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        overlayParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        return overlayParams;
    }


    public void detachOverlayContainer() {
        getWindowManager().removeView(overlayContainer);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mainActionView.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mainActionView.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void addViewToCurrentContainer(View view) {
        addViewToCurrentContainer(view, null);
    }


    public void removeViewFromCurrentContainer(View view) {
        if (systemOverlay) {
            overlayContainer.removeView(view);
        } else {
            ((ViewGroup) getActivityContentView()).removeView(view);
        }
    }

    /**
     * Retrieves the screen size from the Activity context
     *
     * @return the screen size as a Point object
     */
    private Point getScreenSize() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    public void setStateChangeListener(MenuStateChangeListener listener) {
        this.stateChangeListener = listener;
    }

    public OnFloatingActionClickListener getFloatingActionClickListener() {
        return mFloatingActionClickListener;
    }

    public void setFloatingActionClickListener(OnFloatingActionClickListener floatingActionClickListener) {
        mFloatingActionClickListener = floatingActionClickListener;
    }

    @Override
    public void onClick(View view) {
        if (mFloatingActionClickListener != null) {
            mFloatingActionClickListener.onSubClick(view.getId());
        }
    }

    /**
     * A simple click listener used by the main action view
     */
    public class ActionViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            toggle(animated);
        }
    }

    /**
     * This runnable calculates sizes of Item views that are added to the menu.
     */
    protected class ItemViewQueueListener implements Runnable {

        private static final int MAX_TRIES = 10;
        private Item item;
        private int tries;

        public ItemViewQueueListener(Item item) {
            this.item = item;
            this.tries = 0;
        }

        @Override
        public void run() {
            // Wait until the the view can be measured but do not push too hard.
            if (item.view.getMeasuredWidth() == 0 && tries < MAX_TRIES) {
                item.view.post(this);
                return;
            }
            // Measure the size of the item view
            item.width = item.view.getMeasuredWidth();
            item.height = item.view.getMeasuredHeight();
            if (item.label != null){
                item.labelWidth = item.label.getMeasuredWidth();
                item.labelHeight = item.label.getMeasuredHeight();
                item.label.setAlpha(item.alpha);
                removeViewFromCurrentContainer(item.label);
            }
            // Revert everything back to normal
            item.view.setAlpha(item.alpha);
            // Remove the item view from view hierarchy
            removeViewFromCurrentContainer(item.view);
        }
    }

    public static WindowManager.LayoutParams getDefaultSystemWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    /**
     * A builder for {@link FloatingActionMenu} in conventional Java BaseBuilder format
     */
    protected abstract static class BaseBuilder<T> {

        protected View actionView;
        protected List<Item> subActionItems;
        protected MenuAnimationHandler animationHandler;
        protected boolean animated;
        protected MenuStateChangeListener stateChangeListener;
        protected OnFloatingActionClickListener mFloatingActionClickListener;
        protected boolean systemOverlay;

        public BaseBuilder(Context context, boolean systemOverlay) {
            subActionItems = new ArrayList<>();
            animationHandler = new DefaultAnimationHandler();
            animated = true;
            this.systemOverlay = systemOverlay;
        }

        public BaseBuilder(Context context) {
            this(context, false);
        }


        public FrameLayout.LayoutParams getDefaultLayoutParams() {
            return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @SuppressWarnings("unchecked")
        public T addSubActionView(View subActionView, int width, int height) {
            subActionView.setId(subActionItems.size());
            subActionItems.add(new Item(subActionView, width, height));
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addSubFABView(Context context, ColorStateList color, @DrawableRes int resId, @FloatingActionButton.Size int size, int width, int height) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(size);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            fab.setBackgroundTintList(color);
            subActionItems.add(new Item(fab, width, height));
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addSubFABView(Context context, @DrawableRes int resId, @FloatingActionButton.Size int size, int width, int height) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(size);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            subActionItems.add(new Item(fab, width, height));
            return (T) this;
        }


        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @param subActionView a view for the menu
         * @return the builder object itself
         */
        @SuppressWarnings("unchecked")
        public T addSubActionView(View subActionView) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            if (!(subActionView instanceof SubActionButton)) {
                subActionView.setLayoutParams(getDefaultLayoutParams());
            }
            addSubActionView(subActionView, 0, 0);
            return (T) this;
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public T addSubFABView(Context context, ColorStateList color, @DrawableRes int resId) {
            return addSubFABView(context, color, resId, FloatingActionButton.SIZE_MINI);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public T addSubFABView(Context context, @DrawableRes int resId) {
            return addSubFABView(context, resId, FloatingActionButton.SIZE_MINI);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public T addSubFABView(Context context, ColorStateList color, @DrawableRes int resId, @FloatingActionButton.Size int size) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            return addSubFABView(context, color, resId, size, 0, 0);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public T addSubFABView(Context context, @DrawableRes int resId, @FloatingActionButton.Size int size) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            return addSubFABView(context, resId, size, 0, 0);
        }


        /**
         * Inflates a new view from the specified resource id and adds it as a sub action view.
         *
         * @param resId   the resource id reference for the view
         * @param context a valid context
         * @return the builder object itself
         */
        public T addSubActionView(int resId, Context context) {
            View view = LayoutInflater.from(context).inflate(resId, null, false);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            return addSubActionView(view, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        /**
         * Sets the current animation handler to the specified MenuAnimationHandler child
         *
         * @param animationHandler a MenuAnimationHandler child
         * @return the builder object itself
         */
        @SuppressWarnings("unchecked")
        public T setAnimationHandler(MenuAnimationHandler animationHandler) {
            this.animationHandler = animationHandler;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T enableAnimations() {
            animated = true;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T disableAnimations() {
            animated = false;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T setStateChangeListener(MenuStateChangeListener listener) {
            stateChangeListener = listener;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T setFloatingActionClickListener(OnFloatingActionClickListener listener) {
            mFloatingActionClickListener = listener;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T setSystemOverlay(boolean systemOverlay) {
            this.systemOverlay = systemOverlay;
            return (T) this;
        }

        /**
         * Attaches the whole menu around a main action view, usually a button.
         * All the calculations are made according to this action view.
         *
         * @param actionView
         * @return the builder object itself
         */
        @SuppressWarnings("unchecked")
        public T attachTo(View actionView) {
            this.actionView = actionView;
            return (T) this;
        }

        public abstract <VT extends FloatingActionMenu> VT build();
    }

}
