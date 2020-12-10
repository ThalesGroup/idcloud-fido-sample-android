package com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.RemoveAuthenticatorRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.RemoveAuthenticatorResponse;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.ui.AuthenticatorsFragment;


public class RemoveAuthenticator  {

    private Authenticator authenticator;
    private IdCloudClient idCloudClient;
    private Activity activity;

    public RemoveAuthenticator(FragmentActivity activity, String url, Authenticator authenticator) {
        this.authenticator = authenticator;
        this.activity = activity;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(AuthenticatorsFragment.OnExecuteFinishListener listener) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Set remove Authenticator Request Callback
                RemoveAuthenticatorRequestCallback removeAuthenticatorRequestCallback = new RemoveAuthenticatorRequestCallback() {
                    @Override
                    public void onSuccess(@NonNull RemoveAuthenticatorResponse response) {
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(@NonNull IdCloudClientException exception) {
                        listener.onError(exception);
                    }
                };
                // Create Remove authenticator request with callbacks and execute request
                idCloudClient.createRemoveAuthenticatorRequest(authenticator.getType(), removeAuthenticatorRequestCallback).execute();
            }
        });
    }
}
