package com.owa.snai.store.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    int timer;
    boolean stopTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideUI();
        loadingProcess();
    }

    @Override
    public void onResume() {
        hideUI();
        loadingProcess();
        super.onResume();
    }

    private void hideUI() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View overlay = findViewById(R.id.loading_screen);
        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void loadingProcess() {
        timer = 0;
        stopTimer = false;
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!stopTimer) {
                    timer++;
                    if (timer >= 5) {
                        stopTimer = true;
                        MainActivity.this.startActivity(new Intent(MainActivity.this, WebViewActivity.class));
                    }
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }
}