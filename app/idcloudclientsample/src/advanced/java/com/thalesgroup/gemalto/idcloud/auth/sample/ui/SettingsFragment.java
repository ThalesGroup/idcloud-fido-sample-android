package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.thales.dis.mobile.idcloud.auth.IdCloudClient;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.OnExecuteFinishListener;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.RefreshPushToken;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

import java.util.List;


public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        findPreference(getString(R.string.key_sdk_version)).setTitle(IdCloudClient.getSDKVersion());
        findPreference(getString(R.string.key_refresh_push_token)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                executeRefreshPushToken(new OnExecuteFinishListener<Void>() {
                    @Override
                    public void onSuccess(Void ignored) {
                        DialogUtil.showToastMessage(getActivity(), getString(R.string.refresh_push_success));
                    }

                    @Override
                    public void onError(IdCloudClientException e) {
                        DialogUtil.showAlertDialog(getActivity(), getString(R.string.refresh_push_alert_title), getString(R.string.refresh_push_fail), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    }
                });
                return false;
            }
        });
    }


    private void executeRefreshPushToken(final OnExecuteFinishListener listener) {
        if (isNotificationsEnabled()) {
            RefreshPushToken refreshPushToken = new RefreshPushToken(getActivity());
            refreshPushToken.execute(listener);
        } else {
            DialogUtil.showAlertDialog(getActivity(), getString(R.string.alert_error_title), getString(R.string.enable_push_notification_from_setting_error), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(intent);
                }
            });
        }
    }

    public boolean isNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    return false;
                }
            }
            return true;
        } else {
            return NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
        }
    }
}
