package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import static com.thalesgroup.gemalto.idcloud.auth.sample.ui.PushNotificationService.PROCESS_PUSH_NOTIFICATION_ACTION;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.MainActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.OnExecuteFinishListener;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.ProcessNotification;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

import java.util.HashMap;
import java.util.Map;

public class MainViewActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, PushNotificationService.PushNotificationHandler {

    private BottomNavigationView navigation;
    private int navigationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);

        //loading the default fragment
        loadFragment(new AuthenticateHomeFragment());
        navigationId = R.id.navigation_home;

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        rejectAppLinksEnrollment();
    }

    BroadcastReceiver receiver;

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new PushNotificationBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(PROCESS_PUSH_NOTIFICATION_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.EXTRA_NAME_PUSH_NOTIFICATION);
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
                navigationId = R.id.navigation_home;
                break;

            case R.id.navigation_authenticators:
                fragment = new AuthenticatorsFragment();
                navigationId = R.id.navigation_authenticators;
                break;

            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                navigationId = R.id.navigation_settings;
                break;

            default:
                return false;
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
        ProcessNotification processNotification = new ProcessNotification(this, notification);
        processNotification.execute(new OnExecuteFinishListener<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                DialogUtil.showToastMessage(MainViewActivity.this, getString(R.string.fetch_alert_message));

            }

            @Override
            public void onError(IdCloudClientException e) {
                if (e.getError() != IdCloudClientException.ErrorCode.USER_CANCELLED) {
                    DialogUtil.showAlertDialog(MainViewActivity.this, getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        switch (navigationId) {
            case R.id.navigation_home:
                super.onBackPressed();
                break;

            case R.id.navigation_authenticators:
            case R.id.navigation_settings:
                Fragment fragment = new AuthenticateHomeFragment();
                loadFragment(fragment);
                navigationId = R.id.navigation_home;
                navigation.setSelectedItemId(R.id.navigation_home);
        }
    }
}
