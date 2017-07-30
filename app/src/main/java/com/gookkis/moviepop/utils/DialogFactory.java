package com.gookkis.moviepop.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gookkis.moviepop.R;


public final class DialogFactory {

    public static Dialog createSimpleOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).setNeutralButton(context.getString(R.string.close), null);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogErrorStyle).setTitle(context.getString(R.string.generic_error_title)).setMessage(message).setNeutralButton(R.string.close, null);
        return alertDialog.create();
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context, @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

    public static ProgressBar DProgressBar(Context context) {

        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();
        ProgressBar mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        mProgressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout rl = new RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);
        layout.addView(rl, params);
        return mProgressBar;
    }

}
