package com.wang.floatingactionmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.wang.floatingactionmenu.animation.MenuAnimationHandler;
import com.wang.floatingactionmenu.interfaces.MenuStateChangeListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.model.Item;

import java.util.List;

/**
 * Provides the main structure of the menu.
 */
public class FloatingActionLineMenu2 extends FloatingActionMenu {

    public static final int EXPAND_UP = 0;
    public static final int EXPAND_DOWN = 1;
    public static final int EXPAND_LEFT = 2;
    public static final int EXPAND_RIGHT = 3;

    public static final int LABELS_ON_LEFT_SIDE = 0;
    public static final int LABELS_ON_RIGHT_SIDE = 1;
    public static final int LABELS_ON_UP_SIDE = 2;
    public static final int LABELS_ON_DOWN_SIDE = 3;

    private int mLabelsMargin;

    private int mButtonSpacing;

    /**
     * the subView expand direction
     */
    private int mExpandDirection;
    /**
     * TextView style
     */
    private int mLabelsStyle;
    /**
     * TextView location
     */
    private int mLabelsPosition;

    /**
     * Constructor that takes the parameters collected using {@link Builder}
     */
    public FloatingActionLineMenu2(View mainActionView, int buttonSpacing, int labelsMargin,
                                   int labelsVerticalOffset, int expandDirection, int labelsStyle,
                                   int labelsPosition, List<Item> subActionItems,
                                   MenuAnimationHandler animationHandler, boolean animated,
                                   MenuStateChangeListener stateChangeListener,
                                   OnFloatingActionClickListener floatingActionClickListener,
                                   boolean systemOverlay) {
        super(mainActionView, subActionItems, animationHandler, animated, stateChangeListener,
                floatingActionClickListener, systemOverlay);
        mLabelsStyle = labelsStyle;
        mLabelsPosition = labelsPosition;
        mExpandDirection = expandDirection;
        mButtonSpacing = buttonSpacing;
        mLabelsMargin = labelsMargin;
    }

    @Override
    protected Point calculateItemPositions() {
        final Point center = getActionViewCenter();

        switch (mExpandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:
                boolean expandUp = mExpandDirection == EXPAND_UP;

                int nextY = expandUp ?
                        center.y - mainActionView.getMeasuredHeight() / 2 - mButtonSpacing :
                        center.y + mainActionView.getMeasuredHeight()/ 2 + mButtonSpacing;

                for (int i = 0; i < subActionItems.size(); i++) {
                    Item item = subActionItems.get(i);
                    item.x = center.x - item.width / 2;
                    item.y = expandUp ? nextY - item.height : nextY;

                    View label = item.label;
                    if (label != null) {

                        int labelsXNearButton = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? item.x - mLabelsMargin
                                : item.x + mLabelsMargin + item.width;

                        int labelXAwayFromButton = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? labelsXNearButton - item.labelWidth
                                : labelsXNearButton + item.labelWidth;

                        item.labelX = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? labelXAwayFromButton
                                : labelsXNearButton;

                        item.labelY = item.y + item.height / 2 - item.labelHeight / 2;

                    }

                    nextY = expandUp ?
                            item.y - mButtonSpacing :
                            item.y + item.height + mButtonSpacing;
                }
                break;

            case EXPAND_LEFT:
            case EXPAND_RIGHT:
                boolean expandLeft = mExpandDirection == EXPAND_LEFT;


                int nextX = expandLeft ?
                        center.x - mainActionView.getMeasuredWidth() / 2 - mButtonSpacing :
                        center.x + mainActionView.getMeasuredWidth() / 2 + mButtonSpacing;

                for (int i = 0; i < subActionItems.size(); i++) {
                    Item item = subActionItems.get(i);

                    item.x = expandLeft ? nextX - item.width : nextX;
                    item.y = center.y - item.height / 2;


                    View label = item.label;
                    if (label != null) {

                        int labelsYNearButton = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? item.y - mLabelsMargin
                                : item.y + mLabelsMargin + item.height;

                        int labelYAwayFromButton = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? labelsYNearButton - item.labelHeight
                                : labelsYNearButton + item.labelHeight;

                        item.labelY = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? labelYAwayFromButton
                                : labelsYNearButton;

                        item.labelX = item.x + item.width / 2 - item.labelWidth / 2;

                    }


                    nextX = expandLeft ?
                            item.x - mButtonSpacing :
                            item.x + item.width + mButtonSpacing;
                }

                break;
        }
        return center;
    }


    public static class Builder extends BaseBuilder<Builder> {

        private int mLabelsVerticalOffset;

        private int mLabelsMargin;

        private int mButtonSpacing;

        /**
         * the subView expand direction
         */
        private int mExpandDirection;
        /**
         * TextView style
         */
        private int mLabelsStyle;
        /**
         * TextView location
         */
        private int mLabelsPosition;

        public Builder(Context context, boolean systemOverlay) {
            super(context, systemOverlay);
            mButtonSpacing = (int) (context.getResources().getDimension(R.dimen.fab_actions_spacing) - context.getResources().getDimension(R.dimen.fab_shadow_radius) - context.getResources().getDimension(R.dimen.fab_shadow_offset));
            mLabelsMargin = context.getResources().getDimensionPixelSize(R.dimen.fab_labels_margin);
            mLabelsVerticalOffset = context.getResources().getDimensionPixelSize(R.dimen.fab_shadow_offset);
        }

        public Builder(Context context) {
            this(context, false);
        }

        public Builder setExpandDirection(int expandDirection) {
            mExpandDirection = expandDirection;
            return this;
        }

        /**
         * set label TextView style, must before addSubView
         * @param labelStyle
         * @return
         */
        public Builder setLabelStyle(int labelStyle) {
            mLabelsStyle = labelStyle;
            return this;
        }

        public Builder setLabelPosition(int labelsPosition) {
            mLabelsPosition = labelsPosition;
            return this;
        }

        public Builder setLabelsVerticalOffset(int labelsVerticalOffset) {
            mLabelsVerticalOffset = labelsVerticalOffset;
            return this;
        }

        public Builder setButtonSpacing(int buttonSpacing) {
            mButtonSpacing = buttonSpacing;
            return this;
        }

        public Builder setLabelsMargin(int labelsMargin) {
            mLabelsMargin = labelsMargin;
            return this;
        }

        @Override
        public Builder addSubFABView(Context context, @DrawableRes int resId) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(FloatingActionButton.SIZE_MINI);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            VerTextView label = new VerTextView(context);
            label.setText("我是天才");
            label.setTextColor(Color.RED);
//            label.setBackgroundColor(Color.BLACK);
            label.setLayoutParams(getDefaultLayoutParams());
            subActionItems.add(new Item(fab, label, 0, 0));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public FloatingActionLineMenu2 build() {
            return new FloatingActionLineMenu2(
                    actionView,
                    mButtonSpacing,
                    mLabelsMargin,
                    mLabelsVerticalOffset,
                    mExpandDirection,
                    mLabelsStyle,
                    mLabelsPosition,
                    subActionItems,
                    animationHandler,
                    animated,
                    stateChangeListener,
                    mFloatingActionClickListener,
                    systemOverlay
            );
        }
    }
}
