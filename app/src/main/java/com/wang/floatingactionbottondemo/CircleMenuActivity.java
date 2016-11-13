package com.wang.floatingactionbottondemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.floatingactionmenu.FloatingActionCircleMenu;
import com.wang.floatingactionmenu.FloatingActionLineMenu2;
import com.wang.floatingactionmenu.FloatingActionMenu;
import com.wang.floatingactionmenu.RotatingDrawable;
import com.wang.floatingactionmenu.SubActionButton;
import com.wang.floatingactionmenu.interfaces.MenuStateChangeListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionClickListener;
import com.wang.floatingactionmenu.interfaces.OnFloatingActionLabelClickListener;


public class CircleMenuActivity extends AppCompatActivity implements OnFloatingActionClickListener, OnFloatingActionLabelClickListener {

    FloatingActionButton mAddFab;

    FloatingActionButton mStarFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_menu);

        mAddFab = (FloatingActionButton) findViewById(R.id.add_fab);
        mStarFab = (FloatingActionButton) findViewById(R.id.star_fab);
        mAddFab.setImageDrawable(new RotatingDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_new)));


        FloatingActionButton fab1 = new FloatingActionButton(this);
        fab1.setImageResource(R.drawable.ic_action_chat);
        fab1.setBackgroundTintList(mAddFab.getBackgroundTintList());


//         Build the menu with default options: light theme, 90 degrees, 72dp radius.
//         Set 4 default SubActionButtons
        FloatingActionCircleMenu rightLowerMenu = new FloatingActionCircleMenu.Builder(this)
                .addSubActionView(fab1)
                .addSubFABView(this, R.drawable.ic_action_camera)
                .addSubFABView(this, ContextCompat.getColorStateList(this, R.color.blue500), R.drawable.ic_action_video)
                .addSubActionView(R.layout.item_email_fab, this)
                .setFloatingActionClickListener(this)
                .attachTo(mAddFab)
                .build();

//        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                Log.d("CircleMenuActivity", "opened");
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                Log.d("CircleMenuActivity", "closed");
            }
        });

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        TextView textView = new TextView(this);
        textView.setText("kingwang");
        new FloatingActionCircleMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(textView).build())
                .addSubFABView(this, R.drawable.ic_action_place, FloatingActionButton.SIZE_NORMAL)
                .addSubFABView(this, ContextCompat.getColorStateList(this, R.color.bg_text_color_white_primary), R.drawable.ic_action_location_found, FloatingActionButton.SIZE_NORMAL)
                .addSubFABView(this, R.drawable.ic_action_picture)
                .addSubFABView(this, R.drawable.ic_action_headphones)
                .setFloatingActionClickListener(this)
                .setRadius(getResources().getDimensionPixelSize(R.dimen.test_radius))
                .setStartAngle(70)
                .setEndAngle(-70)
                .attachTo(mStarFab)
                .build();

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
