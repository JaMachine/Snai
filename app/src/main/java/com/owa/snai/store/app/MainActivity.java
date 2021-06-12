package com.owa.snai.store.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    boolean connected;
    public static String act = "checkinternet";
    private IntentFilter intentFilter;
    RelativeLayout internetStatus;


    int countingPeriodicState;
    boolean finishPeriodicCounting;
    ImageView splashImage;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    static String main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new InitializeOneSignal().init(this);
        hideUI();
        main = getResources().getString(R.string.icra);
        splashImage = findViewById(R.id.splash_screen);
        internetStatus = findViewById(R.id.internet_status);

        intentFilter = new IntentFilter();
        intentFilter.addAction(act);
        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);
        if (net(getApplicationContext()))
            startApp();
        else showConnectionMessage();

    }

    @Override
    public void onResume() {
        hideUI();
        registerReceiver(broadcastReceiver, intentFilter);
        if (net(getApplicationContext()))
            startApp();
        else showConnectionMessage();
        super.onResume();
    }

    private void hideUI() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View overlay = findViewById(R.id.loading_screen);
        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void initialize() {
        countingPeriodicState = 0;
        finishPeriodicCounting = false;
        final Handler handler = new Handler();
        final int d = 500;
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!finishPeriodicCounting) {
                    ++countingPeriodicState;
                    if (countingPeriodicState > 0) {
                        splashImage.setVisibility(View.VISIBLE);
                    }
                    if (countingPeriodicState >= 9) {
                        finishPeriodicCounting = true;
                        MainActivity.this.startActivity(new Intent(MainActivity.this, WebViewActivity.class));
                    }
                    handler.postDelayed(this, d);
                }
            }
        }, d);
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


    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(act)) {
                if (intent.getStringExtra("online_status").equals("true"))
                    startApp();
                else showConnectionMessage();
            }
        }
    };


    void showConnectionMessage() {
        internetStatus.setVisibility(View.VISIBLE);
        connected = false;
    }

    void startApp() {
        if (!connected) {
            internetStatus.setVisibility(View.GONE);
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

            initialize();
            connected = true;
        }
    }

    @Override
    protected void onRestart() {
        registerReceiver(broadcastReceiver, intentFilter);
        hideUI();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        hideUI();
        super.onPause();
    }

    public boolean net(Context context) {
        ConnectivityManager m = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = m.getActiveNetworkInfo();
        if (i != null && i.isConnectedOrConnecting()) {
            return true;
        } else {
            {
                {
                    return false;
                }
            }
        }
    }
}