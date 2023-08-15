/*
 * Copyright © 2022 THALES. All rights reserved.
 */

package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;

public interface IdCloudClientProvider {

    void createIdCloudClient(FragmentActivity activity) throws IdCloudClientException;

    void removeIdCloudClient(FragmentActivity activity);

    void getIdCloudClient(FragmentActivity activity, OnExecuteFinishListener<IdCloudClient> listener);
}
