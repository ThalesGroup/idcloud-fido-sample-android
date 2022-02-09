package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.RemoveAuthenticatorRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.RemoveAuthenticatorResponse;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;


public class RemoveAuthenticator  {

    private final Authenticator authenticator;
    private final FragmentActivity activity;

    public RemoveAuthenticator(FragmentActivity activity, Authenticator authenticator) {
        this.authenticator = authenticator;
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        //Set remove Authenticator Request Callback
        RemoveAuthenticatorRequestCallback removeAuthenticatorRequestCallback = new RemoveAuthenticatorRequestCallback() {
            @Override
            public void onSuccess(@NonNull RemoveAuthenticatorResponse response) {
                listener.onSuccess(null);
            }

            @Override
            public void onError(@NonNull IdCloudClientException exception) {
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

        // Create Remove authenticator request with callbacks and execute request
        BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
            @Override
            public void onSuccess(IdCloudClient idCloudClient) {
                idCloudClient.createRemoveAuthenticatorRequest(authenticator.getType(), removeAuthenticatorRequestCallback).execute();
            }

            @Override
            public void onError(IdCloudClientException ignored) { }
        });
    }
}
