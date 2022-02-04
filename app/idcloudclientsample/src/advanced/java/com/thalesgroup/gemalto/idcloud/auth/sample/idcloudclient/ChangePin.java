package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.ChangePinRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.ChangePinResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;


public class ChangePin  {

    private FragmentActivity activity;
    private SampleSecurePinUiCallback securePinPadUiCallback;
    private IdCloudClient idCloudClient;

    public ChangePin(FragmentActivity activity, String url, SampleSecurePinUiCallback securePinPadUiCallback) {
        this.activity = activity;
        this.securePinPadUiCallback = securePinPadUiCallback;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
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
        idCloudClient.createChangePinRequest(
                securePinPadUiCallback,
                changePinRequestCallback
        ).execute();
    }

}
