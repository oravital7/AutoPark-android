package com.example.autopark.model;

import android.graphics.RectF;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class ParkingExt extends Parking {



    private RectF mRectF;

    public ParkingExt(Timestamp date , GeoPoint Geom , double size , String ID , RectF rectF){
        super(date ,Geom ,size,ID);
        mRectF = rectF;
    }

    public RectF getmRectF() {
        return mRectF;
    }
}
