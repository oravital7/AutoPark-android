package com.example.autopark.algs.objectDetector;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.example.autopark.model.ParkingExt;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;



public class ParkingRecognition {
    int height, width;
    final double threshold = 0.01;
    private GeoPoint mGeoPoint;
    private  FirebaseUser mCurrentUser;

    public ParkingRecognition(int height, int width, GeoPoint geoPoint , FirebaseUser CurrentUser) {
        mGeoPoint = geoPoint;
        mCurrentUser = CurrentUser;
        this.width = 300;
        this.height = 300;
        Log.d("Detector", "h: " + this.height + ", w" + this.width);
    }

    public List<ParkingExt> detectParking(List<RectF> cars)
    {
        Log.d("detectParking" ,"size: " + cars.size());
        for (RectF rect : cars)
            Log.d("detectParking" ,"car: " + rect);
        List<ParkingExt> results = new ArrayList<ParkingExt>();
        int widthThresHold = width / 2;
        for (int i = 0; i < cars.size(); i++)
        {
            RectF park = cars.get(i);
            for (int j = i + 1; j < cars.size(); j++)
            {
//                "RectF(" + left + ", " + top + ", " + right + ", " + bottom + ")";
                RectF tempParkRect = null;
                RectF park2 = cars.get(j);
                if ((park.left <= widthThresHold && park2.left <= widthThresHold) || (park.right >= widthThresHold && park2.right >= widthThresHold))
                    tempParkRect = calcParksDistance(park, park2);
                // else
                //    tempPark = calcParksDistance(park, park2);

                if (tempParkRect != null)
                {
                    results.add(addExtParkFromRect(tempParkRect));
                }
            }
        }
        return results;
    }

    private ParkingExt addExtParkFromRect(RectF tempParkRect) {
        return new ParkingExt(Timestamp.now() , mGeoPoint ,33 ,mCurrentUser.getUid(), tempParkRect,"image");
    }

    private RectF calcParksDistance(RectF park, RectF park2)
    {
        PointF pBottom;
        PointF pTop;
        RectF parkTop, parkBottom;

        if (park.bottom > park2.bottom)
        {
            pBottom = new PointF(park2.left, park2.top);
            pTop = new PointF(park.left, park.bottom);
            parkTop = park2;
            parkBottom = park;
        }
        else
        {
            pBottom = new PointF(park.left, park.top);
            pTop = new PointF(park2.left, park2.bottom);
            parkTop = park;
            parkBottom = park2;
        }

        if(parkTop.bottom >= parkBottom.top)
            return null;

        double ac = Math.abs(pBottom.y - pTop.y);
        double cb = Math.abs(pBottom.x - pTop.x);
        double distance = Math.hypot(ac, cb);

        double avgHeight = (park.height() + park2.height()) / 2;

        PointF pTopMiddle = new  PointF(parkTop.width() / 2 + parkTop.left, parkTop.bottom - parkTop.height() / 2);
        PointF pBottomMiddle = new  PointF(parkBottom.width() / 2 + parkBottom.left, parkBottom.bottom - parkBottom.height() / 2 );
        double Car1 = parkTop.width() * parkTop.height();
        double Car2 = parkBottom.width() * parkBottom.height();
        Log.d("Carsize" , "car top" + Car1 + " , car bottom " + Car2 );
        Log.d("PrakingRecognition", "Distance [" + distance + "]" + "avgH [" + avgHeight + "]" + "res [" + distance / avgHeight + "]");

        int frameResulotion = height * width;
        if (distance >= avgHeight && (double)(parkTop.width() * parkTop.height())/frameResulotion > threshold
        && (double)(parkBottom.width() * parkBottom.height())/frameResulotion > threshold)
            return new RectF(pTopMiddle.x,pTopMiddle.y,pBottomMiddle.x,pBottomMiddle.y);
        return null;
    }

//    private List<RectF> getSideParks(List<RectF> parks, Side side)
//    {
//        List<RectF> results = new ArrayList<RectF>();
//
//        for (RectF park : parks)
//        {
//            if (side == Side.LEFT && park.right <= heightThresHold)
//                results.add(park);
//            else if (side == Side.RIGHT && park.left >= heightThresHold)
//                results.add(park);
//        }
//
//        return results;
//    }

    enum Side
    {
        RIGHT,
        LEFT
    }
}