package com.example.autopark.locationUpdater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.autopark.locationUpdater.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tag", "in on receive");
        if(intent != null)
        {
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action))
            {
                LocationResult result = LocationResult.extractResult(intent);

                if(result != null)
                {
                    Location location = result.getLastLocation();
                    String location_string = new StringBuilder(""+location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();
                    try{
                        locationUpdaterActivity.getInstance().updateTextView(location_string);
                        Toast.makeText(context,"MyLocationSeervice update: "+location_string,Toast.LENGTH_SHORT).show();
                        Log.d("tag", "got ocation hopefullt in the bacground"+location_string);

                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(context,"MyLocationSeervice failed ): "+location_string,Toast.LENGTH_SHORT).show();
                        Log.d("tag", "MyLocationSeervice failed ");
                    }
                }
            }
        }
    }
}
