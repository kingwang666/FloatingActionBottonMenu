package com.wang.floatingactionmenu.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.wang.floatingactionmenu.FloatingActionCircleMenu;
import com.wang.floatingactionmenu.model.Item;


/**
 * An example animation handler
 * Animates translation, rotation, scale and alpha at the same time using Property Animation APIs.
 */
public class DefaultAnimationHandler extends MenuAnimationHandler {

    /** duration of animations, in milliseconds */
    protected static final int DURATION = 300;
    /** duration to wait between each of  */
    protected static final int LAG_BETWEEN_ITEMS = 20;
    /** holds the current state of animation */
    private boolean animating;

    public DefaultAnimationHandler() {
        setAnimating(false);
    }

    @Override
    public void animateMenuOpening(Point center) {
        super.animateMenuOpening(center);

        setAnimating(true);

        Animator lastAnimation = null;
        for (int i = 0; i < menu.getSubActionItems().size(); i++) {

            menu.getSubActionItems().get(i).view.setScaleX(0);
            menu.getSubActionItems().get(i).view.setScaleY(0);
            menu.getSubActionItems().get(i).view.setAlpha(0);

            if (menu.getSubActionItems().get(i).label != null) {
                PropertyValuesHolder labelX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, menu.getSubActionItems().get(i).labelX - center.x + menu.getSubActionItems().get(i).labelWidth / 2);
                PropertyValuesHolder labelY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, menu.getSubActionItems().get(i).labelY - center.y + menu.getSubActionItems().get(i).labelHeight / 2);
//            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 720);
                PropertyValuesHolder labelsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
                PropertyValuesHolder labelsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
                PropertyValuesHolder labelA = PropertyValuesHolder.ofFloat(View.ALPHA, 1);

                final ObjectAnimator animationLabel = ObjectAnimator.ofPropertyValuesHolder(menu.getSubActionItems().get(i).label, labelX, labelY, /*pvhR,*/ labelsX, labelsY, labelA);
                animationLabel.setDuration(DURATION);
                animationLabel.setInterpolator(new OvershootInterpolator(0.9f));
                animationLabel.start();
            }

            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, menu.getSubActionItems().get(i).x - center.x + menu.getSubActionItems().get(i).width / 2);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, menu.getSubActionItems().get(i).y - center.y + menu.getSubActionItems().get(i).height / 2);
//            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 720);
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 1);

            final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(menu.getSubActionItems().get(i).view, pvhX, pvhY, /*pvhR,*/ pvhsX, pvhsY, pvhA);
            animation.setDuration(DURATION);
            animation.setInterpolator(new OvershootInterpolator(0.9f));
            animation.addListener(new SubActionItemAnimationListener(menu.getSubActionItems().get(i), ActionType.OPENING));

            if(i == 0) {
                lastAnimation = animation;
            }

            // Put a slight lag between each of the menu items to make it asymmetric
            animation.setStartDelay((menu.getSubActionItems().size() - i) * LAG_BETWEEN_ITEMS);
            animation.start();
        }
        View mainView = menu.getMainActionView();
        if (mainView instanceof ImageView){
            OvershootInterpolator interpolator = new OvershootInterpolator(0.9f);
            ObjectAnimator openAnimator = ObjectAnimator.ofFloat(((ImageView) mainView).getDrawable(), "rotation", 0, 135);
            openAnimator.setInterpolator(interpolator);
            openAnimator.setDuration(DURATION);
            openAnimator.start();
        }
        if(lastAnimation != null) {
            lastAnimation.addListener(new LastAnimationListener());
        }

    }

    @Override
    public void animateMenuClosing(Point center) {
        super.animateMenuOpening(center);

        setAnimating(true);

        Animator lastAnimation = null;
        for (int i = 0; i < menu.getSubActionItems().size(); i++) {

            if (menu.getSubActionItems().get(i).label != null){
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, - (menu.getSubActionItems().get(i).labelX - center.x + menu.getSubActionItems().get(i).labelWidth / 2));
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, - (menu.getSubActionItems().get(i).labelY - center.y + menu.getSubActionItems().get(i).labelHeight / 2));
//            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, -720);
                PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
                PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
                PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

                final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(menu.getSubActionItems().get(i).label, pvhX, pvhY, /*pvhR,*/ pvhsX, pvhsY, pvhA);
                animation.setDuration(DURATION);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            }

            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, - (menu.getSubActionItems().get(i).x - center.x + menu.getSubActionItems().get(i).width / 2));
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, - (menu.getSubActionItems().get(i).y - center.y + menu.getSubActionItems().get(i).height / 2));
//            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, -720);
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

            final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(menu.getSubActionItems().get(i).view, pvhX, pvhY, /*pvhR,*/ pvhsX, pvhsY, pvhA);
            animation.setDuration(DURATION);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.addListener(new SubActionItemAnimationListener(menu.getSubActionItems().get(i), ActionType.CLOSING));

            if(i == 0) {
                lastAnimation = animation;
            }

            animation.setStartDelay((menu.getSubActionItems().size() - i) * LAG_BETWEEN_ITEMS);
            animation.start();
        }
        View mainView = menu.getMainActionView();
        if (mainView instanceof ImageView){
            OvershootInterpolator interpolator = new OvershootInterpolator(0.9f);
            ObjectAnimator closeAnimator = ObjectAnimator.ofFloat(((ImageView) mainView).getDrawable(), "rotation", 135, 0);
            closeAnimator.setInterpolator(interpolator);
            closeAnimator.setDuration(DURATION);
            closeAnimator.start();
        }
        if(lastAnimation != null) {
            lastAnimation.addListener(new LastAnimationListener());
        }
    }

    @Override
    public boolean isAnimating() {
        return animating;
    }

    @Override
    protected void setAnimating(boolean animating) {
        this.animating = animating;
    }

    protected class SubActionItemAnimationListener implements Animator.AnimatorListener {

        private Item subActionItem;
        private ActionType actionType;

        public SubActionItemAnimationListener(Item subActionItem, ActionType actionType) {
            this.subActionItem = subActionItem;
            this.actionType = actionType;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            restoreSubActionViewAfterAnimation(subActionItem, actionType);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            restoreSubActionViewAfterAnimation(subActionItem, actionType);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {}
    }
}
