package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ActivatedAuthenticators  {

    private IdCloudClient idCloudClient;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ActivatedAuthenticators(FragmentActivity activity,String url) {

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public void execute(OnExecuteFinishListener<Authenticator[]> listener) {
        // Retrieve a list of previously registered authenticators. Use this list to properly manage your authenticators.
        executor.submit(new Runnable() {
            @Override
            public void run() {
                List<Authenticator> authenticators = idCloudClient.getActivatedAuthenticators();
                listener.onSuccess(authenticators.toArray(new Authenticator[0]));
            }
        });
    }

}
