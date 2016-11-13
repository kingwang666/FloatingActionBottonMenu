package com.wang.floatingactionmenu.util;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;

/**
 * Created on 2016/11/13.
 * Author: wang
 */

public class FabSizeUtil {

    public static int getSizeDimension(Context context, @FloatingActionButton.Size int size) {
        if (size == FloatingActionButton.SIZE_MINI) {
            return context.getResources().getDimensionPixelSize(android.support.design.R.dimen.design_fab_size_mini);
        }
        return context.getResources().getDimensionPixelSize(android.support.design.R.dimen.design_fab_size_normal);
    }
}
