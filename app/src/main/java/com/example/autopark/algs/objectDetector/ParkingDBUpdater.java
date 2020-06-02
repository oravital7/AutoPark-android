package com.example.autopark.algs.objectDetector;

import android.graphics.RectF;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ParkingDBUpdater {

    private List<RectF> parking;
    private FirebaseFirestore mFstore;

    public ParkingDBUpdater (List<RectF> parking )
    {
        this.parking=parking;
        mFstore = FirebaseFirestore.getInstance();
    }

    public void addParking()
    {
        //if the parking does not exist in the DB
//        mFstore.collection("parking")
//                .document(country)
//                .collection(city).add(park);
    }
}
