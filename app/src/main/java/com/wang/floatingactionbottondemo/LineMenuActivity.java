package com.wang.floatingactionbottondemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.floatingactionmenu.CustomFloatingActionButton;

/**
 * Created on 2016/10/27.
 * Author: wang
 */

public class LineMenuActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_menu);
        findViewById(R.id.bottom_1_fab).setOnClickListener(this);
        findViewById(R.id.bottom_2_fab).setOnClickListener(this);
        findViewById(R.id.top_1_fab).setOnClickListener(this);
        findViewById(R.id.top_2_fab).setOnClickListener(this);
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
}
