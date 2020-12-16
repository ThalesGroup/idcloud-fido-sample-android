package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;

public class Progress {
    private static AlertDialog alertDialog;

    public static void showProgress(Activity activity, IdCloudProgress progress) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = getStringForProgress(activity, progress);
                if (alertDialog != null) {
                    ((TextView) alertDialog.findViewById(R.id.progress_text)).setText(text);
                } else {
                    View view = activity.getLayoutInflater().inflate(R.layout.dialog_progress, null);
                    ((TextView) view.findViewById(R.id.progress_text)).setText(text);

                    alertDialog = new AlertDialog.Builder(activity)
                            .setView(view)
                            .create();
                }
                alertDialog.show();
            }
        });
    }

    public static void hideProgress() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    private static String getStringForProgress(Context context, IdCloudProgress progressCode) {
        String progressStr;
        switch (progressCode) {
            case START:
                progressStr = context.getString(R.string.progress_start);
                break;
            case RETRIEVING_REQUEST:
                progressStr = context.getString(R.string.progress_retrieving_request);
                break;
            case PROCESSING_REQUEST:
                progressStr = context.getString(R.string.progress_processing_request);
                break;
            case VALIDATING_AUTHENTICATION:
                progressStr = context.getString(R.string.progress_validating_authentication);
                break;
            case END:
                progressStr = context.getString(R.string.progress_end);
                break;
            default:
                progressStr = "unsupported progress";
                break;
        }
        return progressStr;

    }
}