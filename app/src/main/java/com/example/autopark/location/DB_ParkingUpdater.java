package com.example.autopark.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.example.autopark.R;
import com.example.autopark.model.Parking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DB_ParkingUpdater {
    private FirebaseFirestore mFstore;
//    private Location parkingLocation;
    private String TAG = "dbUpdater";
    private ConstraintLayout layout;
    private Parking_updater activity;
    public DB_ParkingUpdater(Parking_updater p)
    {
        activity=p;
        mFstore = FirebaseFirestore.getInstance();
        //this.parkingLocation=parkingLocation;
    }

    public void checkIfParkingIsTaken(final Location parkingLocation, final Context context)
    {
        layout=activity.findViewById(R.id.con);
        final Parking[] foundPark = {null};
        List<Address> addressList = null;
        //Locale locale = new Locale("he", "IL");
        String city = "";
        String country = "";

        Geocoder geocoders = new Geocoder(context , Locale.ENGLISH);
//        try {
//            addressList = geocoders.getFromLocation(parkingLocation.getLatitude(), parkingLocation.getLongitude(), 1);
//            city = addressList.get(0).getLocality();
//
//
//            country = addressList.get(0).getCountryName();
//            Log.d("cityinit", "gotcity"+city);
//            Log.d("cityinit", country);
//
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
        if(city.isEmpty())
            city = "Ra'anana";
        if(country.isEmpty())
            country = "Israel";

        if(!city.isEmpty() && !country.isEmpty()) {
        mFstore.collection("parking").document(country)
                .collection(city)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Parking mPark;
                            Location mParkLoc = parkingLocation;
                            double latitude;
                            double longitude;
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                //DocumentSnapshot documentSnapshot = dc

                                mPark = dc.toObject(Parking.class);
                                latitude = mPark.getGeom().getLatitude() / 1E6;
                                longitude = mPark.getGeom().getLongitude() / 1E6;

                                mParkLoc.setLatitude(latitude);
                                mParkLoc.setLongitude(longitude);

                                if(isParking(parkingLocation,mParkLoc))
                                {
                                    Log.d("cityinit", "mpark found");
                                    popUp popUpClass = new popUp(mPark,context);
                                    try {
                                        popUpClass.showPopupWindow(layout);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    }

    public boolean isParking(Location currentLocation, Location DBParkingLocation)
    {   double venueLat =DBParkingLocation.getLatitude();
        double venueLng =DBParkingLocation.getLongitude();

        double latDistance = Math.toRadians(currentLocation.getLatitude() - venueLat);
        double lngDistance = Math.toRadians(currentLocation.getLongitude() - venueLng);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(currentLocation.getLatitude() ))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = 6371 * c;
        if (dist<0.01){
            return true;
        }
        return true;
    }

}
