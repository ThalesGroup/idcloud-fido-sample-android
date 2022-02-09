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
import com.thales.dis.mobile.idcloud.auth.operation.EnrollRequest;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollResponse;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollmentToken;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollmentTokenFactory;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleBiometricUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.SamplePersistence;

public class EnrollWithPush extends Enroll {

    private final boolean enablePushNotification;

    public EnrollWithPush(FragmentActivity activity, String code, boolean enablePushNotification) {
        super(activity, code);
        this.enablePushNotification = enablePushNotification;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        if (!enablePushNotification) {
            super.execute(listener);
        } else {
            Progress.showProgress(activity, IdCloudProgress.START);
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

                @Override
                public void onComplete(@NonNull Task<String> task) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    // Set up an instance of UiCallbacks, an encapsulated class containing all necessary UI callbacks required by IdCloud FIDO SDK.
                    // As a means of convenience, the IdCloud FIDO UI SDK provides a SampleSecurePinUiCallback,SampleCommonUiCallback class which conforms to the necessary callbacks of IdCloud FIDO SDK
                    UiCallbacks uiCallbacks = new UiCallbacks();
                    SampleSecurePinUiCallback securePinUiCallback = new SampleSecurePinUiCallback(
                            fragmentManager, activity.getString(R.string.usecase_enrollment)
                    );
                    uiCallbacks.securePinPadUiCallback = securePinUiCallback;
                    uiCallbacks.biometricUiCallback = new SampleBiometricUiCallback();
                    uiCallbacks.commonUiCallback = new SampleCommonUiCallback(
                            fragmentManager
                    );

                    // Initialize an instance of EnrollmentToken from its corresponding Factory.
                    // Instances of EnrollmentToken are initialized with a code retrieved from the Bank via a QR code (i.e. or other means) and is simply encoded as a UTF8 data.
                    EnrollmentToken token = EnrollmentTokenFactory.createEnrollmentTokenWithBlob(code.getBytes());
                    if (task.isSuccessful()) {
                        token.setDevicePushToken(task.getResult());
                    } else {
                        Toast.makeText(activity, R.string.text_push_token_failure, Toast.LENGTH_SHORT).show();
                    }

                    //Set enroll Request Call back
                    final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(fragmentManager);
                    EnrollRequestCallback enrollRequestCallback = new EnrollRequestCallback() {
                        @Override
                        public void onSuccess(EnrollResponse enrollResponse) {
                            sampleResponseCallback.onSuccess();
                            listener.onSuccess(null);
                            SamplePersistence.setIsEnrolled(activity, true);
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

                    // Create an instance of the Enrollment request providing the required credentials.
                    // Instances of requests should be held as an instance variable to ensure that completion callbacks will function as expected and to prevent unexpected behaviour.
                    BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
                        @Override
                        public void onSuccess(IdCloudClient idCloudClient) {
                            EnrollRequest enrollRequest = idCloudClient.createEnrollRequest(
                                    token,
                                    uiCallbacks,
                                    enrollRequestCallback
                            );
                            //Execute enroll request.
                            enrollRequest.execute();
                        }

                        @Override
                        public void onError(IdCloudClientException ignored) { }
                    });
                }
            });
        }
    }
}
