package com.thalesgroup.gemalto.idcloud.auth.sample;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientConfig;
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

        //Set safetyNet Attestation Key. Please set it in configuration.java
        IdCloudClientConfig.setAttestationKey(Configuration.safetyNetAttestationKey);

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
