package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

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


public class AddAuthenticator {

    private FragmentActivity activity;
    private UiCallbacks uiCallbacks;
    private IdCloudClient idCloudClient;

    public AddAuthenticator(FragmentActivity activity, String url, UiCallbacks uiCallbacks) {
        this.activity = activity;
        this.uiCallbacks = uiCallbacks;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        //create add Authenticator Request Callback
        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        AddAuthenticatorRequestCallback addAuthenticatorRequestCallback = new AddAuthenticatorRequestCallback() {
            @Override
            public void onSuccess(@NonNull AddAuthenticatorResponse response) {
                sampleResponseCallback.onSuccess();
                listener.onSuccess(null);
            }

            @Override
            public void onError(@NonNull IdCloudClientException exception) {
                sampleResponseCallback.onError();
                listener.onError(exception);
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
        // Create Add authenticator request with callbacks and execute request
        idCloudClient.createAddAuthenticatorRequest(uiCallbacks, addAuthenticatorRequestCallback).execute();
    }

}
