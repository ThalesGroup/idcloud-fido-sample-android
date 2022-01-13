package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import static com.thalesgroup.gemalto.idcloud.auth.sample.ui.PushNotificationService.MAP_EXTRA_NAME;
import static com.thalesgroup.gemalto.idcloud.auth.sample.ui.PushNotificationService.PROCESS_PUSH_NOTIFICATION_ACTION;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thalesgroup.gemalto.idcloud.auth.sample.Configuration;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.ProcessNotification;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

import java.util.HashMap;
import java.util.Map;

public class MainViewActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, PushNotificationService.PushNotificationHandler {

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);

        //loading the default fragment
        loadFragment(new AuthenticateHomeFragment());

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    BroadcastReceiver receiver;

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new PushNotificationBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(PROCESS_PUSH_NOTIFICATION_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MAP_EXTRA_NAME);
        if (bundle != null && bundle.containsKey("ms")) {
            Map<String, String> notification = new HashMap<>();
            notification.put("ms", bundle.getString("ms"));
            handlePushNotification(notification);
            this.setIntent(new Intent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new AuthenticateHomeFragment();
                break;

            case R.id.navigation_authenticators:
                fragment = new AuthenticatorsFragment();
                break;

            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;

        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

            return true;
        }
        return false;
    }

    @Override
    public void handlePushNotification(Map<String, String> notification) {
        ProcessNotification processNotification = new ProcessNotification(this, Configuration.url, notification);
        processNotification.execute(new OnExecuteFinishListener() {
            @Override
            public void onSuccess() {
                DialogUtil.showAlertDialog(MainViewActivity.this, getString(R.string.authenticate_alert_title), getString(R.string.authenticate_alert_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(IdCloudClientException e) {
                DialogUtil.showAlertDialog(MainViewActivity.this, getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }
}
