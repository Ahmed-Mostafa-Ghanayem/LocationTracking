package com.ahmed.locationtracking;

class PlayServicesNotAvailableException extends RuntimeException {
    PlayServicesNotAvailableException() {
        super("Make sure play services are installed in your device");
    }
}