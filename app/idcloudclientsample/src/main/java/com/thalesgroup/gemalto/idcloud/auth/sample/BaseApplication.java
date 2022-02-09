package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.app.Application;

import androidx.fragment.app.FragmentActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientFactory;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.IdCloudClientProvider;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.OnExecuteFinishListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseApplication extends Application implements IdCloudClientProvider {

    private final Map<Integer, IdCloudClient> idCloudClientPool = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static BaseApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void createIdCloudClient(FragmentActivity activity) {
        IdCloudClient idCloudClient = IdCloudClientFactory.createIdCloudClient(activity, Configuration.url);
        idCloudClientPool.put(activity.hashCode(), idCloudClient);
    }

    @Override
    public void removeIdCloudClient(FragmentActivity activity) {
        idCloudClientPool.remove(activity.hashCode());
    }

    @Override
    public void getIdCloudClient(FragmentActivity activity, OnExecuteFinishListener<IdCloudClient> listener) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (!idCloudClientPool.containsKey(activity.hashCode())) {
                    createIdCloudClient(activity);
                }
                listener.onSuccess(idCloudClientPool.get(activity.hashCode()));
            }
        });
    }

}
