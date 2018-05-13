package com.ahmed.locationtracking;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RequestPermission extends AppCompatActivity {
    private static SingleEmitter<Boolean> emitter;
    private static String permission;
    private final int PERMISSION_REQUEST_CODE = 1111;

    public static Single<Boolean> requestPermission(@NonNull Context context,
                                                    @NonNull String permission) {
        if (checkPermission(context, permission)) {
            return Single.just(true);
        } else {
            startRequestPermissionActivity(context);
            RequestPermission.permission = permission;
            return Single.create(emitter -> RequestPermission.emitter = emitter);
        }
    }

    private static boolean checkPermission(@NonNull Context context,
                                           @NonNull String permission) {
        return !isMarshmallowOrLater() || ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED;
    }

    private static boolean isMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private static void startRequestPermissionActivity(@NonNull Context context) {
        Intent intent = new Intent(context, RequestPermission.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length == 0 || grantResults.length == 0 || grantResults[0] == PERMISSION_DENIED) {
                requestPermission();
            } else {
                emitter.onSuccess(true);
                finish();
            }
        }
    }
}