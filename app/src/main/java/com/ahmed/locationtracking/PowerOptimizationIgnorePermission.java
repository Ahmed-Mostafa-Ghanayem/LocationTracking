package com.ahmed.locationtracking;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import io.reactivex.Single;

public class PowerOptimizationIgnorePermission {
    private PowerOptimizationIgnorePermission() {
    }

    public static PowerOptimizationIgnorePermission create() {
        return new PowerOptimizationIgnorePermission();
    }

    @SuppressLint("BatteryLife")
    public Single<Boolean> requestPermission(@NonNull Context context) {
        if (isMarshmallowOrLater()) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                return Single.just(true);
            }
        }
        return Single.just(true);
    }

    private boolean isMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}