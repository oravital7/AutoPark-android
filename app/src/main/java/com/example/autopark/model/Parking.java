package com.example.autopark.model;


import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;

public class Parking {
    private Timestamp data;
    private GeoPoint Geom;
    private double size;
    private String ID;

    public Parking(){

    }
    public Parking(Timestamp data ,GeoPoint Geom , double size , String ID){
        this.data = data;
        this.Geom = Geom;
        this.size = size;
        this.ID = ID;
    }


    public GeoPoint getGeom() {
        return Geom;
    }

    public void setGeom(GeoPoint geom) {
        Geom = geom;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
