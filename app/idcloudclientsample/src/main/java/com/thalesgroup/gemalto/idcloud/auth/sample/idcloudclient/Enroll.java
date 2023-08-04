package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;

public class Enroll {

    protected final FragmentActivity activity;
    protected final String code;

    public Enroll(FragmentActivity activity, String code) {
        this.activity = activity;
        this.code = code;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // Set up an instance of UiCallbacks, an encapsulated class containing all necessary UI callbacks required by IdCloud FIDO SDK.
        // As a means of convenience, the IdCloud FIDO UI SDK provides a SampleSecurePinUiCallback,SampleCommonUiCallback class which conforms to the necessary callbacks of IdCloud FIDO SDK
        /* 1 */
        ## Set up the necessary UI callbacks ##

        // Initialize an instance of EnrollmentToken from its corresponding Factory.
        // Instances of EnrollmentToken are initialized with a code retrieved from the Bank via a QR code (i.e. or other means) and is simply encoded as a UTF8 data.
        /* 2 */
        ## Create an enrollment token ##

        /* 3 */
        ## Set enroll Request Call back ##


        // Create an instance of the Enrollment request providing the required credentials.
        // Instances of requests should be held as an instance variable to ensure that completion callbacks will function as expected and to prevent unexpected behaviour.
        /* 4 */
        ## Create an Enroll request ##

        /* 5 */
        ## Execute the request ##

    }

}
