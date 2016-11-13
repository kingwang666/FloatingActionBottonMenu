package com.wang.floatingactionbottondemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.line_btn).setOnClickListener(this);
        findViewById(R.id.circle_btn).setOnClickListener(this);
        findViewById(R.id.system_overlay_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.line_btn:
                Intent intent = new Intent(MainActivity.this, LineMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.circle_btn:
                Intent intent1 = new Intent(MainActivity.this, CircleMenuActivity.class);
                startActivity(intent1);
                break;
            case R.id.system_overlay_btn:
                Intent intent2 = new Intent(MainActivity.this, SystemOverlayMenuActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
