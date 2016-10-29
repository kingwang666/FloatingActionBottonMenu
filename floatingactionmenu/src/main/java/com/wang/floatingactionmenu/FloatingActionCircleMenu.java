package com.wang.floatingactionmenu;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;


import com.wang.floatingactionmenu.animation.MenuAnimationHandler;
import com.wang.floatingactionmenu.interfaces.MenuStateChangeListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.model.Item;

import java.util.List;

/**
 * Provides the main structure of the menu.
 */

public class FloatingActionCircleMenu extends FloatingActionMenu{
    /**
     * The angle (in degrees, modulus 360) which the circular menu starts from
     */
    private int startAngle;
    /**
     * The angle (in degrees, modulus 360) which the circular menu ends at
     */
    private int endAngle;
    /**
     * Distance of menu items from mainActionView
     */
    private int radius;


    /**
     * Constructor that takes the parameters collected using {@link FloatingActionCircleMenu.Builder}
     *
     * @param mainActionView
     * @param startAngle
     * @param endAngle
     * @param radius
     * @param subActionItems
     * @param animationHandler
     * @param animated
     * @param floatingActionClickListener
     */
    public FloatingActionCircleMenu(final View mainActionView,
                                    int startAngle,
                                    int endAngle,
                                    int radius,
                                    List<Item> subActionItems,
                                    MenuAnimationHandler animationHandler,
                                    boolean animated,
                                    MenuStateChangeListener stateChangeListener,
                                    OnFloatingActionClickListener floatingActionClickListener,
                                    final boolean systemOverlay) {
        super(mainActionView, subActionItems, animationHandler, animated,stateChangeListener, floatingActionClickListener, systemOverlay);
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.radius = radius;
    }

    /**
     * Calculates the desired positions of all items.
     *
     * @return getActionViewCenter()
     */
    @Override
    protected Point calculateItemPositions() {
        // Create an arc that starts from startAngle and ends at endAngle
        // in an area that is as large as 4*radius^2
        final Point center = getActionViewCenter();
        RectF area = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);

        Path orbit = new Path();
        orbit.addArc(area, startAngle, endAngle - startAngle);

        PathMeasure measure = new PathMeasure(orbit, false);

        // Prevent overlapping when it is a full circle
        int divisor;
        if (Math.abs(endAngle - startAngle) >= 360 || subActionItems.size() <= 1) {
            divisor = subActionItems.size();
        } else {
            divisor = subActionItems.size() - 1;
        }

        // Measure this path, in order to find points that have the same distance between each other
        for (int i = 0; i < subActionItems.size(); i++) {
            float[] coords = new float[]{0f, 0f};
            measure.getPosTan((i) * measure.getLength() / divisor, coords, null);
            // get the x and y values of these points and set them to each of sub action items.
            Item item = subActionItems.get(i);
            item.x = (int) coords[0] - item.width / 2;
            item.y = (int) coords[1] - item.height / 2;
        }
        return center;
    }

    /**
     * @return the specified raduis of the menu
     */
    public int getRadius() {
        return radius;
    }

    /**
     * A builder for {@link FloatingActionCircleMenu} in conventional Java BaseBuilder format
     */
    public static class Builder extends BaseBuilder<Builder> {

        private int startAngle;
        private int endAngle;
        private int radius;

        public Builder(Context context, boolean systemOverlay) {
            super(context, systemOverlay);
            // Default settings
            radius = context.getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
            startAngle = 180;
            endAngle = 270;
        }

        public Builder(Context context) {
            this(context, false);
        }

        public Builder setStartAngle(int startAngle) {
            this.startAngle = startAngle;
            return this;
        }

        public Builder setEndAngle(int endAngle) {
            this.endAngle = endAngle;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        @SuppressWarnings("unchecked")
        public FloatingActionCircleMenu build() {
            return new FloatingActionCircleMenu(
                    this.actionView,
                    this.startAngle,
                    this.endAngle,
                    this.radius,
                    this.subActionItems,
                    this.animationHandler,
                    this.animated,
                    this.stateChangeListener,
                    this.mFloatingActionClickListener,
                    this.systemOverlay);
        }

    }


}