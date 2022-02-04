package com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;

public interface OnExecuteFinishListener<T> {

    void onSuccess(T result);
    void onError(IdCloudClientException e);

}
