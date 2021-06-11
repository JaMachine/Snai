package com.owa.snai.store.app;

import android.content.Context;

import com.onesignal.OneSignal;

public class InitializeOneSignal {

    public void init(Context c) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(c);
        OneSignal.setAppId("02faf4a5-d4ec-4ec2-8465-47ebb7338ee5");
    }
}
