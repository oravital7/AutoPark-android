package com.example.autopark.algs.objectDetector;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;



public class ParkingRecognition {
    int height, width;

    public ParkingRecognition(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public List<RectF> detectParking(List<RectF> cars)
    {
        List<RectF> results = new ArrayList<RectF>();
        int heightThresHold = height / 2;

        for (int i = 0; i < cars.size(); i++)
        {
            RectF park = cars.get(i);
            for (int j = i+1; j < cars.size(); j++)
            {
                RectF park2 = cars.get(j);
                RectF tempPark = calcParksDistance(park, park2);
                if (tempPark != null)
                    results.add(tempPark);
//                if (park.right <= heightThresHold && park2.right <= heightThresHold)
//                {
//                    RectF tempPark = calcParksDistance(park, park2);
//                    if (tempPark != null)
//                        results.add(tempPark);
//                }
//                else if (park.left >= heightThresHold && park2.left >= heightThresHold)
//                {
//                    // TODO:: Call function
//                }
            }
        }

        return results;
    }

    private RectF calcParksDistance(RectF park, RectF park2)
    {
        PointF pBottom;
        PointF pTop;

        if (park.bottom > park2.bottom)
        {
            pBottom = new PointF(park2.left, park2.top);
            pTop = new PointF(park.left, park.bottom);
        }
        else
        {
            pBottom = new PointF(park.left, park.top);
            pTop = new PointF(park2.left, park2.bottom);
        }

        double ac = Math.abs(pBottom.y - pTop.y);
        double cb = Math.abs(pBottom.x - pTop.x);
        double distance = Math.hypot(ac, cb);


        double avgHeight = (park.height() + park2.height()) / 2;

        if (distance >= avgHeight)
        {
            return new RectF(park.left, pTop.y, park.right, pBottom.y);
        }

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