package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequest;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.FetchResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.AuthenticateHomeFragment;

public class Authenticate {

    private FragmentActivity activity;
    private IdCloudClient idCloudClient;

    public Authenticate(FragmentActivity activity, String url) {
        this.activity = activity;

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(AuthenticateHomeFragment.OnExecuteFinishListener listener) {

        Progress.showProgress(activity, IdCloudProgress.START);

        new Thread(new Runnable() {
            @Override
            public void run() {

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                // Set up an instance of UiCallbacks, an encapsulated class containing all necessary UI callbacks required by IdCloud FIDO SDK.
                // As a means of convenience, the IdCloud FIDO UI SDK provides a SampleSecurePinUiCallback,SampleCommonUiCallback class which conforms to the necessary callbacks of IdCloud FIDO SDK
                UiCallbacks uiCallbacks = new UiCallbacks();
                SampleSecurePinUiCallback securePinUiCallback = new SampleSecurePinUiCallback(
                        fragmentManager, activity.getString(R.string.usecase_fetch)
                );
                uiCallbacks.securePinPadUiCallback = securePinUiCallback;
                uiCallbacks.commonUiCallback = new SampleCommonUiCallback(
                        fragmentManager
                );

                //Set fetch request callbacks
                final SampleResponseCallback sampleResponseCallback = new SampleResponseCallback(fragmentManager);
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
                        processOnProgress(code);
                    }
                };

                // Create an instance of the Fetch request.
                // Instances of requests should be held as an instance variable to ensure that callbacks will function as expected and to prevent unexpected behaviour.
                FetchRequest fetchRequest = idCloudClient.createFetchRequest(uiCallbacks,
                                                                             fetchRequestCallback);
                //Execute request
                fetchRequest.execute();
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
