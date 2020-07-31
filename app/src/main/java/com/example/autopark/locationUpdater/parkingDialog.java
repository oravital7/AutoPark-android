package com.example.autopark.locationUpdater;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.autopark.map.MapsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatDialogFragment;

public class parkingDialog extends AppCompatDialogFragment {
    private String mParkID;
    private Context mMapActivityContext;
    private Location mLastLocation;
    private FirebaseFirestore mFstore;

    public parkingDialog(String parkID,Context mapActivityContext,Location LastLocation)
    {
        mParkID = parkID;
        mMapActivityContext = mapActivityContext;
        mLastLocation = LastLocation;
        mFstore = FirebaseFirestore.getInstance();
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Did you park here?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //should remove the parking from the DB
                        MapsActivity.isParking=true;
                        Log.d("Currnet_Location", "clicked on yes");
                        //remove parking from DB
                        Geocoder geocoders = new Geocoder(mMapActivityContext , Locale.ENGLISH);
                        List<Address> addressListIntialize=new ArrayList<>();
                        try {
                            addressListIntialize = geocoders.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String city = addressListIntialize.get(0).getLocality();
                        String country = addressListIntialize.get(0).getCountryName();
                        Log.d("parlingID in maps", mParkID);
                        mFstore.collection("parking")
                                .document(country)
                                .collection(city).document(mParkID).delete();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }
}
