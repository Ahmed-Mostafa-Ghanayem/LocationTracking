package com.ahmed.locationtracking;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationUpdateNotifier {

    private static LocationUpdateNotifier instance;
    private FusedLocationProviderClient fusedLocationProvider;

    private LocationUpdateNotifier() {
    }

    public static LocationUpdateNotifier getInstance() {
        if (instance == null) {
            instance = new LocationUpdateNotifier();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdate(Context context, LocationRequest locationRequest) {

        if (fusedLocationProvider == null) {
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context);
        }
        fusedLocationProvider.requestLocationUpdates(locationRequest, getPendingIntent(context));
    }

    public void stop(@NonNull Context context) {
        if (fusedLocationProvider != null) {
            fusedLocationProvider.removeLocationUpdates(getPendingIntent(context));
        }
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, SaveLocationToFireBaseDB.class);
        intent.setAction(SaveLocationToFireBaseDB.ACTION_SAVE_LOCATION);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}