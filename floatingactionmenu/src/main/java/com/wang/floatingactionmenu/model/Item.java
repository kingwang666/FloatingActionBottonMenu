package com.wang.floatingactionmenu.model;

import android.view.View;
import android.widget.TextView;

/**
 * Created on 2016/10/28.
 * Author: wang
 */
public class Item {
    public int x;
    public int y;
    public int width;
    public int height;

    public float alpha;

    public View view;

    public TextView label;

    public int labelX;

    public int labelY;

    public int labelWidth;

    public int labelHeight;

    public Item(View view, TextView label, int width, int height) {
        this.view = view;
        this.label = label;
        this.width = width;
        this.height = height;
        alpha = view.getAlpha();
        x = 0;
        y = 0;
        labelX = 0;
        labelY = 0;
    }
    public Item(View view, int width, int height) {
        this.view = view;
        this.width = width;
        this.height = height;
        alpha = view.getAlpha();
        x = 0;
        y = 0;
    }
}
