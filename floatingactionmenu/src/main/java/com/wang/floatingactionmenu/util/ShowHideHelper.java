package com.wang.floatingactionmenu.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;


/**
 * Created by wang
 * on 2016/12/15
 */

public class ShowHideHelper {

    private static final int HIDE_VIEW = 1;

    private AnimatorSet mShowAnim;

    private AnimatorSet mHideAnim;

    private WeakReference<View> mView;

    private boolean isShow;

    private int mDuration;

    private OnShowHideListener mOnShowHideListener;


    public ShowHideHelper(View view, int duration) {
        this(view, duration, true);
    }

    public ShowHideHelper(View view, int duration, boolean isShow) {
        mView = new WeakReference<>(view);
        this.mDuration = duration;
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void show() {
        show(3000);
    }

    public void showNoHide() {
        if (mShowAnim != null && mShowAnim.isRunning() || (mHideAnim != null && mHideAnim.isRunning())) {
            return;
        }
        final View view = mView.get();
        if (!isShow && view != null) {
            mShowAnim = new AnimatorSet();
            mShowAnim.play(ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0f));
            mShowAnim.setDuration(mDuration);
            mShowAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mOnShowHideListener != null) {
                        mOnShowHideListener.startShow();
                    }
                    View view1 = mView.get();
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    View view1 = mView.get();
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mShowAnim.start();
            isShow = true;
        }
    }

    public void show(int time) {
        if (time < mDuration) {
            throw new IllegalStateException("the show time < duration, are you kidding me?");
        }
        if (mShowAnim != null && mShowAnim.isRunning() || (mHideAnim != null && mHideAnim.isRunning())) {
            return;
        }
        final View view = mView.get();
        if (isShow) {
            mHandler.removeMessages(HIDE_VIEW);
            mHandler.sendEmptyMessageDelayed(HIDE_VIEW, time);
        } else if (view != null) {
            mShowAnim = new AnimatorSet();
            mShowAnim.play(ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0f));
            mShowAnim.setDuration(mDuration);
            mShowAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mOnShowHideListener != null) {
                        mOnShowHideListener.startShow();
                    }
                    View view1 = mView.get();
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    View view1 = mView.get();
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mShowAnim.start();
            mHandler.sendEmptyMessageDelayed(HIDE_VIEW, time);
            isShow = true;
        }
    }

    public void removeMessage() {
        mHandler.removeMessages(HIDE_VIEW);
    }

    public void sendEmptyMessageDelayed() {
        mHandler.sendEmptyMessageDelayed(HIDE_VIEW, 3000);
    }

    public void sendEmptyMessage() {
        mHandler.sendEmptyMessage(HIDE_VIEW);
    }

    public void hide() {
        if (mHideAnim != null && mHideAnim.isRunning() || (mShowAnim != null && mShowAnim.isRunning())) {
            return;
        }
        View view = mView.get();
        if (isShow && view != null) {
            mHandler.removeMessages(HIDE_VIEW);
            mHideAnim = new AnimatorSet();
            mHideAnim.play(ObjectAnimator.ofFloat(view, "translationY", 0f, view.getHeight()));
            mHideAnim.setDuration(mDuration);
            mHideAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mOnShowHideListener != null) {
                        mOnShowHideListener.startHide();
                    }
                    View view1 = mView.get();
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    View view1 = mView.get();
                    if (view1 != null) {
                        view1.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mHideAnim.start();
            isShow = false;
        }
    }

    public boolean isRunning() {
        return mHideAnim != null && mHideAnim.isRunning() || (mShowAnim != null && mShowAnim.isRunning());
    }

    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HIDE_VIEW:
                    hide();
                    break;
            }
        }
    };

    public OnShowHideListener getOnShowHideListener() {
        return mOnShowHideListener;
    }

    public void setOnShowHideListener(OnShowHideListener onShowHideListener) {
        mOnShowHideListener = onShowHideListener;
    }

    public interface OnShowHideListener {
        void startShow();

        void startHide();
    }
}
