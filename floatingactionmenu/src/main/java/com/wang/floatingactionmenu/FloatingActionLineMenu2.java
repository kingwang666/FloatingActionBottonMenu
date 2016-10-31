package com.wang.floatingactionmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wang.floatingactionmenu.animation.MenuAnimationHandler;
import com.wang.floatingactionmenu.interfaces.MenuStateChangeListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionLabelClickListener;
import com.wang.floatingactionmenu.model.Item;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static android.support.annotation.RestrictTo.Scope.GROUP_ID;

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

    @RestrictTo(GROUP_ID)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EXPAND_UP, EXPAND_DOWN, EXPAND_LEFT, EXPAND_RIGHT})
    public @interface ExpandDirection {
    }

    @RestrictTo(GROUP_ID)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LABELS_ON_LEFT_SIDE, LABELS_ON_RIGHT_SIDE, LABELS_ON_UP_SIDE, LABELS_ON_DOWN_SIDE})
    public @interface LabelsSide {
    }

    private int mLabelsMargin;

    private int mButtonSpacing;

    /**
     * the subView expand direction
     */
    @ExpandDirection
    private int mExpandDirection;
    /**
     * TextView style
     */
    private int mLabelsStyle;
    /**
     * TextView location
     */
    @LabelsSide
    private int mLabelsPosition;

    private OnFloatingActionLabelClickListener mLabelClickListener;

    /**
     * Constructor that takes the parameters collected using {@link Builder}
     */
    public FloatingActionLineMenu2(View mainActionView, int buttonSpacing, int labelsMargin,
                                   @ExpandDirection int expandDirection, int labelsStyle,
                                   @LabelsSide int labelsPosition, List<Item> subActionItems,
                                   MenuAnimationHandler animationHandler, boolean animated,
                                   MenuStateChangeListener stateChangeListener,
                                   OnFloatingActionClickListener floatingActionClickListener,
                                   OnFloatingActionLabelClickListener labelClickListener,
                                   boolean systemOverlay) {
        super(mainActionView, subActionItems, animationHandler, animated, stateChangeListener,
                floatingActionClickListener, systemOverlay);
        mLabelsStyle = labelsStyle;
        mLabelsPosition = labelsPosition;
        mExpandDirection = expandDirection;
        mButtonSpacing = buttonSpacing;
        mLabelsMargin = labelsMargin;
        mLabelClickListener = labelClickListener;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof VerticalTextView2){
            if (mLabelClickListener != null) {
                mLabelClickListener.onLabelClick((Integer) view.getTag());
            }
        }
        else {
            super.onClick(view);
        }
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
                        center.y + mainActionView.getMeasuredHeight() / 2 + mButtonSpacing;

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

        private int mLabelsMargin;

        private int mButtonSpacing;

        /**
         * the subView expand direction
         */
        @ExpandDirection
        private int mExpandDirection;
        /**
         * TextView style
         */
        @StyleRes
        private int mLabelsStyle;
        /**
         * TextView location
         */
        @LabelsSide
        private int mLabelsPosition;

        private OnFloatingActionLabelClickListener mLabelClickListener;

        public Builder(Context context, boolean systemOverlay) {
            super(context, systemOverlay);
            mButtonSpacing = (int) (context.getResources().getDimension(R.dimen.fab_actions_spacing) - context.getResources().getDimension(R.dimen.fab_shadow_radius) - context.getResources().getDimension(R.dimen.fab_shadow_offset));
            mLabelsMargin = context.getResources().getDimensionPixelSize(R.dimen.fab_labels_margin);
        }

        public Builder(Context context) {
            this(context, false);
        }

        public Builder setExpandDirection(@ExpandDirection int expandDirection) {
            mExpandDirection = expandDirection;
            return this;
        }

        /**
         * set label TextView style, must before addSubView
         *
         * @param labelStyle
         * @return
         */
        public Builder setLabelStyle(@StyleRes int labelStyle) {
            mLabelsStyle = labelStyle;
            return this;
        }

        public Builder setLabelClickListener(OnFloatingActionLabelClickListener labelClickListener) {
            mLabelClickListener = labelClickListener;
            return this;
        }

        public Builder setLabelPosition(@LabelsSide int labelsPosition) {
            mLabelsPosition = labelsPosition;
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

        public Builder addSubActionView(View subActionView, String text, @ColorInt int textColor, int width, int height) {
            subActionView.setId(subActionItems.size());
            Context context = new ContextThemeWrapper(subActionView.getContext(), mLabelsStyle);
            VerticalTextView2 label = new VerticalTextView2(context);
            label.setText(text);
            label.setTextColor(textColor);
            label.setLayoutParams(getDefaultLayoutParams());
            label.setTag(subActionItems.size());
            subActionItems.add(new Item(subActionView, label, width, height));
            return this;
        }

        public Builder addSubActionView(View subActionView, String text, int width, int height) {
            subActionView.setId(subActionItems.size());
            Context context = new ContextThemeWrapper(subActionView.getContext(), mLabelsStyle);
            VerticalTextView2 label = new VerticalTextView2(context);
            label.setText(text);
            label.setLayoutParams(getDefaultLayoutParams());
            label.setTag(subActionItems.size());
            subActionItems.add(new Item(subActionView, label, width, height));
            return this;
        }

        public Builder addSubFABView(Context context, String text, @ColorInt int textColor, ColorStateList color, @DrawableRes int resId, @FloatingActionButton.Size int size, int width, int height) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(size);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            fab.setBackgroundTintList(color);
            Context context2 = new ContextThemeWrapper(context, mLabelsStyle);
            VerticalTextView2 label = new VerticalTextView2(context2);
            label.setText(text);
            label.setTextColor(textColor);
            label.setLayoutParams(getDefaultLayoutParams());
            label.setTag(subActionItems.size());
            subActionItems.add(new Item(fab, label, width, height));
            return this;
        }

        public Builder addSubFABView(Context context, String text, ColorStateList color, @DrawableRes int resId, @FloatingActionButton.Size int size, int width, int height) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(size);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            fab.setBackgroundTintList(color);
            Context context2 = new ContextThemeWrapper(context, mLabelsStyle);
            VerticalTextView2 label = new VerticalTextView2(context2);
            label.setText(text);
            label.setLayoutParams(getDefaultLayoutParams());
            label.setTag(subActionItems.size());
            subActionItems.add(new Item(fab, label, width, height));
            return this;
        }


        public Builder addSubFABView(Context context, String text, @ColorInt int textColor, @DrawableRes int resId, @FloatingActionButton.Size int size, int width, int height) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(size);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            Context context2 = new ContextThemeWrapper(context, mLabelsStyle);
            VerticalTextView2 label = new VerticalTextView2(context2);
            label.setText(text);
            label.setTextColor(textColor);
            label.setLayoutParams(getDefaultLayoutParams());
            label.setTag(subActionItems.size());
            subActionItems.add(new Item(fab, label, width, height));
            return this;
        }

        public Builder addSubFABView(Context context, String text, @DrawableRes int resId, @FloatingActionButton.Size int size, int width, int height) {
            FloatingActionButton fab = new FloatingActionButton(context);
            fab.setSize(size);
            fab.setLayoutParams(getDefaultLayoutParams());
            fab.setId(subActionItems.size());
            fab.setImageResource(resId);
            Context context2 = new ContextThemeWrapper(context, mLabelsStyle);
            VerticalTextView2 label = new VerticalTextView2(context2);
            label.setText(text);
            label.setLayoutParams(getDefaultLayoutParams());
            label.setTag(subActionItems.size());
            subActionItems.add(new Item(fab, label, width, height));
            return this;
        }


        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @param subActionView a view for the menu
         * @return the builder object itself
         */
        public Builder addSubActionView(View subActionView, String text, @ColorInt int textColor) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            if (!(subActionView instanceof SubActionButton)) {
                subActionView.setLayoutParams(getDefaultLayoutParams());
            }
            return addSubActionView(subActionView, text, textColor, 0, 0);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @param subActionView a view for the menu
         * @return the builder object itself
         */
        public Builder addSubActionView(View subActionView, String text) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            if (!(subActionView instanceof SubActionButton)) {
                subActionView.setLayoutParams(getDefaultLayoutParams());
            }
            return addSubActionView(subActionView, text, 0, 0);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public Builder addSubFABView(Context context, String text, @ColorInt int textColor, ColorStateList color, @DrawableRes int resId) {
            return addSubFABView(context, text, textColor, color, resId, FloatingActionButton.SIZE_MINI);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public Builder addSubFABView(Context context, String text, ColorStateList color, @DrawableRes int resId) {
            return addSubFABView(context, text, color, resId, FloatingActionButton.SIZE_MINI);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public Builder addSubFABView(Context context, String text, @ColorInt int textColor, @DrawableRes int resId) {
            return addSubFABView(context, text, textColor, resId, FloatingActionButton.SIZE_MINI);
        }

        public Builder addSubFABView(Context context, String text, @DrawableRes int resId) {
            return addSubFABView(context, text, Color.BLACK, resId, FloatingActionButton.SIZE_MINI);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public Builder addSubFABView(Context context, String text, @ColorInt int textColor, ColorStateList color, @DrawableRes int resId, @FloatingActionButton.Size int size) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            return addSubFABView(context, text, textColor, color, resId, size, 0, 0);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public Builder addSubFABView(Context context, String text, ColorStateList color, @DrawableRes int resId, @FloatingActionButton.Size int size) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            return addSubFABView(context, text, color, resId, size, 0, 0);
        }

        /**
         * Adds a sub action view that is already alive, but not added to a parent View.
         *
         * @return the builder object itself
         */
        public Builder addSubFABView(Context context, String text, @ColorInt int textColor, @DrawableRes int resId, @FloatingActionButton.Size int size) {
            if (systemOverlay) {
                throw new RuntimeException("Sub action views cannot be added without " +
                        "definite width and height. Please use " +
                        "other methods named addSubActionView");
            }
            return addSubFABView(context, text, textColor, resId, size, 0, 0);
        }


        /**
         * Inflates a new view from the specified resource id and adds it as a sub action view.
         *
         * @param resId   the resource id reference for the view
         * @param context a valid context
         * @return the builder object itself
         */
        public Builder addSubActionView(int resId, Context context, String text, @ColorInt int textColor) {
            View view = LayoutInflater.from(context).inflate(resId, null, false);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            return addSubActionView(view, text, textColor, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        /**
         * Inflates a new view from the specified resource id and adds it as a sub action view.
         *
         * @param resId   the resource id reference for the view
         * @param context a valid context
         * @return the builder object itself
         */
        public Builder addSubActionView(int resId, Context context, String text) {
            View view = LayoutInflater.from(context).inflate(resId, null, false);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            return addSubActionView(view, text, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        @Override
        @SuppressWarnings("unchecked")
        public FloatingActionLineMenu2 build() {
            return new FloatingActionLineMenu2(
                    actionView,
                    mButtonSpacing,
                    mLabelsMargin,
                    mExpandDirection,
                    mLabelsStyle,
                    mLabelsPosition,
                    subActionItems,
                    animationHandler,
                    animated,
                    stateChangeListener,
                    mFloatingActionClickListener,
                    mLabelClickListener,
                    systemOverlay
            );
        }
    }
}
