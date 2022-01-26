package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import static com.thalesgroup.gemalto.idcloud.auth.sample.ui.PushNotificationService.MAP_EXTRA_NAME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

public class PushNotificationBroadcastReceiver extends BroadcastReceiver {

        private PushNotificationService.PushNotificationHandler handler;

        PushNotificationBroadcastReceiver(PushNotificationService.PushNotificationHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Map<String, String> notification = (Map<String, String>) intent.getSerializableExtra(MAP_EXTRA_NAME);
            handler.handlePushNotification(notification);
        }
    }
