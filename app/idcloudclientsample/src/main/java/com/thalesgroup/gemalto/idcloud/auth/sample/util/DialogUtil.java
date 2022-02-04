package com.thalesgroup.gemalto.idcloud.auth.sample.util;

import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

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

    public static void showAlertDialogWithCancel(FragmentActivity activity, final String title, final String message, DialogInterface.OnClickListener onOkListener, DialogInterface.OnClickListener onCancelListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, onOkListener)
                        .setNegativeButton(android.R.string.cancel, onCancelListener);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    public static void showToastMessage(FragmentActivity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
