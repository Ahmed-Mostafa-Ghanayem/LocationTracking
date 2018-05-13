package com.ahmed.locationtracking;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;

public class ConnectGoogleApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SingleEmitter<Boolean> emitter;
    private GoogleApiClient client;

    private ConnectGoogleApiClient() {
    }

    public static ConnectGoogleApiClient create() {
        return new ConnectGoogleApiClient();
    }

    public Single<Boolean> ConnectGoogleApiClient(@NonNull Context context) {
        return Single.create(emitter -> {
            this.emitter = emitter;
            client = createGoogleApiClient(context);
            client.connect();
        });
    }

    private GoogleApiClient createGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        emitter.onSuccess(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        emitter.onError(new GoogleApiClientConnectionException("Connection suspended"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        emitter.onError(new GoogleApiClientConnectionException("Connection Failed"));
    }
}