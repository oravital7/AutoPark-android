package com.example.autopark.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;


import com.example.autopark.R;
import com.example.autopark.model.Parking;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private static final String TAG = "tsst";
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private SearchView mSearchView;
    private Marker mMarkerseache;
    private FirebaseFirestore mFstore;
    private Parking mPark;
    private List<Parking> mParking;
    private Geocoder mGeocoders;
    private HashMap<String, Marker> hashMapMarker = new HashMap<>();
    List<Marker> mMarkersSearch = new ArrayList<Marker>();


    // The entry point to the Places API.
//    private PlacesClient mPlacesClient;

    private ImageView mGps;
    private ImageView mSendLocation;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLocation;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    // Used for selecting the current place.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        mGps = findViewById(R.id.ic_gps);
        mSendLocation= findViewById(R.id.send_location);
        mSearchView = findViewById(R.id.location);
        mFstore = FirebaseFirestore.getInstance();
        mParking = new ArrayList<>();
        mGeocoders = new Geocoder(MapsActivity.this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLocation = locationManager.getLastKnownLocation(provider);
//        Log.d("Or", mLocation.toString() + "add");




        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }



    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        requestLocationUpdates();





        searchView();

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        mSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLastKnownLocation != null){
                    GeoPoint geoPoint = new GeoPoint(mLastKnownLocation.getLatitude() , mLastKnownLocation.getLongitude());
                    addParking(geoPoint);

                }
            }
        });
    }




    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLastKnownLocation = location;
                            if (mLastKnownLocation != null) {
                                Log.d("isLocation", mLastKnownLocation.toString());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            }
                            else {
                                Log.d("isLocation", "noLocationfind");
                                mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            }

                        }
                    }

                });
        startDb();

    }
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d("sendl", "location update " + location);
                        mLastKnownLocation = location;
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude() , location.getLongitude());
                        startDb();
                    }
                }
            }, null);
        }
    }



    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }



    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    public void searchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("map", "createUserWithEmail:success");


                String location = mSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (!location.isEmpty()){
                    //Locale locale = new Locale("he", "IL");
                    Geocoder geocoder = new Geocoder(MapsActivity.this , Locale.ENGLISH);;
                    try {
                        addressList = geocoder.getFromLocationName(location , 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        String city = address.getLocality();
                        String country = address.getCountryName();
                        mFstore.collection("parking").document(country)
                                .collection(city)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w("test", "listen:error", e);
                                            return;
                                        }
                                        //real time update
                                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                            DocumentSnapshot documentSnapshot = dc.getDocument();
                                            mPark = documentSnapshot.toObject(Parking.class);
                                            if(mPark.getGeom() != null) {
                                                LatLng latLng = new LatLng(mPark.getGeom().getLatitude(), mPark.getGeom().getLongitude());
                                                try {
                                                    mMarkersSearch.add(mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressName(mPark.getGeom())).snippet("id :" + mPark.getID())));
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }

                                    }
                                });
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));


                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!mMarkersSearch.isEmpty())
                    for (Marker marker: mMarkersSearch) {
                        marker.remove();
                    }
                mMarkersSearch.clear();

                return false;
            }
        });
    }


    public void startDb(){
        // need to check if there is a current location
        //mLocation = new Location("32.06302,34.77155");
        if(mLastKnownLocation != null)
            Log.d("locationc", "havelocation");
        else if(mLastKnownLocation == null)
            Log.d("locationc", "nolocation");
        List<Address> addressList = null;
       //Locale locale = new Locale("he", "IL");
        Geocoder geocoders = new Geocoder(MapsActivity.this , Locale.ENGLISH);

        String city = "";
        String country = "";

        try {
            addressList = geocoders.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
            city = addressList.get(0).getLocality();


            country = addressList.get(0).getCountryName();
            Log.d("cityinit", city);
            Log.d("cityinit", country);

        } catch(Exception e) {
            e.printStackTrace();
        }
        if(!city.isEmpty() && !country.isEmpty()) {
            mFstore.collection("parking").document(country)
                    .collection(city)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("test", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED: {
                                        Log.d("test", "New city: " + dc.getDocument().getData());
                                        DocumentSnapshot documentSnapshot = dc.getDocument();

                                        mPark = documentSnapshot.toObject(Parking.class);
                                        Log.d("test", "mPark: " + mPark.getGeom());
                                        Boolean isExist = false;
                                        for (Parking park : mParking) {
//                                                if(park.getGeom().equals(mPark.getGeom()))
                                            if (park.getGeom().compareTo(mPark.getGeom()) == 0) {
                                                isExist = true;
                                                break;
                                            }
                                        }
                                        if (!isExist) {
                                            Log.d("test", "new add on: " + mPark.getGeom());
                                            mParking.add(mPark);
                                            LatLng latLng = new LatLng(mPark.getGeom().getLatitude(), mPark.getGeom().getLongitude());
                                            try {
                                                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressName(mPark.getGeom())).snippet("id :" + mPark.getID()));
                                                Log.d("address", "new add on: " + getAddressName(mPark.getGeom()));
                                                hashMapMarker.put(mPark.getID(), marker);
                                                //mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressName(mPark.getGeom())).snippet("id :" + mPark.getID()));

                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                        }

                                    }

                                    break;
                                    case MODIFIED:
                                        Log.d("test", "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        DocumentSnapshot documentSnapshot = dc.getDocument();

                                        mPark = documentSnapshot.toObject(Parking.class);
                                        Marker marker = hashMapMarker.get(mPark.getID());

                                        marker.remove();
                                        hashMapMarker.remove(mPark.getID());
                                        Log.d("test", "Removed city: " + dc.getDocument().getData());
                                        break;
                                }
                            }

                        }
                    });
        }



    }
    public String getAddressName(GeoPoint geoPoint) throws IOException {

        List<Address> addressList = null;

        try {
            addressList = mGeocoders.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String address = "";

        if (addressList != null && !addressList.isEmpty())
            address = addressList.get(0).getAddressLine(0);

        return address;
    }
    public Location getCurrentLocation(){
        return mLastKnownLocation;
    }

    public void addParking(GeoPoint myLocation)
    {
        String Id = "20";
        int size = 5;
        Timestamp timestamp = Timestamp.now();
        Parking park = new Parking(timestamp,myLocation, size, Id);
        //Locale locale = new Locale("he", "IL");
        Geocoder geocoders = new Geocoder(MapsActivity.this , Locale.ENGLISH);
        //converting to list
        List<Address> addressListIntialize=new ArrayList<>();
        try {
            addressListIntialize = geocoders.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String city = addressListIntialize.get(0).getLocality();
        String country = addressListIntialize.get(0).getCountryName();

        mFstore.collection("parking")
                .document(country)
                .collection(city).add(park);
    }

}
