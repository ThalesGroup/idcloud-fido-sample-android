package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.AddAuthenticatorRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.AddAuthenticatorResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleBiometricUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;


public class AddAuthenticator {

    private final FragmentActivity activity;

    public AddAuthenticator(FragmentActivity activity) {
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);

        UiCallbacks uiCallbacks = new UiCallbacks();
        SampleSecurePinUiCallback securePinUiCallback = new SampleSecurePinUiCallback(
                activity.getSupportFragmentManager(), activity.getString(R.string.usecase_addauthenticator)
        );
        uiCallbacks.securePinPadUiCallback = securePinUiCallback;
        uiCallbacks.biometricUiCallback = new SampleBiometricUiCallback();
        uiCallbacks.commonUiCallback = new SampleCommonUiCallback(
                activity.getSupportFragmentManager()
        );

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
        BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
            @Override
            public void onSuccess(IdCloudClient idCloudClient) {
                idCloudClient.createAddAuthenticatorRequest(uiCallbacks, addAuthenticatorRequestCallback).execute();
            }

            @Override
            public void onError(IdCloudClientException ignored) { }
        });
    }

}
