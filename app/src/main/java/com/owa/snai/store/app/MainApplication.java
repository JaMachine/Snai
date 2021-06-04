package com.owa.snai.store.app;

import android.app.Application;

import com.onesignal.OneSignal;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class MainApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "02faf4a5-d4ec-4ec2-8465-47ebb7338ee5";

    @Override
    public void onCreate() {
        super.onCreate();

        yandexMetric();
        startOneSignal();
    }

    void yandexMetric() {
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("ee30e1b7-5bd4-4d4b-a9e0-9a3e55ad29db").build();
        YandexMetrica.activate(getApplicationContext(), config);
        YandexMetrica.enableActivityAutoTracking(this);
    }

    void startOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
