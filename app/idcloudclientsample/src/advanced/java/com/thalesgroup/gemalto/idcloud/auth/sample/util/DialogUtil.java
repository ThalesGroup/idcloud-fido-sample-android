package com.thalesgroup.gemalto.idcloud.auth.sample.util;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.EnrollActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.MainViewActivity;

public class DialogUtil {

    public static void showAlertDialog(FragmentActivity activity, final String title, final String message, DialogInterface.OnClickListener onClickListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, onClickListener);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }
}
