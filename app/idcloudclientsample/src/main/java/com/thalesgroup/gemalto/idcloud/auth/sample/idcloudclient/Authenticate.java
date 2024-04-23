/*
 * Copyright © 2022 THALES. All rights reserved.
 */

package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequest;
import com.thales.dis.mobile.idcloud.auth.operation.FetchRequestCallback;
import com.thales.dis.mobile.idcloud.auth.operation.FetchResponse;
import com.thales.dis.mobile.idcloud.auth.operation.IdCloudProgress;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleBiometricUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleResponseCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;
import com.thalesgroup.gemalto.idcloud.auth.sample.Progress;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.CustomAppClientConformerCallback;

public class Authenticate {

    private final FragmentActivity activity;
    private IdCloudClient idCloudClient;

    public Authenticate(FragmentActivity activity) {
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Void> listener) {
        Progress.showProgress(activity, IdCloudProgress.START);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // Set up an instance of UiCallbacks, an encapsulated class containing all necessary UI callbacks required by IdCloud FIDO SDK.
        // As a means of convenience, the IdCloud FIDO UI SDK provides a SampleSecurePinUiCallback,SampleCommonUiCallback class which conforms to the necessary callbacks of IdCloud FIDO SDK
        /* 1 */
        ## Set up the necessary UI callbacks ##

        /* 2 */
        ## Set fetch request callbacks ##

        // Create an instance of the Fetch request.
        // Instances of requests should be held as an instance variable to ensure that callbacks will function as expected and to prevent unexpected behaviour.
        BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
            @Override
            public void onSuccess(IdCloudClient idCloudClient) {
                /* 3 */
                ## Create a Fetch request callback ##
                
                /* 4 */
                ## Execute request ##
            }

            @Override
            public void onError(IdCloudClientException e) {
                Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
