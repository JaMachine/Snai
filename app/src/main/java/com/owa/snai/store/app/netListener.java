package com.owa.snai.store.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import static com.owa.snai.store.app.Main.act;

public class netListener extends Service {
    Handler h = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            h.postDelayed(r, 963);
            Intent intent = new Intent();
            intent.setAction(act);
            intent.putExtra("online_status", "" + net(netListener.this));
            sendBroadcast(intent);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        h.post(r);
        return START_STICKY;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
