package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;

public interface IdCloudClientProvider {

    void createIdCloudClient(FragmentActivity activity);

    void removeIdCloudClient(FragmentActivity activity);

    void getIdCloudClient(FragmentActivity activity, OnExecuteFinishListener<IdCloudClient> listener);
}
