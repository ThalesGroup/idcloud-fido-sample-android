package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.NotificationProfile;
import com.thales.dis.mobile.idcloud.auth.operation.RefreshPushTokenRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.RefreshPushTokenResponse;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;

public class RefreshPushToken {

    private final FragmentActivity activity;

    public RefreshPushToken(FragmentActivity activity) {
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String pushToken = null;
                if (task.isSuccessful()) {
                    pushToken = task.getResult();
                } else {
                    Toast.makeText(activity, R.string.text_push_token_failure, Toast.LENGTH_SHORT).show();
                }

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(fragmentManager);
                RefreshPushTokenRequestCallback refreshPushTokenRequestCallback = new RefreshPushTokenRequestCallback() {
                    @Override
                    public void onSuccess(@NonNull RefreshPushTokenResponse response) {
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

                NotificationProfile notificationProfile = new NotificationProfile(pushToken);

                BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
                    @Override
                    public void onSuccess(IdCloudClient idCloudClient) {
                        idCloudClient.createRefreshPushTokenRequest(notificationProfile, refreshPushTokenRequestCallback).execute();
                    }

                    @Override
                    public void onError(IdCloudClientException ignored) { }
                });
            }
        });
    }

}
