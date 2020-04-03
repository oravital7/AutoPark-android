package com.example.autopark.map;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.autopark.R;
import com.example.autopark.model.Parking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Location mLocation;
    private GoogleMap mMap;
    float mZoomLevel = 16.0f;
    private SearchView mSearchView;
    private LatLng mCurrentLocation;
    private LocationManager mLocationManager;

    private FirebaseFirestore mFstore;
    private Parking mPark;
    private List<Parking> mParking;
    private ImageView mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFstore = FirebaseFirestore.getInstance();
        mSearchView = findViewById(R.id.location);
        mGps = findViewById(R.id.ic_gps);
        mParking = new ArrayList<>();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDeviceLocation();
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        searchView();
    }

    public void searchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mSearchView.getQuery().toString();
                List<Address> addressList = null;
                if(!location.isEmpty()){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location , 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel));
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLocation != null)
            mCurrentLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, mZoomLevel));
    }
    public String getAddressName(GeoPoint geoPoint) throws IOException {
        List<Address> addressList = null;
        Geocoder geocoders = new Geocoder(MapsActivity.this);

        addressList = geocoders.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        String address = addressList.get(0).getAddressLine(0);

        return  address;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mFstore.collection("parking").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                mParking.clear();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    mPark = documentSnapshot.toObject(Parking.class);
                    Log.d("Geom " , String.valueOf(mPark.getGeom()));
                    mParking.add(mPark);
                }
                for (Parking p : mParking){
                    if(p.getGeom()!=null) {
//                        if(calculateRadius(p.getGeom())) {
                            LatLng latLng = new LatLng(p.getGeom().getLatitude(), p.getGeom().getLongitude());
                            try {
                                mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressName(p.getGeom())).snippet("size :" + p.getSize()));
                            } catch (IOException e1) {
                                e1.printStackTrace();
//                            }
                        }
                    }
                }
            }
        });
    }
    public Boolean calculateRadius(GeoPoint geoPoint){
        Location locationA = new Location("A");
        locationA.setLatitude(geoPoint.getLatitude());
        locationA.setLongitude(geoPoint.getLongitude());
        float dis = mLocation.distanceTo(locationA);
        Log.d("distant " , String.valueOf(dis));
        if(dis > 5000) {
            return false;
        }
        else
            return true;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app
     */
}
