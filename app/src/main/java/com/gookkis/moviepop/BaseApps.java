package com.gookkis.moviepop;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;


public class BaseApps extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApps.context = getApplicationContext();
        Realm.init(this);
    }

    public static Context getAppContext() {
        return BaseApps.context;
    }
}
