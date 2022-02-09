package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseApplication;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ActivatedAuthenticators  {

    private final FragmentActivity activity;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ActivatedAuthenticators(FragmentActivity activity) {
        this.activity = activity;
    }

    public void execute(OnExecuteFinishListener<Authenticator[]> listener) {
        // Retrieve a list of previously registered authenticators. Use this list to properly manage your authenticators.
        BaseApplication.getInstance().getIdCloudClient(activity, new OnExecuteFinishListener<IdCloudClient>() {
            @Override
            public void onSuccess(IdCloudClient idCloudClient) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        List<Authenticator> authenticators = idCloudClient.getActivatedAuthenticators();
                        listener.onSuccess(authenticators.toArray(new Authenticator[0]));
                    }
                });
            }

            @Override
            public void onError(IdCloudClientException ignored) { }
        });
    }

}
