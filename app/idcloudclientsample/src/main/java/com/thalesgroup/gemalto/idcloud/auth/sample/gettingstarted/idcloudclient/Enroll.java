package com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollResponse;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollmentToken;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollmentTokenFactory;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.SamplePersistence;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui.EnrollActivity;

public class Enroll  {

    private FragmentActivity activity;
    private String code;
    private UiCallbacks uiCallbacks;
    private IdCloudClient idCloudClient;

    public Enroll(FragmentActivity activity, String code, String url, UiCallbacks uiCallbacks) {
        this.activity = activity;
        this.code = code;
        this.uiCallbacks = uiCallbacks;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);

    }

    public void execute(EnrollActivity.OnExecuteFinishListener listener) {

        // Initialize an instance of EnrollmentToken from its corresponding Factory.
        // Instances of EnrollmentToken are initialized with a code retrieved from the Bank via a QR code (i.e. or other means) and is simply encoded as a UTF8 data.
        EnrollmentToken token = EnrollmentTokenFactory.createEnrollmentTokenWithBlob(code.getBytes());

        //Set enroll Request Call back
        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        EnrollRequestCallback enrollRequestCallback = new EnrollRequestCallback() {
            @Override
            public void onSuccess(EnrollResponse enrollResponse) {
                SamplePersistence.setIsEnrolled(activity, true);
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

        // Create an instance of the Enrollment request providing the required credentials.
        // Instances of requests should be held as an instance variable to ensure that completion callbacks will function as expected and to prevent unexpected behaviour.
        new Thread(new Runnable() {
            @Override
            public void run() {
                idCloudClient.createEnrollRequest(
                        token,
                        uiCallbacks,
                        enrollRequestCallback
                ).execute();
            }
        }).start();
    }
}
