package com.ahmed.locationtracking;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


@RequiresApi(api = Build.VERSION_CODES.M)
public class StartUpdateLocation extends Service {
    private Disposable disposable;

    private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationRequest locationRequest;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopLocation();
        dispose(disposable);
        createLocationRequest();
        disposable = checkGooglePlayServiceAvailable()
                .flatMap(requestPowerOptimizationPermission())
                .flatMap(requestLocationPermissionFunction())
                .flatMap(connectGoogleAPIClientFunction())
                .flatMap(enableLocationServiceSettingFunction())
                .doOnSuccess(startLocationUpdateConsumer())
                .subscribe(enabled -> stopSelf(), getLocationErrorConsumer());
        return super.onStartCommand(intent, flags, startId);
    }

    private void createLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setFastestInterval(/*5 * 60 * 1000 */5000)
                    .setInterval(/*7 * 60 * 1000 */10000)
                    .setMaxWaitTime(/*7 * 60 * 1000 */12000);
        }
    }

    @NonNull
    private Single<Boolean> checkGooglePlayServiceAvailable() {
        return CheckPlayServiceAvailable.create().check(this);
    }

    private Function<Boolean, SingleSource<? extends Boolean>> requestPowerOptimizationPermission() {
        return available -> PowerOptimizationIgnorePermission.create().requestPermission(this);
    }

    @NonNull
    private Function<Boolean, SingleSource<? extends Boolean>> requestLocationPermissionFunction() {
        return permissionGranted -> RequestPermission.requestPermission(this, LOCATION_PERMISSION);
    }

    @NonNull
    private Function<Boolean, SingleSource<? extends Boolean>> connectGoogleAPIClientFunction() {
        return permissionGranted -> ConnectGoogleApiClient.create()
                .ConnectGoogleApiClient(this);
    }

    @NonNull
    private Function<Boolean, SingleSource<? extends Boolean>> enableLocationServiceSettingFunction() {
        return connected -> EnableLocationServiceSetting
                .checkLocationServiceSetting(this, locationRequest);
    }

    @NonNull
    private Consumer<Boolean> startLocationUpdateConsumer() {
        return locationServiceOn -> {
            Log.d("LOCATION_SERVICE", "APPROVED: " + locationServiceOn);
            LocationUpdateNotifier.getInstance().startLocationUpdate(getApplicationContext(), locationRequest);
        };
    }

    @NonNull
    private Consumer<Throwable> getLocationErrorConsumer() {
        return Throwable::printStackTrace;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dispose(disposable);
    }

    private void stopLocation() {
        LocationUpdateNotifier.getInstance().stop(getApplicationContext());
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}