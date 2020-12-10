package com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thales.dis.mobile.idcloud.authui.util.DialogUtils;
import com.thalesgroup.gemalto.idcloud.auth.sample.Configuration;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.SecureLogArchive;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.idcloudclient.Enroll;
import com.thalesgroup.gemalto.idcloud.auth.sample.ui.MainViewActivity;

import java.io.File;

public class EnrollActivity extends AppCompatActivity {

    private Enroll enrollObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        Button btn_enroll = (Button)findViewById(R.id.button_Enroll);
        TextView textView =(TextView)findViewById(R.id.textView_enroll);

        btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //For convenience, a QR Scanner is provided to retrieve the enrollmentToken.
                DialogUtils.showQRscanner(EnrollActivity.this, new DialogUtils.OnRegisterListener() {
                    @Override
                    public void onClickOK(String registrationCode) {
                        //Execute enroll use-case
                        executeEnroll(registrationCode, new OnExecuteFinishListener() {
                            @Override
                            public void onSuccess() {
                                showAlertDialog(getString(R.string.enroll_alert_title),getString(R.string.enroll_alert_message));
                            }
                            @Override
                            public void onError(IdCloudClientException e) {
                                showAlertDialog(getString(R.string.alert_error_title),e.getLocalizedMessage());
                            }
                        });
                    }
                });
            }
        });

        ImageButton btn_share =(ImageButton)findViewById(R.id.imageButton_shareInEnroll);
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


    private void executeEnroll(String registrationCode,final OnExecuteFinishListener listener) {
        // Initialize an instance of the Enroll use-case, providing
        // (1) the retrieved code
        // (2) the pre-configured URL
        // (3) the uiCallbacks

        FragmentManager fragmentManager = EnrollActivity.this.getSupportFragmentManager();

        UiCallbacks uiCallbacks = new UiCallbacks();
        SampleSecurePinUiCallback securePinUiCallback = new SampleSecurePinUiCallback(
                fragmentManager, getString(R.string.usecase_enrollment)
        );
        uiCallbacks.securePinPadUiCallback = securePinUiCallback;
        uiCallbacks.commonUiCallback = new SampleCommonUiCallback(
                fragmentManager
        );

        enrollObj = new Enroll(EnrollActivity.this, registrationCode, Configuration.url, uiCallbacks);
        enrollObj.execute(listener);
    }

    public interface OnExecuteFinishListener {
        void onSuccess();
        void onError(IdCloudClientException e);
    }

    protected void showAlertDialog(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EnrollActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(title.equals(getString(R.string.enroll_alert_title))) {
                                    Intent intent = new Intent(EnrollActivity.this, MainViewActivity.class);
                                    EnrollActivity.this.startActivity(intent);
                                }
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });
    }
}