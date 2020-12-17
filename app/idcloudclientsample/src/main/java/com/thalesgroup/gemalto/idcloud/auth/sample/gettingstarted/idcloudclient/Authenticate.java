package com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequest;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.FetchResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.SamplePersistence;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui.AuthenticateHomeFragment;

public class Authenticate {

    private FragmentActivity activity;
    private UiCallbacks uiCallbacks;
    private IdCloudClient idCloudClient;

    public Authenticate(FragmentActivity activity, String url, UiCallbacks uiCallbacks) {
        this.activity = activity;
        this.uiCallbacks = uiCallbacks;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(AuthenticateHomeFragment.OnExecuteFinishListener listener) {

        //Set fetch request callbacks
        /* 2 */
        ## Create request callback ##

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Create an instance of the Fetch request.
                // Instances of requests should be held as an instance variable to ensure that callbacks will function as expected and to prevent unexpected behaviour.
                /* 3 */
                ## Create necessary request ##

                //Execute request
                /* 4 */
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
}
