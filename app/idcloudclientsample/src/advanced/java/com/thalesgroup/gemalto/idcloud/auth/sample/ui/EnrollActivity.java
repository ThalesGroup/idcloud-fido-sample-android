package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.authui.util.DialogUtils;
import com.thalesgroup.gemalto.idcloud.auth.sample.Configuration;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.SecureLogArchive;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.EnrollWithPush;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

import java.io.File;

public class EnrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        Button btn_enroll = (Button) findViewById(R.id.button_Enroll);
        TextView textView = (TextView) findViewById(R.id.textView_enroll);

        btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //For convenience, a QR Scanner is provided to retrieve the enrollmentToken.
                DialogUtils.showQRscanner(EnrollActivity.this, new DialogUtils.OnRegisterListener() {
                    @Override
                    public void onClickOK(String registrationCode) {
                        //Execute enroll use-case
                        //1.show dialog to ask user whether enroll with push noti or not
                        showEnablePushNotiAlertDialog(getString(R.string.alert_push_noti_title), getString(R.string.alert_push_noti_message), registrationCode);
                    }
                });
            }
        });

        ImageButton btn_share = (ImageButton) findViewById(R.id.imageButton_shareInEnroll);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Prepare secure log zip folder.
                File zipFile = SecureLogArchive.createSecureLogZip(EnrollActivity.this);

                //Sending secureLog through zip
                if (Build.VERSION.SDK_INT >= 24) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, SecureLogArchive.getEmailTitle(EnrollActivity.this));
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.secureLog_email_content));
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipFile));
                startActivity(Intent.createChooser(sendIntent, getString(R.string.securelog_chooser_title)));

            }
        });
    }

    @Override
    public void onBackPressed() {
        //do nothing here
    }


    private void executeEnroll(String registrationCode, final OnExecuteFinishListener listener) {
        // Initialize an instance of the EnrollWithPush use-case, providing
        // (1) the retrieved code
        // (2) the pre-configured URL
        EnrollWithPush enrollObj = new EnrollWithPush(EnrollActivity.this, registrationCode, Configuration.url);
        enrollObj.execute(listener);
    }

    protected void showEnablePushNotiAlertDialog(final String title, final String message, final String registrationCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pushNotiEnableSharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pushNotiEnableSharedPref.edit();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EnrollActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putBoolean(getString(R.string.enable_push_noti_key), false);
                                editor.apply();
                                dialog.dismiss();
                                callExecuteEnroll(registrationCode);
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                editor.putBoolean(getString(R.string.enable_push_noti_key), true);
                                editor.apply();
                                dialog.dismiss();
                                callExecuteEnroll(registrationCode);
                            }
                        });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    private void callExecuteEnroll(String registrationCode) {
        executeEnroll(registrationCode, new OnExecuteFinishListener() {
            @Override
            public void onSuccess() {
                DialogUtil.showAlertDialog(EnrollActivity.this, getString(R.string.enroll_alert_title), getString(R.string.enroll_alert_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(EnrollActivity.this, MainViewActivity.class);
                        EnrollActivity.this.startActivity(intent);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(IdCloudClientException e) {
                DialogUtil.showAlertDialog(EnrollActivity.this, getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
