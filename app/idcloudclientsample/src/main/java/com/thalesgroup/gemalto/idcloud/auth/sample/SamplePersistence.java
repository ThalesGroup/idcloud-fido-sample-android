package com.thalesgroup.gemalto.idcloud.auth.sample;

import android.content.Context;
import android.content.SharedPreferences;

public class SamplePersistence {
    private static final String PREFERENCE_NAME = "SamplePersistence";
    private static final String PREFERENCE_IS_ENROLLED_KEY = "isEnrolled";

    public static boolean getIsEnrolled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_IS_ENROLLED_KEY, false);
    }

    public static void setIsEnrolled(Context context, boolean isEnrolled) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_IS_ENROLLED_KEY, isEnrolled).apply();
    }
}
