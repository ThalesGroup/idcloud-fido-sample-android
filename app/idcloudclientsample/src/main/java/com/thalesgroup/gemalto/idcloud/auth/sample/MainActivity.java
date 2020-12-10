package com.thalesgroup.gemalto.idcloud.auth.sample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.thales.dis.mobile.idcloud.auth.IdCloudClientConfig;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui.EnrollActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set safetyNet Attestation Key. Please set it in configuration.java
        IdCloudClientConfig.setAttestationKey(Configuration.safetyNetAttestationKey);

        Intent intent = new Intent(MainActivity.this, EnrollActivity.class);
        MainActivity.this.startActivity(intent);
    }
}