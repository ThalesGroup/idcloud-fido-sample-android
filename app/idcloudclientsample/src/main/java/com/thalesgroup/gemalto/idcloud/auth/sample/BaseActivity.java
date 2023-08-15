/*
 * Copyright © 2022 THALES. All rights reserved.
 */

package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BaseActivity extends AppCompatActivity {

    protected void rejectAppLinksEnrollment() {
        Intent intent = getIntent();
        String enrollmentToken = intent.getStringExtra(MainActivity.EXTRA_NAME_APP_LINKS_ENROLLMENT_TOKEN);
        if (enrollmentToken != null) {
            DialogUtil.showAlertDialog(this, getString(R.string.alert_error_title), getString(R.string.alert_error_message_app_links), null);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    exportLogArchive();
                } else {
                    Toast.makeText(this, "External storage permission required to export the log.", Toast.LENGTH_SHORT).show();
                }
            });

    public void shareLogEmail() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }

        exportLogArchive();
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

    private void exportLogArchive() {
        // 1. generate secure log zip file into app's internal storage
        File zipFile = SecureLogArchive.createSecureLogZip(getApplicationContext());

        // 2. export zip file into external download folder
        Uri savedUri = saveSecureLogArchiveToDownload(zipFile);
        if (savedUri != null) {
            Toast.makeText(this, "Log zip has been saved to Download folder: " + savedUri.getPath(), Toast.LENGTH_SHORT).show();
        } else {
            return;
        }

        // 3. share the zip via email
        shareSecureLogArchiveViaEmail(savedUri);
    }

    private void shareSecureLogArchiveViaEmail(Uri attachmentUri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, SecureLogArchive.getEmailTitle(getApplicationContext()));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.secureLog_email_content));
        sendIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.securelog_chooser_title)));
    }

    private Uri saveSecureLogArchiveToDownload(File zipFile) {
        try (FileInputStream inputStream = new FileInputStream(zipFile)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, zipFile.getName());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                ContentResolver resolver = getApplicationContext().getContentResolver();

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri != null) {
                    try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int readBytes = inputStream.read(buffer);
                            if (readBytes == -1) break;
                            outputStream.write(buffer, 0, readBytes);
                        }
                        return uri;
                    }
                }
            } else {
                File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadFolder.exists()) {
                    downloadFolder.mkdirs();
                }
                File file = new File(downloadFolder, zipFile.getName());
                if (file.exists()) {
                    file.delete();
                }
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int readBytes = inputStream.read(buffer);
                        if (readBytes == -1) break;
                        outputStream.write(buffer, 0, readBytes);
                    }
                    return Uri.fromFile(file);
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error save to downloads: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
