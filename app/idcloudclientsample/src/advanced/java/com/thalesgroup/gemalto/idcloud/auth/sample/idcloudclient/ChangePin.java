package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.ChangePinRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.ChangePinResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;


public class ChangePin  {

    private final FragmentActivity activity;

    public ChangePin(FragmentActivity activity) {
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        SampleSecurePinUiCallback securePinPadUiCallback = new SampleSecurePinUiCallback(
                activity.getSupportFragmentManager(), activity.getString(R.string.usecase_enrollment)
        );

        //Set changePin request callback
        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        ChangePinRequestCallback changePinRequestCallback = new ChangePinRequestCallback() {
            @Override
            public void onSuccess(ChangePinResponse changePinResponse) {
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

        // Create Change pin request with callbacks
        BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
            @Override
            public void onSuccess(IdCloudClient idCloudClient) {
                idCloudClient.createChangePinRequest(
                        securePinPadUiCallback,
                        changePinRequestCallback
                ).execute();
            }

            @Override
            public void onError(IdCloudClientException ignored) { }
        });
    }

}
