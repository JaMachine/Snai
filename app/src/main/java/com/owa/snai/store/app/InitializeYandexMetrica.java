package com.owa.snai.store.app;

import android.app.Application;

import com.onesignal.OneSignal;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class InitializeYandexMetrica extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        YandexMetricaConfig c = YandexMetricaConfig.newConfigBuilder("ee30e1b7-5bd4-4d4b-a9e0-9a3e55ad29db").build();
        YandexMetrica.activate(getApplicationContext(), c);
        YandexMetrica.enableActivityAutoTracking(this);


    }

}
