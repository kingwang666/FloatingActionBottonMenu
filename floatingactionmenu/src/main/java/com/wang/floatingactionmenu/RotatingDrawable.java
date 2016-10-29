package com.wang.floatingactionmenu;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created on 2016/10/27.
 * Author: wang
 */

public class RotatingDrawable extends LayerDrawable {

    public RotatingDrawable(Drawable drawable) {
        super(new Drawable[]{drawable});
    }

    private float mRotation;

    @SuppressWarnings("UnusedDeclaration")
    public float getRotation() {
        return mRotation;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mRotation, getBounds().centerX(), getBounds().centerY());
        super.draw(canvas);
        canvas.restore();
    }
}
