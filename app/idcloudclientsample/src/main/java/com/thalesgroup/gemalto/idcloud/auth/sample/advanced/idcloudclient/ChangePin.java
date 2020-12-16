package com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient;

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
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.ui.AuthenticatorsFragment;


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

    public void execute(AuthenticatorsFragment.OnExecuteFinishListener listener) {
        //Set changePin request callback
        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        ChangePinRequestCallback changePinRequestCallback = new ChangePinRequestCallback() {
            @Override
            public void onSuccess(ChangePinResponse changePinResponse) {
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
                    case RETRIEVING_REQUEST:
                    case VALIDATING_AUTHENTICATION:
                        Progress.showProgress(activity, code);
                        break;
                    case PROCESSING_REQUEST:
                    case END:
                        Progress.hideProgress();
                        break;
                }
            }
        };

        // Create Change pin request with callbacks
        new Thread(new Runnable() {
            @Override
            public void run() {
                idCloudClient.createChangePinRequest(
                        securePinPadUiCallback,
                        changePinRequestCallback
                ).execute();
            }
        }).start();
    }

}
