package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.authui.util.DialogUtils;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.Configuration;
import com.thalesgroup.gemalto.idcloud.auth.sample.MainActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.Enroll;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.OnExecuteFinishListener;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

public class EnrollActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        OnExecuteFinishListener<Void> listener = new OnExecuteFinishListener<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                DialogUtil.showToastMessage(EnrollActivity.this, getString(R.string.enroll_alert_message));
                Intent intent = new Intent(EnrollActivity.this, MainViewActivity.class);
                EnrollActivity.this.startActivity(intent);
            }

            @Override
            public void onError(IdCloudClientException e) {
                if (e.getError() != IdCloudClientException.ErrorCode.USER_CANCELLED) {
                    DialogUtil.showAlertDialog(EnrollActivity.this, getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        };

        Button enrollButton = (Button) findViewById(R.id.button_Enroll);
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For convenience, a QR Scanner is provided to retrieve the enrollmentToken.
                DialogUtils.showQRscanner(EnrollActivity.this, new DialogUtils.OnRegisterListener() {
                    @Override
                    public void onClickOK(String enrollmentToken) {
                        //Execute enroll use-case
                        executeEnroll(enrollmentToken, listener);
                    }
                });
            }
        });

        ImageButton shareButton = (ImageButton) findViewById(R.id.imageButton_shareInEnroll);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLogEmail();
            }
        });

        if (getIntent() != null && getIntent().getStringExtra(MainActivity.EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN) != null) {
            String enrollmentToken = getIntent().getStringExtra(MainActivity.EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN);
            executeEnroll(enrollmentToken, listener);
        }
    }

    private void executeEnroll(String enrollmentToken, OnExecuteFinishListener listener) {
        // Initialize an instance of the Enroll use-case, providing
        // (1) the retrieved code
        Enroll enrollObj = new Enroll(EnrollActivity.this, enrollmentToken);
        enrollObj.execute(listener);
    }

}
