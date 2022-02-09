package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.UnenrollRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.UnenrollResponse;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.SamplePersistence;

public class Unenroll  {

    private final FragmentActivity activity;

    public Unenroll(FragmentActivity activity) {
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        UnenrollRequestCallback unenrollRequestCallback = new UnenrollRequestCallback() {
            @Override
            public void onSuccess(UnenrollResponse unenrollResponse) {
                SamplePersistence.setIsEnrolled(activity, false);
                sampleResponseCallback.onSuccess();
                listener.onSuccess(null);
            }

            @Override
            public void onError(IdCloudClientException e) {
                sampleResponseCallback.onError();
                listener.onError(e);
            }

            @Override
            public void onProgress(final IdCloudProgress code) {
                if (code == IdCloudProgress.END) {
                    Progress.hideProgress();
                } else {
                    Progress.showProgress(activity, code);
                }
            }
        };

        //Create unenroll request and execute request.
        BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
            @Override
            public void onSuccess(IdCloudClient idCloudClient) {
                idCloudClient.createUnenrollRequest(unenrollRequestCallback).execute();
            }

            @Override
            public void onError(IdCloudClientException ignored) { }
        });
    }

}
