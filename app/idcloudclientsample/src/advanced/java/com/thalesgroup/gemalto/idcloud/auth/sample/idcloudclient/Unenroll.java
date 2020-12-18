package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.UnenrollRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.UnenrollResponse;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.SamplePersistence;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.AuthenticateHomeFragment;

public class Unenroll  {

    private FragmentActivity activity;
    private IdCloudClient idCloudClient;

    public Unenroll(FragmentActivity activity,String url) {
        this.activity = activity;
        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(AuthenticateHomeFragment.OnExecuteFinishListener listener) {

        Progress.showProgress(activity, IdCloudProgress.START);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
                UnenrollRequestCallback unenrollRequestCallback = new UnenrollRequestCallback() {
                    @Override
                    public void onSuccess(UnenrollResponse unenrollResponse) {
                        SamplePersistence.setIsEnrolled(activity, false);
                        sampleResponseCallback.onSuccess();
                        listener.onSuccess();
                        Progress.hideProgress();
                    }

                    @Override
                    public void onError(IdCloudClientException e) {
                        sampleResponseCallback.onError();
                        listener.onError(e);
                        Progress.hideProgress();
                    }

                    @Override
                    public void onProgress(final IdCloudProgress code) {
                        switch (code) {
                            case START:
                                Progress.showProgress(activity, code);
                                break;
                            case PROCESSING_REQUEST:
                            case END:
                                Progress.hideProgress();
                                break;
                        }
                    }
                };

                //Create unenroll request and execute request.
                idCloudClient.createUnenrollRequest(unenrollRequestCallback).execute();
            }
        }).start();
    }

}
