package com.example.autopark.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.autopark.R;
import com.example.autopark.model.Parking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.opencv.android.NativeCameraView.TAG;

public class popUp {

    //PopupWindow display method
    private Parking myPark;
    private FirebaseFirestore mFstore;
    private Context context;
    popUp(Parking p,Context context)
{
    this.myPark=p;
    this.context=context;
}
    public void showPopupWindow(final View view) throws IOException {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_parkingtaken, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, 700, 700, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

       TextView popUpText = popupView.findViewById(R.id.textPopup);

        Geocoder geocoders = new Geocoder(context , Locale.ENGLISH);
        popUpText.setText("do you park in "+getAddressName(myPark.getGeom(),geocoders));
//
       Button noB = popupView.findViewById(R.id.noBpark);
        noB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Button yesB = popupView.findViewById(R.id.yesBpark);
        yesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(view.getContext(), "park:"+myPark.getGeom().toString(), Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                List<Address> addressList = null;
                String city = "";
                String country = "";
                mFstore = FirebaseFirestore.getInstance();

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
                    final CollectionReference itemsRef=  mFstore.collection("parking").document(country)
                            .collection(city);

                    Query query = itemsRef.whereEqualTo("geom", myPark.getGeom());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    itemsRef.document(document.getId()).delete();

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                                }
            }
        });

    }
    public String getAddressName(GeoPoint geoPoint, Geocoder mGeocoders) throws IOException {

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
}
