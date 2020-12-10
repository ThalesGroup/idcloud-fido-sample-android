package com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient;

import androidx.fragment.app.FragmentActivity;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.UnenrollRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.UnenrollResponse;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui.AuthenticateHomeFragment;

public class Unenroll  {

    private FragmentActivity activity;
    private IdCloudClient idCloudClient;

    public Unenroll(FragmentActivity activity,String url) {
        this.activity = activity;
        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(AuthenticateHomeFragment.OnExecuteFinishListener listener) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Set unenroll request call back
                final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
                UnenrollRequestCallback unenrollRequestCallback = new UnenrollRequestCallback() {
                    @Override
                    public void onSuccess(UnenrollResponse unenrollResponse) {
                        sampleResponseCallback.onSuccess();
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(IdCloudClientException e) {
                        sampleResponseCallback.onError();
                        listener.onError(e);
                    }

                    @Override
                    public void onProgress(final IdCloudProgress code) {
                        switch (code) {
                            case RETRIEVING_REQUEST:
                            case VALIDATING_AUTHENTICATION:
                                if(Progress.progressDialog == null) {
                                    Progress.showProgressDialog(activity, code);
                                } else {
                                    Progress.updateProgressMessage(activity,code);
                                }
                                break;
                            case PROCESSING_REQUEST:
                            case END:
                                Progress.dismissProgress();
                                break;
                        }
                    }
                };

                //Create unenroll request and execute request.
                idCloudClient.createUnenrollRequest(unenrollRequestCallback).execute();
            }
        });
    }

}
