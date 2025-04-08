/*
 * Copyright © 2025 THALES. All rights reserved.
 */

package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();

    public static X509Certificate[] getPinningCertificates(Context context) {
        X509Certificate[] certificates = new X509Certificate[1];
        certificates[0] = AppUtils.getCertificate(context, R.raw.intermediate_cert);

        return certificates;
    }

    private static X509Certificate getCertificate(Context context, int resId) {
        X509Certificate certificate = null;
        InputStream caInput = null;

        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(context.getResources().openRawResource(resId));
            certificate = (X509Certificate) cf.generateCertificate(caInput);
            Log.i(TAG, "ca=" + (certificate).getSubjectDN());
        } catch (CertificateException ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            if (caInput != null) {
                try {
                    caInput.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        return certificate;
    }
}
