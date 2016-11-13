package com.wang.floatingactionbottondemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.floatingactionmenu.CustomFloatingActionButton;
import com.wang.floatingactionmenu.FloatingActionLineMenu2;
import com.wang.floatingactionmenu.RotatingDrawable;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionLabelClickListener;

/**
 * Created on 2016/10/27.
 * Author: wang
 */

public class LineMenuActivity extends AppCompatActivity implements View.OnClickListener, OnFloatingActionClickListener, OnFloatingActionLabelClickListener {

    FloatingActionButton mAddFab2;
    FloatingActionButton mAddFab3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_menu);
        findViewById(R.id.bottom_1_fab).setOnClickListener(this);
        findViewById(R.id.bottom_2_fab).setOnClickListener(this);
        findViewById(R.id.top_1_fab).setOnClickListener(this);
        findViewById(R.id.top_2_fab).setOnClickListener(this);

        mAddFab2 = (FloatingActionButton) findViewById(R.id.add_fab_2);
        mAddFab2.setImageDrawable(new RotatingDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_new)));

        mAddFab3 = (FloatingActionButton) findViewById(R.id.add_fab_3);
        mAddFab3.setImageDrawable(new RotatingDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_new)));

        new FloatingActionLineMenu2.Builder(this)
                .setExpandDirection(FloatingActionLineMenu2.EXPAND_RIGHT)
                .setLabelPosition(FloatingActionLineMenu2.LABELS_ON_DOWN_SIDE)
                .addSubFABView(this, R.drawable.ic_action_place, FloatingActionButton.SIZE_NORMAL)
                .addSubFABView(this, "喵帕斯", R.drawable.ic_action_picture)
                .addSubFABView(this, R.drawable.ic_action_headphones)
                .setLabelClickListener(this)
                .setFloatingActionClickListener(this)
                .attachTo(mAddFab2)
                .build();

        new FloatingActionLineMenu2.Builder(this)
                .setExpandDirection(FloatingActionLineMenu2.EXPAND_UP)
                .setLabelPosition(FloatingActionLineMenu2.LABELS_ON_RIGHT_SIDE)
                .addSubFABView(this, R.drawable.ic_action_place, FloatingActionButton.SIZE_NORMAL)
                .addSubFABView(this, "喵帕斯", R.drawable.ic_action_picture)
                .addSubFABView(this, R.drawable.ic_action_headphones)
                .setLabelClickListener(this)
                .setFloatingActionClickListener(this)
                .attachTo(mAddFab3)
                .build();
    }

    @Override
    public void onClick(View view) {
        String text = "";
        if (view instanceof TextView){
            switch ((int)view.getTag()){
                case R.id.bottom_1_fab:
                    text = "bottom 1 label click";
                    break;
                case R.id.bottom_2_fab:
                    text = "bottom 2 label click";
                    break;
                case R.id.top_1_fab:
                    text = "top 1 label click";
                    break;
                case R.id.top_2_fab:
                    text = "top 2 label click";
                    break;
            }
        }
        else {
            switch (view.getId()){
                case R.id.bottom_1_fab:
                    text = "bottom 1 fab click";
                    break;
                case R.id.bottom_2_fab:
                    text = "bottom 2 fab click";
                    break;
                case R.id.top_1_fab:
                    text = "top 1 fab click";
                    break;
                case R.id.top_2_fab:
                    text = "top 2 fab click";
                    break;
            }
        }

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubClick(int position) {
        Toast.makeText(this, "sub " + position + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMainClick(boolean open) {
        Log.d("CircleMenuActivity", "main " + (open ? "open" : "close") + " click");
//        Toast.makeText(this, "main " + (open ? "open" : "close") + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLabelClick(int position) {
        Toast.makeText(this, "label " + position + " click", Toast.LENGTH_SHORT).show();
    }
}
