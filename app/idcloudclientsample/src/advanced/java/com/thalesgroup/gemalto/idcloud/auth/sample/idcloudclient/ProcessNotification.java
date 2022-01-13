package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.operation.ProcessNotificationRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.ProcessNotificationResponse;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleBiometricUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.OnExecuteFinishListener;

import java.util.Map;

public class ProcessNotification {

    private FragmentActivity activity;
    private IdCloudClient idCloudClient;
    private Map<String, String> notification;

    public ProcessNotification(FragmentActivity activity, String url, Map<String, String> notification) {
        this.activity = activity;
        idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
        this.notification = notification;
    }

    public void execute(OnExecuteFinishListener listener) {

        Progress.showProgress(activity, IdCloudProgress.START);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        UiCallbacks uiCallbacks = new UiCallbacks();

        //  PASSCODE
        SampleSecurePinUiCallback securePinUiCallback = new SampleSecurePinUiCallback(
                fragmentManager, activity.getString(R.string.usecase_process_notification)
        );
        uiCallbacks.securePinPadUiCallback = securePinUiCallback;

        // BIOMETRIC
        uiCallbacks.biometricUiCallback = new SampleBiometricUiCallback();

        //  COMMON
        uiCallbacks.commonUiCallback = new SampleCommonUiCallback(
                fragmentManager
        );

        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(fragmentManager);
        ProcessNotificationRequestCallback processNotificationRequestCallback = new ProcessNotificationRequestCallback() {
            @Override
            public void onSuccess(@NonNull ProcessNotificationResponse response) {

                Progress.hideProgress();
                sampleResponseCallback.onSuccess();
                listener.onSuccess();
            }

            @Override
            public void onError(@NonNull IdCloudClientException exception) {

                Progress.hideProgress();
                sampleResponseCallback.onError();
                listener.onError(exception);

            }

            @Override
            public void onProgress(@NonNull IdCloudProgress code) {
                processOnProgress(code);
            }
        };

        idCloudClient.createProcessNotificationRequest(
                notification,
                uiCallbacks,
                processNotificationRequestCallback).execute();

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
