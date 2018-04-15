package com.harbingerstudio.islamiclife.islamiclife;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.harbingerstudio.islamiclife.islamiclife.onesignal.MyNotificationOpenedHandler;
import com.harbingerstudio.islamiclife.islamiclife.onesignal.MyNotificationReceivedHandler;
import com.onesignal.OneSignal;

public class IslamicLifeApp extends Application {
    private static IslamicLifeApp mInstance;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        context = getApplicationContext();

        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler( new MyNotificationReceivedHandler() )
                .init();
        Fresco.initialize(this);

    }
    public static synchronized IslamicLifeApp getmInstance(){
        return mInstance;
    }

}
