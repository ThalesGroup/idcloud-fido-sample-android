package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;

public interface OnExecuteFinishListener {

    void onSuccess();
    void onError(IdCloudClientException e);
}
