package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    protected void rejectAppLinksEnrollment() {
        Intent intent = getIntent();
        String enrollmentToken = intent.getStringExtra(MainActivity.EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN);
        if (enrollmentToken != null) {
            DialogUtil.showAlertDialog(this, getString(R.string.alert_error_title), getString(R.string.alert_error_message_app_links), null);
        }
    }

    public void shareLogEmail() {
        // Prepare secure log zip folder.
        File zipFile = SecureLogArchive.createSecureLogZip(this);

        //Sending secureLog through zip
        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, SecureLogArchive.getEmailTitle(this));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.secureLog_email_content));
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipFile));
        startActivity(Intent.createChooser(sendIntent, getString(R.string.securelog_chooser_title)));
    }

    private long lastBackPressed = 0;

    @Override
    public void onBackPressed() {
        // press back twice in 2s to exit the app
        long currentBackPressed = System.currentTimeMillis();
        if (currentBackPressed - lastBackPressed < 3000) {
            finishAffinity();
        } else {
            lastBackPressed = currentBackPressed;
            Toast.makeText(this, R.string.text_back_exit, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().removeIdCloudClient(this);
    }
}
