package com.example.autopark.model;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;



public class Parking {
    private Timestamp date;
    private GeoPoint Geom;
    private double size;
    private String ID;

    public Parking(){

    }
    public Parking(Timestamp date ,GeoPoint Geom , double size , String ID){
        this.date = date;
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

    public Timestamp getDate() { return date; }

    public void setDate(Timestamp date) {
        this.date = date;
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
