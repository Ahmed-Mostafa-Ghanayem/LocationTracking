package com.ahmed.locationtracking;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.util.List;
import java.util.Locale;

public class SaveLocationToFireBaseDB extends IntentService {
    public static final String ACTION_SAVE_LOCATION = "com.ahmed.locationtracking.action.Location";

    public SaveLocationToFireBaseDB() {
        super("SaveLocationToFireBaseDB");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_LOCATION.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    for (Location location : locations) {
                        updateLocation(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        }
    }

    private void updateLocation(double lat, double lng) {
        // todo update fire base database
        String location = String.format(Locale.getDefault(), "LAT: %f , LNG: %f", lat, lng);
        Log.d("LOCATION", location);
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this, location, Toast.LENGTH_SHORT).show());
    }
}
