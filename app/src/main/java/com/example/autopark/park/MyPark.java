package com.example.autopark.park;

import com.google.firebase.firestore.GeoPoint;

public class MyPark {

    private GeoPoint geom;
    private String id;

    public MyPark(){}

    public MyPark(GeoPoint geom, String id) {
        this.geom = geom;
        this.id = id;
    }

    public GeoPoint getGeom() {
        return geom;
    }

    public void setGeom(GeoPoint geom) {
        this.geom = geom;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


}
