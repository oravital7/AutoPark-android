package com.example.autopark.map;
import androidx.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.autopark.R;
import com.example.autopark.model.Parking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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
        mSearchView = (SearchView) findViewById(R.id.location);
        mGps = (ImageView)findViewById(R.id.ic_gps);
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
        connectToDatabase();
        searchView();
    }
    public void connectToDatabase(){
        mFstore.collection("parking").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                mPark = d.toObject(Parking.class);
                                Log.d("Geom " , String.valueOf(mPark.getGeom()));
                                mParking.add(mPark);
                            }
                            for (Parking p : mParking){
                                if(p.getGeom()!=null) {
                                    LatLng latLng = new LatLng(p.getGeom().getLatitude(), p.getGeom().getLongitude());
                                    try {
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressName(p.getGeom())).snippet("size :" + p.getSize()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public void searchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mSearchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals("")){
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
    public void getDeviceLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
//        GeoPoint geoPoint = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
//        try {
//            mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title(getAddressName(geoPoint)).snippet("Current Location"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
    public void addDataToDataBase(){
        Timestamp time = Timestamp.now();
        GeoPoint p1 = new GeoPoint(32.0923952,34.9691267);
        GeoPoint p2 = new GeoPoint(32.0910606,34.9689872);
        GeoPoint p3 = new GeoPoint(32.0908038,34.9669221);
        GeoPoint p4 = new GeoPoint(32.0901877,34.9657993);
        double size = 12;
        double size1 = 10;
        double size3 = 9;
        double size4 = 11;
        String user1 = "2a";
        String user2 = "3a";
        Parking park2 = new Parking(time,p2,size1,user1);
        Parking park3 = new Parking(time,p3,size3,user2);
        Parking park4 = new Parking(time,p4,size4,user2);
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
                        LatLng latLng = new LatLng(p.getGeom().getLatitude(), p.getGeom().getLongitude());
                        try {
                            mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressName(p.getGeom())).snippet("size :" + p.getSize()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
}
