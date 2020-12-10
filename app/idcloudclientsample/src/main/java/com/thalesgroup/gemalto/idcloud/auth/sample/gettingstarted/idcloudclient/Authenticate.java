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
            }

            @Override
            public void onError(IdCloudClientException e) {
                sampleResponseCallback.onError();
                listener.onError(e);
            }

            @Override
            public void onProgress(IdCloudProgress code) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (code) {
                            case RETRIEVING_REQUEST:
                            case VALIDATING_AUTHENTICATION:
                                if(Progress.progressDialog == null) {
                                    Progress.showProgressDialog(activity, code);
                                } else {
                                    Progress.updateProgressMessage(activity,code);
                                }
                                break;
                            case PROCESSING_REQUEST:
                            case END:
                                Progress.dismissProgress();
                                break;
                        }
                    }
                });
            }
        };

        // Create an instance of the Fetch request.
        // Instances of requests should be held as an instance variable to ensure that callbacks will function as expected and to prevent unexpected behaviour.
        idCloudClient.createFetchRequest(uiCallbacks, fetchRequestCallback).execute();
    }
}
