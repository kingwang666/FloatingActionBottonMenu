package com.wang.floatingactionmenu.animation;

import android.animation.Animator;
import android.graphics.Point;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.wang.floatingactionmenu.FloatingActionMenu;
import com.wang.floatingactionmenu.model.Item;


/**
 * An abstract class that is a prototype for the actual animation handlers
 */
public abstract class MenuAnimationHandler {

    // There are only two distinct animations at the moment.
    protected enum ActionType {OPENING, CLOSING}

    protected FloatingActionMenu menu;

    public MenuAnimationHandler() {
    }

    public void setMenu(FloatingActionMenu menu) {
        this.menu = menu;
    }

    /**
     * Starts the opening animation
     * Should be overriden by children
     * @param center
     */
    public void animateMenuOpening(Point center) {
        if(menu == null) {
            throw new NullPointerException("MenuAnimationHandler cannot animate without a valid FloatingActionCircleMenu.");
        }

    }

    /**
     * Ends the opening animation
     * Should be overriden by children
     * @param center
     */
    public void animateMenuClosing(Point center) {
        if(menu == null) {
            throw new NullPointerException("MenuAnimationHandler cannot animate without a valid FloatingActionCircleMenu.");
        }
    }

    /**
     * Restores the specified sub action view to its final state, according to the current actionType
     * Should be called after an animation finishes.
     * @param subActionItem
     * @param actionType
     */
    protected void restoreSubActionViewAfterAnimation(Item subActionItem, ActionType actionType) {
        ViewGroup.LayoutParams params = subActionItem.view.getLayoutParams();
        subActionItem.view.setTranslationX(0);
        subActionItem.view.setTranslationY(0);
        subActionItem.view.setRotation(0);
        subActionItem.view.setScaleX(1);
        subActionItem.view.setScaleY(1);
        subActionItem.view.setAlpha(1);

        if(actionType == ActionType.OPENING) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            if(menu.isSystemOverlay()) {
                WindowManager.LayoutParams overlayParams = (WindowManager.LayoutParams) menu.getOverlayContainer().getLayoutParams();
                lp.setMargins(subActionItem.x - overlayParams.x, subActionItem.y - overlayParams.y, 0, 0);
            }
            else {
                lp.setMargins(subActionItem.x, subActionItem.y, 0, 0);
            }
            subActionItem.view.setLayoutParams(lp);
            if (subActionItem.label != null){
                subActionItem.label.setTranslationX(0);
                subActionItem.label.setTranslationY(0);
                subActionItem.label.setRotation(0);
                subActionItem.label.setScaleX(1);
                subActionItem.label.setScaleY(1);
                subActionItem.label.setAlpha(1);
                FrameLayout.LayoutParams labelLp = (FrameLayout.LayoutParams) subActionItem.label.getLayoutParams();
                if(menu.isSystemOverlay()) {
                    WindowManager.LayoutParams overlayParams = (WindowManager.LayoutParams) menu.getOverlayContainer().getLayoutParams();
                    labelLp.setMargins(subActionItem.labelX - overlayParams.x, subActionItem.labelY - overlayParams.y, 0, 0);
                }
                else {
                    labelLp.setMargins(subActionItem.labelX, subActionItem.labelY, 0, 0);
                }
                subActionItem.label.setLayoutParams(labelLp);
//                subActionItem.label.requestLayout();
            }
        }
        else if(actionType == ActionType.CLOSING) {
            Point center = menu.getActionViewCenter();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            if(menu.isSystemOverlay()) {
                WindowManager.LayoutParams overlayParams = (WindowManager.LayoutParams) menu.getOverlayContainer().getLayoutParams();
                lp.setMargins(center.x - overlayParams.x - subActionItem.width / 2, center.y - overlayParams.y - subActionItem.height / 2, 0, 0);
            }
            else {
                lp.setMargins(center.x - subActionItem.width / 2, center.y - subActionItem.height / 2, 0, 0);
            }
            subActionItem.view.setLayoutParams(lp);
            menu.removeViewFromCurrentContainer(subActionItem.view);

            if (subActionItem.label != null){
                FrameLayout.LayoutParams labelLp = (FrameLayout.LayoutParams) subActionItem.label.getLayoutParams();
                if(menu.isSystemOverlay()) {
                    WindowManager.LayoutParams overlayParams = (WindowManager.LayoutParams) menu.getOverlayContainer().getLayoutParams();
                    labelLp.setMargins(center.x - overlayParams.x - subActionItem.labelWidth / 2, center.y - overlayParams.y - subActionItem.labelHeight / 2, 0, 0);
                }
                else {
                    labelLp.setMargins(center.x - subActionItem.labelWidth / 2, center.y - subActionItem.labelHeight / 2, 0, 0);
                }
                subActionItem.label.setLayoutParams(labelLp);
                menu.removeViewFromCurrentContainer(subActionItem.label);
            }

            if(menu.isSystemOverlay()) {
                // When all the views are removed from the overlay container,
                // we also need to detach it
                if (menu.getOverlayContainer().getChildCount() == 0) {
                    menu.detachOverlayContainer();
                }
            }
        }
    }

    /**
     * A special animation listener that is intended to listen the last of the sequential animations.
     * Changes the animating property of children.
     */
    public class LastAnimationListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            setAnimating(true);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setAnimating(false);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            setAnimating(false);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            setAnimating(true);
        }
    }

    public abstract boolean isAnimating();
    protected abstract void setAnimating(boolean animating);
}
