package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.IdCloudClientConfig;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui.EnrollActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.MainViewActivity;
import com.thalesgroup.gemalto.securelog.SecureLogConfig;
import com.thalesgroup.gemalto.securelog.SecureLogLevel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set secure log public key. Please set it in configuration.java
        SecureLogConfig secureLogConfig = new SecureLogConfig.Builder(getApplicationContext())
                .publicKey(Configuration.secureLogPublicKeyModulus, Configuration.secureLogPublicKeyExponent)
                .fileID("sample")
                .level(SecureLogLevel.ALL)
                .rollingFileMaxCount(10)
                .rollingFileMaxSizeInKB(100)
                .directory(getApplicationContext().getExternalFilesDir(null))
                .build();

        SecureLogArchive.mSecureLog = IdCloudClient.configureSecureLog(secureLogConfig);

        //Set safetyNet Attestation Key. Please set it in configuration.java
        IdCloudClientConfig.setAttestationKey(Configuration.safetyNetAttestationKey);

        Intent intent;
        if (!SamplePersistence.getIsEnrolled(this)) {
            intent = new Intent(MainActivity.this, EnrollActivity.class);
        } else {
            intent = new Intent(MainActivity.this, MainViewActivity.class);
        }
        MainActivity.this.startActivity(intent);
    }
}