package com.wang.floatingactionbottondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class SystemOverlayMenuActivity extends AppCompatActivity {

    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_with_overlay);

        final Button button = (Button) findViewById(R.id.startOverlayServiceButton);
        final Intent is = new Intent(SystemOverlayMenuActivity.this, SystemOverlayMenuService.class);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start an unbound service.
                if (isStart) {
                    button.setText("start service");
                    stopService(is);
                    isStart = false;
                } else {
                    button.setText("stop service");
                    startService(is);
                    isStart = true;
                }
                // We need to be able to stop it later though.
                // This is currently done by the red button of the topCenterMenu in SystemOverlayMenuService
            }
        });
    }


}
