package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.NotificationProfile;
import com.thales.dis.mobile.idcloud.auth.operation.RefreshPushTokenRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.RefreshPushTokenResponse;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.OnExecuteFinishListener;

public class RefreshPushToken {

    private FragmentActivity activity;
    private IdCloudClient idCloudClient;

    public RefreshPushToken(FragmentActivity activity, String url) {
        this.activity = activity;
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(OnExecuteFinishListener listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String pushToken = "";
                        if (task.isSuccessful()) {
                            pushToken = task.getResult();
                        }

                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(fragmentManager);
                        RefreshPushTokenRequestCallback refreshPushTokenRequestCallback = new RefreshPushTokenRequestCallback() {
                            @Override
                            public void onSuccess(@NonNull RefreshPushTokenResponse response) {
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
                            public void onProgress(@NonNull IdCloudProgress code) {
                                processOnProgress(code);
                            }
                        };

                        NotificationProfile notificationProfile = new NotificationProfile(pushToken);
                        idCloudClient.createRefreshPushTokenRequest(notificationProfile, refreshPushTokenRequestCallback).execute();
                    }
                });
            }
        }).start();
    }

    private void processOnProgress(IdCloudProgress code) {
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

}
