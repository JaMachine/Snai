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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {
    boolean connected;
    public static String act = "checkinternet";
    private IntentFilter intentFilter;


    int countingPeriodicState;
    TextView conny;
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
        conny = findViewById(R.id.conny);

        intentFilter = new IntentFilter();
        intentFilter.addAction(act);
        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);
        if (net(getApplicationContext())) {
            if (!connected) {
                conny.setVisibility(View.GONE);
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
                            main = getSecret(main);
                        } else {
                            main = mFirebaseRemoteConfig.getString("icra");
                        }
                    }
                });

                initialize();
                connected = true;
            }
        } else {
            showConnectionMessage();
        }

    }

    @Override
    public void onResume() {
        hideUI();
        registerReceiver(broadcastReceiver, intentFilter);
        if (net(getApplicationContext())) {
            if (!connected) {
                conny.setVisibility(View.GONE);
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
                            main = getSecret(main);
                        } else {
                            main = mFirebaseRemoteConfig.getString("icra");
                        }
                    }
                });

                initialize();
                connected = true;
            }
        } else showConnectionMessage();
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


    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(act)) {
                if (intent.getStringExtra("online_status").equals("true"))
                    fireStarter();
                else showConnectionMessage();
            }
        }
    };

    private void fireStarter() {
        if (!connected) {
            conny.setVisibility(View.GONE);
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
                        main = getSecret(main);
                    } else {
                        main = mFirebaseRemoteConfig.getString("icra");
                    }
                }
            });

            initialize();
            connected = true;
        }
    }


    void showConnectionMessage() {
        conny.setVisibility(View.VISIBLE);
        connected = false;
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

    public static String getSecret(String str) {
        byte[] array = Base64.decode(str, Base64.DEFAULT);
        try {
            return new String(array, "UTF-8");
        } catch (Exception e) {
            return "";
        }
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