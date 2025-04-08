/*
 * Copyright © 2020-2022 THALES. All rights reserved.
 */

package com.thalesgroup.gemalto.idcloud.auth.sample;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudConfig;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.ui.pin.PinConfig;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.EnrollActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.MainViewActivity;
import com.thalesgroup.gemalto.securelog.SecureLogConfig;

public class MainActivity extends BaseActivity {

    public static final String EXTRA_NAME_PUSH_NOTIFICATION = "MAP_EXTRA_NAME_PUSH_NOTIFICATION";
    public static final String EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN = "MAP_EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set secure log public key. Please set it in configuration.java
        SecureLogConfig secureLogConfig = new SecureLogConfig.Builder(getApplicationContext())
                .publicKey(Configuration.secureLogPublicKeyModulus, Configuration.secureLogPublicKeyExponent)
                .fileID("sample")
                .build();

        SecureLogArchive.mSecureLog = IdCloudClient.configureSecureLog(secureLogConfig);

        //Set PIN rules
        if (!PinConfig.setMinimumLength(Configuration.pinLength[0])) {
            Toast.makeText(this, "Minimal PIN length is not valid.", Toast.LENGTH_SHORT).show();
        }
        if (!PinConfig.setMaximumLength(Configuration.pinLength[1])) {
            Toast.makeText(this, "Maximal PIN length is not valid.", Toast.LENGTH_SHORT).show();
        }

        try {
            PinConfig.setPinRules(this, Configuration.pinRules);
        } catch (IdCloudClientException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        IdCloudConfig.setTlsCertificates(AppUtils.getPinningCertificates(this));

        createNotificationChannel();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Intent launchIntent;
        if (!SamplePersistence.getIsEnrolled(this)) {
            launchIntent = new Intent(MainActivity.this, EnrollActivity.class);

        } else {
            launchIntent = new Intent(MainActivity.this, MainViewActivity.class);
        }

        if (intent.getExtras() != null) {
            launchIntent.putExtra(EXTRA_NAME_PUSH_NOTIFICATION, intent.getExtras());
        }

        if (intent.getData() != null) {
            Uri appLinkUri = intent.getData();
            String enrollmentToken = appLinkUri.getQueryParameter("token");
            launchIntent.putExtra(EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN, enrollmentToken);
        }

        startActivity(launchIntent);
    }

    @Override
    public void onBackPressed() { }

    private void createNotificationChannel() {
        String channelId = getString(R.string.gcm_notification_channel_id);
        String name = getString(R.string.gcm_notification_channel_name);
        String description = getString(R.string.gcm_notification_channel_description);
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_MAX)
                .setName(name)
                .setDescription(description)
                .setVibrationEnabled(true)
                .build();
        NotificationManagerCompat.from(this).createNotificationChannel(channel);
    }
}
