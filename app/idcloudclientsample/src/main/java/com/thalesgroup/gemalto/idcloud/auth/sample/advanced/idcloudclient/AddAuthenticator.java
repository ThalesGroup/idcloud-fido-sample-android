package com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.AddAuthenticatorRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.AddAuthenticatorResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.ui.AuthenticatorsFragment;


public class AddAuthenticator  {

    private FragmentActivity activity;
    private UiCallbacks uiCallbacks;
    private IdCloudClient idCloudClient;

    public AddAuthenticator(FragmentActivity activity, String url, UiCallbacks uiCallbacks) {
        this.activity = activity;
        this.uiCallbacks = uiCallbacks;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(AuthenticatorsFragment.OnExecuteFinishListener listener) {
        //create add Authenticator Request Callback
        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        AddAuthenticatorRequestCallback addAuthenticatorRequestCallback = new AddAuthenticatorRequestCallback() {
            @Override
            public void onSuccess(@NonNull AddAuthenticatorResponse response) {
                sampleResponseCallback.onSuccess();
                listener.onSuccess();
                Progress.hideProgress();
            }

            @Override
            public void onError(@NonNull IdCloudClientException exception) {
                sampleResponseCallback.onError();
                listener.onError(exception);
                Progress.hideProgress();
            }

            @Override
            public void onProgress(IdCloudProgress code) {
                switch (code) {
                    case START:
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

        // Create Add authenticator request with callbacks and execute request
        new Thread(new Runnable() {
            @Override
            public void run() {
                idCloudClient.createAddAuthenticatorRequest(uiCallbacks, addAuthenticatorRequestCallback).execute();
            }
        }).start();

    }

}
