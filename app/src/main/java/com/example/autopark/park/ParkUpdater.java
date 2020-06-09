package com.example.autopark.park;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class ParkUpdater {
    private Location mLocation;
    private Location mLastKnownLocation;
    private static final String KEY_LOCATION = "location";
    private LocationCallback locationCallback;
    public ParkUpdater()
    {


    }

    public void update()
    {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("location","current location"+location.toString());
                }
            }
        };
    }


}
