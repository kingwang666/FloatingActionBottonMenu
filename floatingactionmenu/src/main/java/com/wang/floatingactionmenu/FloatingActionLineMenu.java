package com.wang.floatingactionmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.wang.floatingactionmenu.util.ShowHideHelper;

@Deprecated
public class FloatingActionLineMenu extends ViewGroup {
    public static final int EXPAND_UP = 0;
    public static final int EXPAND_DOWN = 1;
    public static final int EXPAND_LEFT = 2;
    public static final int EXPAND_RIGHT = 3;

    public static final int LABELS_ON_LEFT_SIDE = 0;
    public static final int LABELS_ON_RIGHT_SIDE = 1;
    public static final int LABELS_ON_UP_SIDE = 2;
    public static final int LABELS_ON_DOWN_SIDE = 3;

    private static final int ANIMATION_DURATION = 300;
    private static final float COLLAPSED_PLUS_ROTATION = 0f;
    private static final float EXPANDED_PLUS_ROTATION = 90f + 45f;

    private int mBackgroundColor;

    private int mAddButtonPlusColor;
    private int mAddButtonColorNormal;
    private int mAddButtonColorPressed;
    private int mAddButtonSize;
    @DrawableRes
    private int mAddButtonIcon;
    private boolean mAddButtonStrokeVisible;
    private int mExpandDirection;

    private int mButtonSpacing;
    private int mLabelsMargin;
    private int mLabelsVerticalOffset;

    private boolean mExpanded;

    private AnimatorSet mExpandAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);
    private AnimatorSet mCollapseAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);


    private CustomFloatingActionButton mAddButton;
    private RotatingDrawable mRotatingDrawable;
    private int mMaxButtonWidth;
    private int mMaxButtonHeight;
    private int mLabelsStyle;
    private int mLabelsPosition;
    private int mButtonsCount;

    public boolean mCanHide;


    private OnFloatingActionsMenuUpdateListener mListener;

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }

    public FloatingActionLineMenu(Context context) {
        this(context, null);
    }

    public FloatingActionLineMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionLineMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mButtonSpacing = (int) (getResources().getDimension(R.dimen.fab_actions_spacing) - getResources().getDimension(R.dimen.fab_shadow_radius) - getResources().getDimension(R.dimen.fab_shadow_offset));
        mLabelsMargin = getResources().getDimensionPixelSize(R.dimen.fab_labels_margin);
        mLabelsVerticalOffset = getResources().getDimensionPixelSize(R.dimen.fab_shadow_offset);


        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionLineMenu, 0, 0);

        mBackgroundColor = attr.getColor(R.styleable.FloatingActionLineMenu_fab_backgroundColor, getColor(android.R.color.transparent));

        mAddButtonPlusColor = attr.getColor(R.styleable.FloatingActionLineMenu_fab_addButtonPlusIconColor, getColor(android.R.color.white));
        mAddButtonColorNormal = attr.getColor(R.styleable.FloatingActionLineMenu_fab_addButtonColorNormal, getColor(android.R.color.holo_blue_dark));
        mAddButtonColorPressed = attr.getColor(R.styleable.FloatingActionLineMenu_fab_addButtonColorPressed, getColor(android.R.color.holo_blue_light));
        mAddButtonIcon = attr.getResourceId(R.styleable.FloatingActionLineMenu_fab_addButtonIcon, 0);
        mAddButtonSize = attr.getInt(R.styleable.FloatingActionLineMenu_fab_addButtonSize, CustomFloatingActionButton.SIZE_NORMAL);
        mAddButtonStrokeVisible = attr.getBoolean(R.styleable.FloatingActionLineMenu_fab_addButtonStrokeVisible, true);
        mExpandDirection = attr.getInt(R.styleable.FloatingActionLineMenu_fab_expandDirection, EXPAND_UP);
        mLabelsStyle = attr.getResourceId(R.styleable.FloatingActionLineMenu_fab_labelStyle, 0);
        mLabelsPosition = attr.getInt(R.styleable.FloatingActionLineMenu_fab_labelsPosition, LABELS_ON_LEFT_SIDE);
        mCanHide = attr.getBoolean(R.styleable.FloatingActionLineMenu_fab_canHide, false);
        attr.recycle();

//        if (mLabelsStyle != 0 && expandsHorizontally()) {
//            throw new IllegalStateException("Action labels in horizontal expand orientation is not supported.");
//        }
        if (mBackgroundColor != Color.TRANSPARENT) {
            initBackgroundDimAnimation();
        }
        createAddButton(context);
    }

    public void setOnFloatingActionsMenuUpdateListener(OnFloatingActionsMenuUpdateListener listener) {
        mListener = listener;
    }

    private boolean expandsHorizontally() {
        return mExpandDirection == EXPAND_LEFT || mExpandDirection == EXPAND_RIGHT;
    }


    private void createAddButton(Context context) {
        mAddButton = new CustomFloatingActionButton(context) {
            @Override
            void updateBackground() {
                mColorNormal = mAddButtonColorNormal;
                mColorPressed = mAddButtonColorPressed;
                mStrokeVisible = mAddButtonStrokeVisible;
                super.updateBackground();
            }

            @Override
            Drawable getIconDrawable() {
                final Drawable drawable;
                if (mAddButtonIcon != 0) {
                    drawable = ContextCompat.getDrawable(getContext(), mAddButtonIcon);
                } else {
                    RotatingDrawable rotatingDrawable = new RotatingDrawable(getDefaultIcon(mAddButtonPlusColor));
                    mRotatingDrawable = rotatingDrawable;
                    drawable = rotatingDrawable;
                    final OvershootInterpolator interpolator = new OvershootInterpolator();

                    final ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(rotatingDrawable, "rotation", EXPANDED_PLUS_ROTATION, COLLAPSED_PLUS_ROTATION);
                    final ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(rotatingDrawable, "rotation", COLLAPSED_PLUS_ROTATION, EXPANDED_PLUS_ROTATION);

                    collapseAnimator.setInterpolator(interpolator);
                    expandAnimator.setInterpolator(interpolator);

                    mExpandAnimation.play(expandAnimator);
                    mCollapseAnimation.play(collapseAnimator);
                }
                return drawable;
            }
        };

//    mAddButton.setId(R.id.fab_expand_menu_button);
        mAddButton.setSize(mAddButtonSize);
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        addView(mAddButton, generateDefaultLayoutParams());
        mButtonsCount++;
    }

    public void hide() {
        mAddButton.hide();
    }

    public void show() {
        mAddButton.show();
    }

    public void setOnShowHideListener(ShowHideHelper.OnShowHideListener listener) {
        mAddButton.setOnShowHideListener(listener);
    }

    public void show(int time) {
        mAddButton.show(time);
    }

    public boolean isShow() {
        return mAddButton.isShow();
    }

    private Drawable getDefaultIcon(int color) {
        final float iconSize = getResources().getDimension(R.dimen.fab_icon_size);
        final float iconHalfSize = iconSize / 2f;

        final float plusSize = getResources().getDimension(R.dimen.fab_plus_icon_size);
        final float plusHalfStroke = getResources().getDimension(R.dimen.fab_plus_icon_stroke) / 2f;
        final float plusOffset = (iconSize - plusSize) / 2f;

        final Shape shape = new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawRect(plusOffset, iconHalfSize - plusHalfStroke, iconSize - plusOffset, iconHalfSize + plusHalfStroke, paint);
                canvas.drawRect(iconHalfSize - plusHalfStroke, plusOffset, iconHalfSize + plusHalfStroke, iconSize - plusOffset, paint);
            }
        };

        ShapeDrawable drawable = new ShapeDrawable(shape);

        final Paint paint = drawable.getPaint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        return drawable;
    }

    private void initBackgroundDimAnimation() {
        final int maxAlpha = Color.alpha(mBackgroundColor);
        final int red = Color.red(mBackgroundColor);
        final int green = Color.green(mBackgroundColor);
        final int blue = Color.blue(mBackgroundColor);

        ValueAnimator mShowBackgroundAnimator = ValueAnimator.ofInt(0, maxAlpha);
        mShowBackgroundAnimator.setDuration(ANIMATION_DURATION);
        mShowBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer) animation.getAnimatedValue();
                setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        });
        mExpandAnimation.play(mShowBackgroundAnimator);

        ValueAnimator mHideBackgroundAnimator = ValueAnimator.ofInt(maxAlpha, 0);
        mHideBackgroundAnimator.setDuration(ANIMATION_DURATION);
        mHideBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer) animation.getAnimatedValue();
                setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        });
        mCollapseAnimation.play(mHideBackgroundAnimator);
    }

    public void addButton(CustomFloatingActionButton button) {
        addView(button, mButtonsCount - 1);
        mButtonsCount++;

        if (mLabelsStyle != 0) {
            createLabels();
        }
    }

    public void removeButton(CustomFloatingActionButton button) {
        removeView(button.getLabelView());
        removeView(button);
        button.setTag(R.id.fab_label, null);
        mButtonsCount--;
    }

    private int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

//        mMaxButtonWidth = 0;
//        mMaxButtonHeight = 0;
        int maxLabelWidth = 0;
        int maxLabelHeight = 0;

        for (int i = 0; i < mButtonsCount; i++) {
            int usedWidth = 0;
            int usedHeight = 0;

            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            switch (mExpandDirection) {
                case EXPAND_UP:
                case EXPAND_DOWN:
                    mMaxButtonWidth = Math.max(mMaxButtonWidth, child.getMeasuredWidth());
                    height += child.getMeasuredHeight();
                    usedWidth += child.getMeasuredWidth();
                    break;
                case EXPAND_LEFT:
                case EXPAND_RIGHT:
                    width += child.getMeasuredWidth();
                    usedHeight += child.getMeasuredHeight();
                    mMaxButtonHeight = Math.max(mMaxButtonHeight, child.getMeasuredHeight());
                    break;
            }

            if (!expandsHorizontally()) {
                TextView label = (TextView) child.getTag(R.id.fab_label);
                if (label != null) {
//                    注意
                    int labelOffset = (mMaxButtonWidth - child.getMeasuredWidth());
                    int labelUsedWidth = child.getMeasuredWidth() + mLabelsMargin + labelOffset;
                    measureChildWithMargins(label, widthMeasureSpec, labelUsedWidth, heightMeasureSpec, 0);
                    usedWidth += label.getMeasuredWidth();
                    maxLabelWidth = Math.max(maxLabelWidth, usedWidth + labelOffset);
                }
            } else {
                TextView label = (TextView) child.getTag(R.id.fab_label);
                if (label != null) {
//                    注意
                    int labelOffset = (mMaxButtonHeight - child.getMeasuredHeight());
                    int labelUsedHeight = child.getMeasuredHeight() + mLabelsMargin + labelOffset;
                    measureChildWithMargins(label, widthMeasureSpec, 0, heightMeasureSpec, labelUsedHeight);
                    usedHeight += label.getMeasuredHeight();
                    maxLabelHeight = Math.max(maxLabelHeight, usedHeight + labelOffset);
                }
            }
        }

        if (!expandsHorizontally()) {
            width = Math.max(mMaxButtonWidth, maxLabelWidth + mLabelsMargin) + getPaddingLeft() + getPaddingRight();
        } else {
            height = Math.max(mMaxButtonHeight, maxLabelHeight + mLabelsMargin) + getPaddingTop() + getPaddingBottom();
        }

        switch (mExpandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:
                height += mButtonSpacing * (mButtonsCount - 1) + getPaddingTop() + getPaddingBottom();
                height = adjustForOvershoot(height);
                break;
            case EXPAND_LEFT:
            case EXPAND_RIGHT:
                width += mButtonSpacing * (mButtonsCount - 1) + getPaddingLeft() + getPaddingRight();
                width = adjustForOvershoot(width);
                break;
        }


        if (getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
            width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        }

        if (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT) {
            height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        }

        setMeasuredDimension(width, height);
    }

    private int adjustForOvershoot(int dimension) {
        return dimension * 12 / 10;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mExpandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:
                boolean expandUp = mExpandDirection == EXPAND_UP;


                int addButtonY = expandUp ? b - t - mAddButton.getMeasuredHeight() - getPaddingBottom() : getPaddingTop();
                // Ensure mAddButton is centered on the line where the buttons should be
                int buttonsHorizontalCenter = mLabelsPosition == LABELS_ON_LEFT_SIDE
                        ? r - l - mMaxButtonWidth / 2 - getPaddingRight()
                        : mMaxButtonWidth / 2 + getPaddingLeft();
                int addButtonLeft = buttonsHorizontalCenter - mAddButton.getMeasuredWidth() / 2;
                mAddButton.layout(addButtonLeft, addButtonY, addButtonLeft + mAddButton.getMeasuredWidth(), addButtonY + mAddButton.getMeasuredHeight());


                int nextY = expandUp ?
                        addButtonY - mButtonSpacing :
                        addButtonY + mAddButton.getMeasuredHeight() + mButtonSpacing;

                for (int i = mButtonsCount - 1; i >= 0; i--) {
                    final View child = getChildAt(i);

                    if (child == mAddButton || child.getVisibility() == GONE) continue;

                    int childX = buttonsHorizontalCenter - child.getMeasuredWidth() / 2;
                    int childY = expandUp ? nextY - child.getMeasuredHeight() : nextY;
                    child.layout(childX, childY, childX + child.getMeasuredWidth(), childY + child.getMeasuredHeight());

                    float collapsedTranslation = addButtonY - childY;
                    float expandedTranslation = 0f;

                    child.setTranslationY(mExpanded ? expandedTranslation : collapsedTranslation);
//                    child.setVisibility(mExpanded ? VISIBLE : GONE);
//                    child.setAlpha(mExpanded ? 1f : 0f);

                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    params.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
                    params.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
                    params.setAnimationsTarget(child);

                    View label = (View) child.getTag(R.id.fab_label);
                    if (label != null) {
                        int labelsOffset = mMaxButtonWidth / 2 + mLabelsMargin;

                        int labelsXNearButton = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? buttonsHorizontalCenter - labelsOffset
                                : buttonsHorizontalCenter + labelsOffset;

                        int labelXAwayFromButton = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? labelsXNearButton - label.getMeasuredWidth()
                                : labelsXNearButton + label.getMeasuredWidth();

                        int labelLeft = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? labelXAwayFromButton
                                : labelsXNearButton;

                        int labelRight = mLabelsPosition == LABELS_ON_LEFT_SIDE
                                ? labelsXNearButton
                                : labelXAwayFromButton;

                        int labelTop = childY - mLabelsVerticalOffset + (child.getMeasuredHeight() - label.getMeasuredHeight()) / 2;

                        label.layout(labelLeft, labelTop, labelRight, labelTop + label.getMeasuredHeight());


                        label.setTranslationY(mExpanded ? expandedTranslation : collapsedTranslation);
//                        label.setVisibility(mExpanded ? VISIBLE : GONE);
//                        label.setAlpha(mExpanded ? 1f : 0f);

                        LayoutParams labelParams = (LayoutParams) label.getLayoutParams();
                        labelParams.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
                        labelParams.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
                        labelParams.setAnimationsTarget(label);
                        label.setVisibility(mExpanded ? VISIBLE : GONE);
                    }
                    child.setVisibility(mExpanded ? VISIBLE : GONE);

                    nextY = expandUp ?
                            childY - mButtonSpacing :
                            childY + child.getMeasuredHeight() + mButtonSpacing;
                }
                break;

            case EXPAND_LEFT:
            case EXPAND_RIGHT:
                boolean expandLeft = mExpandDirection == EXPAND_LEFT;

                int addButtonX = expandLeft ? r - l - mAddButton.getMeasuredWidth() - getPaddingRight() : getPaddingLeft();
                // Ensure mAddButton is centered on the line where the buttons should be
                int buttonsVerticalCenter = mLabelsPosition == LABELS_ON_UP_SIDE
                        ? b - t - mMaxButtonHeight / 2 - getPaddingBottom()
                        : mMaxButtonHeight / 2 + getPaddingTop();
                int addButtonTop = buttonsVerticalCenter - mAddButton.getMeasuredHeight() / 2;
                mAddButton.layout(addButtonX, addButtonTop, addButtonX + mAddButton.getMeasuredWidth(), addButtonTop + mAddButton.getMeasuredHeight());

                int nextX = expandLeft ?
                        addButtonX - mButtonSpacing :
                        addButtonX + mAddButton.getMeasuredWidth() + mButtonSpacing;

                for (int i = mButtonsCount - 1; i >= 0; i--) {
                    final View child = getChildAt(i);

                    if (child == mAddButton || child.getVisibility() == GONE) continue;

                    int childX = expandLeft ? nextX - child.getMeasuredWidth() : nextX;
                    int childY = buttonsVerticalCenter - child.getMeasuredHeight() / 2;
                    child.layout(childX, childY, childX + child.getMeasuredWidth(), childY + child.getMeasuredHeight());

                    float collapsedTranslation = addButtonX - childX;
                    float expandedTranslation = 0f;

                    child.setTranslationX(mExpanded ? expandedTranslation : collapsedTranslation);
//                    child.setVisibility(mExpanded ? VISIBLE : GONE);
//                    child.setAlpha(mExpanded ? 1f : 0f);

                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    params.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
                    params.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
                    params.setAnimationsTarget(child);

                    View label = (View) child.getTag(R.id.fab_label);
                    if (label != null) {
                        int labelsOffset = mMaxButtonHeight / 2 + mLabelsMargin;

                        int labelsYNearButton = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? buttonsVerticalCenter - labelsOffset
                                : buttonsVerticalCenter + labelsOffset;

                        int labelYAwayFromButton = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? labelsYNearButton - label.getMeasuredHeight()
                                : labelsYNearButton + label.getMeasuredHeight();

                        int labelTop = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? labelYAwayFromButton
                                : labelsYNearButton;

                        int labelBottom = mLabelsPosition == LABELS_ON_UP_SIDE
                                ? labelsYNearButton
                                : labelYAwayFromButton;

                        int labelLeft = childX - mLabelsVerticalOffset + (child.getMeasuredWidth() - label.getMeasuredWidth()) / 2;

                        label.layout(labelLeft, labelTop, labelLeft + label.getMeasuredWidth(), labelBottom);


                        label.setTranslationX(mExpanded ? expandedTranslation : collapsedTranslation);
//                        label.setVisibility(mExpanded ? VISIBLE : GONE);
//                        label.setAlpha(mExpanded ? 1f : 0f);

                        LayoutParams labelParams = (LayoutParams) label.getLayoutParams();
                        labelParams.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
                        labelParams.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
                        labelParams.setAnimationsTarget(label);
                        label.setVisibility(mExpanded ? VISIBLE : GONE);
                    }
                    child.setVisibility(mExpanded ? VISIBLE : GONE);

                    nextX = expandLeft ?
                            childX - mButtonSpacing :
                            childX + child.getMeasuredWidth() + mButtonSpacing;
                }

                break;
        }
    }

    @Override
    protected ViewGroup.MarginLayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public ViewGroup.MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(super.generateLayoutParams(attrs));
    }

    @Override
    protected ViewGroup.MarginLayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(super.generateLayoutParams(p));
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p);
    }

    private static Interpolator sExpandInterpolator = new OvershootInterpolator();
    private static Interpolator sCollapseInterpolator = new DecelerateInterpolator(3f);
    private static Interpolator sAlphaExpandInterpolator = new DecelerateInterpolator();

    private class LayoutParams extends ViewGroup.MarginLayoutParams {

        private ObjectAnimator mExpandDir = new ObjectAnimator();
        private ObjectAnimator mExpandAlpha = new ObjectAnimator();
        private ObjectAnimator mExpandScaleX = new ObjectAnimator();
        private ObjectAnimator mExpandScaleY = new ObjectAnimator();
        private ObjectAnimator mCollapseDir = new ObjectAnimator();
        private ObjectAnimator mCollapseAlpha = new ObjectAnimator();
        private ObjectAnimator mCollapseScaleX = new ObjectAnimator();
        private ObjectAnimator mCollapseScaleY = new ObjectAnimator();
        private boolean animationsSetToPlay;

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);

            mExpandDir.setInterpolator(sExpandInterpolator);
            mExpandAlpha.setInterpolator(sAlphaExpandInterpolator);
            mExpandScaleX.setInterpolator(sExpandInterpolator);
            mExpandScaleY.setInterpolator(sExpandInterpolator);
            mCollapseDir.setInterpolator(sCollapseInterpolator);
            mCollapseAlpha.setInterpolator(sCollapseInterpolator);
            mCollapseScaleX.setInterpolator(sExpandInterpolator);
            mCollapseScaleY.setInterpolator(sExpandInterpolator);

            mCollapseAlpha.setProperty(View.ALPHA);
            mCollapseAlpha.setFloatValues(1f, 0f);

            mCollapseScaleX.setProperty(View.SCALE_X);
            mCollapseScaleY.setProperty(View.SCALE_Y);
            mCollapseScaleX.setFloatValues(1f, 0f);
            mCollapseScaleY.setFloatValues(1f, 0f);

            mExpandAlpha.setProperty(View.ALPHA);
            mExpandAlpha.setFloatValues(0f, 1f);

            mExpandScaleX.setProperty(View.SCALE_X);
            mExpandScaleY.setProperty(View.SCALE_Y);
            mExpandScaleX.setFloatValues(0f, 1f);
            mExpandScaleY.setFloatValues(0f, 1f);

            switch (mExpandDirection) {
                case EXPAND_UP:
                case EXPAND_DOWN:
                    mCollapseDir.setProperty(View.TRANSLATION_Y);
                    mExpandDir.setProperty(View.TRANSLATION_Y);
                    break;
                case EXPAND_LEFT:
                case EXPAND_RIGHT:
                    mCollapseDir.setProperty(View.TRANSLATION_X);
                    mExpandDir.setProperty(View.TRANSLATION_X);
                    break;
            }
        }

        public void setAnimationsTarget(View view) {
            mCollapseAlpha.setTarget(view);
            mCollapseDir.setTarget(view);
            mCollapseScaleX.setTarget(view);
            mCollapseScaleY.setTarget(view);
            mExpandAlpha.setTarget(view);
            mExpandDir.setTarget(view);
            mExpandScaleX.setTarget(view);
            mExpandScaleY.setTarget(view);

            mCollapseAlpha.addListener(new MyCollapseAnimatorListener(view));
            mExpandAlpha.addListener(new MyExpandAnimatorListener(view));

            // Now that the animations have targets, set them to be played
            if (!animationsSetToPlay) {
                addLayerTypeListener(mExpandDir, view);
                addLayerTypeListener(mCollapseDir, view);

                mCollapseAnimation.play(mCollapseAlpha);
                mCollapseAnimation.play(mCollapseDir);
                mCollapseAnimation.play(mCollapseScaleX);
                mCollapseAnimation.play(mCollapseScaleY);
                mExpandAnimation.play(mExpandAlpha);
                mExpandAnimation.play(mExpandDir);
                mExpandAnimation.play(mExpandScaleX);
                mExpandAnimation.play(mExpandScaleY);
                animationsSetToPlay = true;
            }
        }

        private void addLayerTypeListener(Animator animator, final View view) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setLayerType(LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    view.setLayerType(LAYER_TYPE_HARDWARE, null);
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bringChildToFront(mAddButton);
        mButtonsCount = getChildCount();

        if (mLabelsStyle != 0) {
            createLabels();
        }
    }

    private void createLabels() {
        Context context = new ContextThemeWrapper(getContext(), mLabelsStyle);

        for (int i = 0; i < mButtonsCount; i++) {
            CustomFloatingActionButton button = (CustomFloatingActionButton) getChildAt(i);
            String title = button.getTitle();

            if (button == mAddButton || title == null || button.getTag(R.id.fab_label) != null)
                continue;

            TextView label = expandsHorizontally() ? new VerticalTextView(context) : new TextView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                label.setTextAppearance(mLabelsStyle);
            } else {
                label.setTextAppearance(getContext(), mLabelsStyle);
            }
            label.setText(button.getTitle());
            addView(label);

            button.setTag(R.id.fab_label, label);
        }
    }

    public void collapse() {
        collapse(false);
    }

    public void collapseImmediately() {
        collapse(true);
    }

    private void collapse(boolean immediately) {
        if (mExpanded) {
            mExpanded = false;
            setClickable(false);
            mCollapseAnimation.setDuration(immediately ? 0 : ANIMATION_DURATION);
            mCollapseAnimation.start();
            mExpandAnimation.cancel();

            if (mListener != null) {
                mListener.onMenuCollapsed();
            }
        }
    }

    public void toggle() {
        if (mCanHide && mAddButton.isRunning()) {
            return;
        }
        if (mExpanded) {
            collapse();
            /**
             * no hide lalala
             */
            if (mCanHide) {
                mAddButton.sendEmptyMessageDelayed();
            }
        } else {
            if (mCanHide) {
                mAddButton.removeMessage();
            }
            expand();
        }
    }

    public void expand() {
        if (!mExpanded) {
            mExpanded = true;
            setClickable(true);
            mCollapseAnimation.cancel();
            mExpandAnimation.start();

            if (mListener != null) {
                mListener.onMenuExpanded();
            }
        }
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mAddButton.setEnabled(enabled);
    }

    public boolean performAddButtonClick() {
        return mAddButton.performClick();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mExpanded = mExpanded;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            mExpanded = savedState.mExpanded;

            if (mRotatingDrawable != null) {
                mRotatingDrawable.setRotation(mExpanded ? EXPANDED_PLUS_ROTATION : COLLAPSED_PLUS_ROTATION);
            }

            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    class MyCollapseAnimatorListener implements Animator.AnimatorListener {

        private View mView;

        public MyCollapseAnimatorListener(View view) {
            mView = view;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mView.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mView.setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    class MyExpandAnimatorListener implements Animator.AnimatorListener {

        private View mView;

        public MyExpandAnimatorListener(View view) {
            mView = view;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mView.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mView.setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public static class SavedState extends BaseSavedState {
        public boolean mExpanded;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            mExpanded = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mExpanded ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
