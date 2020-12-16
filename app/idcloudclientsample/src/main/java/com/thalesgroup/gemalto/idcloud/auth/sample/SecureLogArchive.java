package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.thalesgroup.gemalto.securelog.SecureLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SecureLogArchive {

    public static SecureLog mSecureLog;

    public static File createSecureLogZip(Context context) {

        if (mSecureLog == null) {
            Toast.makeText(context, "SecureLog is not configure!", Toast.LENGTH_LONG)
                    .show();
            return null;
        }
        List<File> slFiles = mSecureLog.getFiles();
        if (slFiles == null || slFiles.isEmpty()) {
            Toast.makeText(context, "Log file list is empty!", Toast.LENGTH_LONG)
                    .show();
            return  null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd(hh.mm)", Locale.US);
        String zFileName = "secureLog-" + sdf.format(new Date()) + ".zip";
        File zipFile = new File(context.getExternalFilesDir(null), zFileName);
        if (zipFile.exists()) {
            zipFile.delete();
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            byte[] buffer = new byte[6 * 1024];
            int read;

            for (File f : slFiles) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    ZipEntry zipEntry = new ZipEntry(f.getName());
                    zos.putNextEntry(zipEntry);

                    while ((read = fis.read(buffer)) != -1)
                        zos.write(buffer, 0, read);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            AlertDialog dlg = new AlertDialog.Builder(context)
                    .setTitle("Failed creating zip file")
                    .setMessage("Can not create secureLog zip file, error: " + ex.getMessage())
                    .create();
            dlg.show();
        }

        return zipFile;
    }

    public static String getEmailTitle(Context context) {
        String title = BuildConfig.FLAVOR;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            title += " - " + pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        title += " is failing!";
        return  title;
    }

}