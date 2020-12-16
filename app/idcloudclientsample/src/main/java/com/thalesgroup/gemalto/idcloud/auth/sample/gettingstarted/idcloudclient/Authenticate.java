package com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.FetchResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
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

        //Create fetch request callbacks
        final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(activity.getSupportFragmentManager());
        FetchRequestCallback fetchRequestCallback = new FetchRequestCallback() {
            @Override
            public void onSuccess(FetchResponse fetchResponse) {
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
            public void onProgress(IdCloudProgress code) {
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

        // Create an instance of the Fetch request.
        // Instances of requests should be held as an instance variable to ensure that callbacks will function as expected and to prevent unexpected behaviour.
        new Thread(new Runnable() {
            @Override
            public void run() {
                idCloudClient.createFetchRequest(uiCallbacks, fetchRequestCallback).execute();
            }
        }).start();
    }
}
