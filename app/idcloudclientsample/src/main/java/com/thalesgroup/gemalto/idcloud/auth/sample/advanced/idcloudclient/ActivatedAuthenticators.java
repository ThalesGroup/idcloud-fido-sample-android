package com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import java.util.List;


public class ActivatedAuthenticators  {

    private IdCloudClient idCloudClient;

    public ActivatedAuthenticators(FragmentActivity activity,String url) {

        // Initialize an instance of IdCloudClient.
        this.idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, url);
    }

    public List<Authenticator> execute() {

        // Retrieve a list of previously registered authenticators. Use this list to properly manage your authenticators.
        return idCloudClient.getActivatedAuthenticators();
    }

}
