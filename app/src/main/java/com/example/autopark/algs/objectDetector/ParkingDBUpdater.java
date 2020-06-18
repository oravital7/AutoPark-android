package com.example.autopark.algs.objectDetector;

import android.content.Context;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.example.autopark.model.Parking;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;


public class ParkingDBUpdater {


    private List<RectF> parking;
    private FirebaseFirestore mFstore;

    static final String REQUEST_METHOD = "POST";

    private long mLastUpdate;

    final String url = "http://176.228.53.84:3000/parks/add/"; // your URL

    private Context context;
    private Geocoder mGeocoders;
    ParkingDBUpdater(Context context){
        this.context = context;
        mLastUpdate = 0;
    }

    public boolean addParking(Parking freePark) throws JSONException {
        Geocoder  Geocoders = new Geocoder(this.context, Locale.ENGLISH);
        if (Calendar.getInstance().getTimeInMillis() - mLastUpdate <= 2000)
            return false;

        GeoPoint geoPoint = freePark.getGeom();
        List<Address> addressListIntialize=new ArrayList<>();
        try {
            addressListIntialize = Geocoders.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressListIntialize.isEmpty())
            return false;

        String city = addressListIntialize.get(0).getLocality();
        String country = addressListIntialize.get(0).getCountryName();
        JSONObject json = new JSONObject();
        json.put("city" , city);
        json.put("country" , country);
        json.put("userId" , freePark.getID());
        JSONObject Geom = new JSONObject();
        Geom.put("_latitude", geoPoint.getLatitude());
        Geom.put("_longitude", geoPoint.getLongitude());
        json.accumulate("Geom" , Geom);

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest lastFMAuthRequest = new JsonObjectRequest (Request.Method.POST, url,json ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        mLastUpdate = Calendar.getInstance().getTimeInMillis();
// add it to the RequestQueue
        queue.add(lastFMAuthRequest);
        return true;
    }
}
