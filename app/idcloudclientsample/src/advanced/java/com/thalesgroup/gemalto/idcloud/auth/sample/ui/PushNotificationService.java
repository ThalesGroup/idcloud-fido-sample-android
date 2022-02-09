package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thalesgroup.gemalto.idcloud.auth.sample.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {
    public static final String PROCESS_PUSH_NOTIFICATION_ACTION = "PROCESS_PUSH_NOTIFICATION_ACTION";

    @Override
    public void onNewToken(String token) {
        Log.e("IdCloudDebug", "FCM Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Intent intent = new Intent(PROCESS_PUSH_NOTIFICATION_ACTION);
            HashMap<String, String> map = new HashMap<>(remoteMessage.getData());
            intent.putExtra(MainActivity.EXTRA_NAME_PUSH_NOTIFICATION, map);
            intent.setAction(PROCESS_PUSH_NOTIFICATION_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    public interface PushNotificationHandler {
        void handlePushNotification(Map<String, String> notification);
    }
}