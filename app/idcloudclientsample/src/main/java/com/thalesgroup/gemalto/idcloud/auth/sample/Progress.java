package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;

public class Progress {

    public static ProgressDialog progressDialog = null;


    public static ProgressDialog showProgressDialog(Activity activity, IdCloudProgress progressCode) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        progressDialog.setContentView(R.layout.progressbar);
        ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        progressDialog.setCancelable(false);
        progressDialog.setMessage(getStringForProgress(activity, progressCode));

        progressDialog.show();
        return progressDialog;
    }

    public static void updateProgressMessage(Activity activity, IdCloudProgress progressCode) {
        progressDialog.setMessage(getStringForProgress(activity,progressCode));
    }

    public static String getStringForProgress(Activity activity, IdCloudProgress progressCode) {

        String progressStr;
        switch (progressCode) {
            case START:
                progressStr = activity.getString(R.string.progress_start);
                break;
            case RETRIEVING_REQUEST:
                progressStr = activity.getString(R.string.progress_retrieving_request);
                break;
            case PROCESSING_REQUEST:
                progressStr = activity.getString(R.string.progress_processing_request);
                break;
            case VALIDATING_AUTHENTICATION:
                progressStr = activity.getString(R.string.progress_validating_authentication);
                break;
            case END:
                progressStr = activity.getString(R.string.progress_end);
                break;
            default:
                progressStr = "unsupported progress";
                break;
        }
        return progressStr;

    }

    public static void dismissProgress() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}