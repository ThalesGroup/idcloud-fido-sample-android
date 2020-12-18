package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollRequest;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollResponse;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollmentToken;
import com.thales.dis.mobile.idcloud.auth.operation.EnrollmentTokenFactory;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.SamplePersistence;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.EnrollActivity;

public class Enroll  {

    private FragmentActivity activity;
    private String code;
    private IdCloudClient idCloudClient;

    public Enroll(FragmentActivity activity, String code, String url) {
        this.activity = activity;
        this.code = code;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);

    }

    public void execute(EnrollActivity.OnExecuteFinishListener listener) {

        Progress.showProgress(activity, IdCloudProgress.START);
        new Thread(new Runnable() {
            @Override
            public void run() {

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                // Set up an instance of UiCallbacks, an encapsulated class containing all necessary UI callbacks required by IdCloud FIDO SDK.
                // As a means of convenience, the IdCloud FIDO UI SDK provides a SampleSecurePinUiCallback,SampleCommonUiCallback class which conforms to the necessary callbacks of IdCloud FIDO SDK
                /* 1 */
                ## Set up the necessary UI callbacks ##

                // Initialize an instance of EnrollmentToken from its corresponding Factory.
                // Instances of EnrollmentToken are initialized with a code retrieved from the Bank via a QR code (i.e. or other means) and is simply encoded as a UTF8 data.
                /* 2 */
                ## Insert enrollment token creation ##

                //Set enroll Request Call back
                /* 3 */
                ## Create request callback ##

                // Create an instance of the Enrollment request providing the required credentials.
                // Instances of requests should be held as an instance variable to ensure that completion callbacks will function as expected and to prevent unexpected behaviour.
                /* 4 */
                ## Create necessary request ##

                //Execute enroll request.
                /* 5 */
                ## Execute request ##
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

    private void processOnSuccess() {
        SamplePersistence.setIsEnrolled(activity, true);
        Progress.hideProgress();
    }
}
