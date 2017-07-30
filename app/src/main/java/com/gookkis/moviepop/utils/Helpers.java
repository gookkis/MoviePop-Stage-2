package com.gookkis.moviepop.utils;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by herikiswanto on 6/21/17.
 */

public class Helpers {
    public static int getWidthPoster(Activity activity) {

        int widthPoster = 0;

        Point size = new Point();
        WindowManager w = activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            widthPoster = size.x / 2;
        } else {
            Display d = w.getDefaultDisplay();
            widthPoster = d.getWidth() / 2;
        }
        return widthPoster;
    }
}
