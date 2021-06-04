package com.owa.snai.store.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    int timer;
    boolean stopTimer;
    ImageView splashImage;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    static String main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideUI();
        main = getResources().getString(R.string.icra);
        splashImage = findViewById(R.id.splash_screen);


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(2600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.paff);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (mFirebaseRemoteConfig.getString("icra").contains("icra")) {
                    main = dc(main);
                } else {
                    main = mFirebaseRemoteConfig.getString("icra");
                }
            }
        });


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
                    if (timer == 1) {
                        splashImage.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown)
                                .duration(999)
                                .playOn(splashImage);
                    }
                    if (timer == 5) {
                        YoYo.with(Techniques.FadeOutDown)
                                .duration(999)
                                .playOn(splashImage);
                    }
                    if (timer >= 6) {
                        stopTimer = true;
                        MainActivity.this.startActivity(new Intent(MainActivity.this, WebViewActivity.class));
                    }
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }


    public static String dc(String str) {
        String text = "";
        byte[] data = Base64.decode(str, Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }
}