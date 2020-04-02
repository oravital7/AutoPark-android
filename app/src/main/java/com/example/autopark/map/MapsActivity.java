package com.example.autopark.map;


import androidx.fragment.app.FragmentActivity;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.autopark.R;

import com.example.autopark.model.Parking;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int Reques_Code=101;
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    float mZoomLevel = 16.0f;

    private FirebaseFirestore mFstore;
    private Parking mPark;
    private List<Parking> mParking;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFstore = FirebaseFirestore.getInstance();
        mParking = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getlastLocation();
    }

    private void getlastLocation() {
        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mLocation = location;
                    Toast.makeText(getApplicationContext() , mLocation.getLatitude()+""+ mLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(mLocation.getLatitude() , mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel));
        connectToDatabase();
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
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("Parking"));
                                }
                            }
                        }
                    }
                });
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
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case Reques_Code:
//                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    GetlastLocation();
//                }
//                break;
//        }
//    }
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
