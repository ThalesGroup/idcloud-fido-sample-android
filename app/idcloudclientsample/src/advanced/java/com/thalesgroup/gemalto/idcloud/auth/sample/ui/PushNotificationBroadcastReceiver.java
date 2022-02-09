package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thalesgroup.gemalto.idcloud.auth.sample.MainActivity;

import java.util.Map;

public class PushNotificationBroadcastReceiver extends BroadcastReceiver {

        private PushNotificationService.PushNotificationHandler handler;

        PushNotificationBroadcastReceiver(PushNotificationService.PushNotificationHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Map<String, String> notification = (Map<String, String>) intent.getSerializableExtra(MainActivity.EXTRA_NAME_PUSH_NOTIFICATION);
            handler.handlePushNotification(notification);
        }
    }
