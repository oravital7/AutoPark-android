package com.example.autopark;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.example.autopark.map.MapsActivity;
import com.example.autopark.model.Parking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class sendLocation extends Activity implements LocationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Geocoder geocoders;
    Button b_send;
    Button allPark;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_location);
        b_send = findViewById(R.id.b_sendLoc);
        geocoders = new Geocoder(sendLocation.this);
        allPark = findViewById(R.id.get_data);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("Latitude", "disable");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        b_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("trying stuff", "" +getLastLocation().getLatitude()+" "+getLastLocation().getLongitude());
                Location location = getLastLocation();
                if (location != null) {
                    GeoPoint gp = new GeoPoint(location.getLatitude(), location.getLongitude());
                    addParking(gp);
                }
            }
        });
        allPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("parking")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshots,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("err", "Listen failed.", e);
                                    return;
                                }

                                for (DocumentChange dc : snapshots.getDocumentChanges()) {

                                        Log.d(TAG, "New city: " + dc.getDocument().toString());

                                }
                            }
                        });

        }});
    }


    public Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public void addParking(GeoPoint myLocation)
    {
        String Id = "20";
        int size = 5;
        Timestamp timestamp = Timestamp.now();
        Parking park = new Parking(timestamp,myLocation, size, Id);
        //converting to list
        List<Address> addressListIntialize=new ArrayList<>();
        try {
            addressListIntialize = geocoders.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String city = addressListIntialize.get(0).getLocality();
        String country = addressListIntialize.get(0).getCountryName();

        db.collection("parking")
                .document(country)
                .collection(city).add(park);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d("Locationnnnnn","Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
