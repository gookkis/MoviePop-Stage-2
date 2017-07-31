package com.gookkis.moviepop;

import android.app.Application;
import android.content.Context;


public class BaseApps extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApps.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BaseApps.context;
    }
}
