package com.example.autopark.model;

public class GeoPoint {
    private float lat;
    private float lon;

    public GeoPoint() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public GeoPoint(float lat , float lon) {
        this.lat = lat;
        this.lon = lon;
    }
    public float getLat() {
        return lat;
    }
    public void setLat(float lat) {
        this.lat = lat;
    }
    public float getLon() {
        return lon;
    }
    public void setLon(float lon){
        this.lon = lon;
    }

}
