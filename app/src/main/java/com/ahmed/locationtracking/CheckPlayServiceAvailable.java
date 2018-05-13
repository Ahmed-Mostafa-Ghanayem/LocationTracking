package com.ahmed.locationtracking;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.reactivex.Single;

public class CheckPlayServiceAvailable {

    private CheckPlayServiceAvailable() {
    }

    public static CheckPlayServiceAvailable create() {
        return new CheckPlayServiceAvailable();
    }

    public Single<Boolean> check(@NonNull Context context) {
        return Single.create(e -> {
            final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            final int status = apiAvailability.isGooglePlayServicesAvailable(context);
            if (status != ConnectionResult.SUCCESS) {
                e.onError(new PlayServicesNotAvailableException());
            } else {
                e.onSuccess(true);
            }
        });
    }
}
