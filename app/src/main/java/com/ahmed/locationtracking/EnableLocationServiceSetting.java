package com.ahmed.locationtracking;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EnableLocationServiceSetting extends AppCompatActivity {

    private static SingleEmitter<Boolean> emitter;
    private static LocationRequest locationRequest;
    private final int SETTING_REQUEST_CODE = 2222;

    public static Single<Boolean> checkLocationServiceSetting(@NonNull Context context,
                                                              @NonNull LocationRequest locationRequest) {
        if (isLocationEnabled(context)) {
            return Single.just(true);
        } else {
            startCheckLocationServiceSettingActivity(context);
            EnableLocationServiceSetting.locationRequest = locationRequest;
            return Single.create(emitter -> EnableLocationServiceSetting.emitter = emitter);
        }
    }


    private static boolean isLocationEnabled(@NonNull Context context) {
        int locationMode;
        String locationProviders;
        if (isKitkatOrLater()) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private static boolean isKitkatOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private static void startCheckLocationServiceSettingActivity(@NonNull Context context) {
        Intent intent = new Intent(context, EnableLocationServiceSetting.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLocationServiceSetting();
    }

    private void checkLocationServiceSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            Log.d("TASK", "SUCCEEDED");
            emitter.onSuccess(true);
            emitter = null;
            finish();
        });

        task.addOnFailureListener(this, e -> {
            Log.d("TASK", "FAILED");
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(EnableLocationServiceSetting.this,
                            SETTING_REQUEST_CODE);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });

        task.addOnCanceledListener(this, () -> {
            Log.d("TASK", "CANCELED");
            checkLocationServiceSetting();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTING_REQUEST_CODE && resultCode == RESULT_OK) {
            emitter.onSuccess(true);
            finish();
        } else {
            checkLocationServiceSetting();
        }
    }
}