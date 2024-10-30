package com.example.am;

import android.location.LocationManager;

public class CustomLocationProvider {
    private LocationManager locationManager;

    public CustomLocationProvider(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public String getName() {
        return LocationManager.GPS_PROVIDER; // Ou outro provedor de localização desejado
    }
}