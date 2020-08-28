package com.example.autopark.algs.objectDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.autopark.map.MapsActivity;
import com.example.autopark.model.Parking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ParkingDBUpdater {


    private List<RectF> parking;
    private FirebaseFirestore mFstore;
    private FirebaseUser mFirebaseUser;
    static final String REQUEST_METHOD = "POST";

    private long mLastUpdate;
    private long mLastUpdateAdd;

    String url;
    private Context context;
    private Geocoder mGeocoders;
    public ParkingDBUpdater(Context context){
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.context = context;
        mLastUpdate = 0;
        mLastUpdateAdd=0;
    }

    private JSONObject jsonBuilder (GeoPoint userlocation,String ParkID,Bitmap image,PointF centerPoint, float park_size_percentage) throws JSONException {
        Geocoder  Geocoders = new Geocoder(this.context, Locale.ENGLISH);


        GeoPoint geoPoint = userlocation;

        List<Address> addressListIntialize=new ArrayList<>();
        try {
            addressListIntialize = Geocoders.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressListIntialize.isEmpty())
            return null;


        String city = addressListIntialize.get(0).getLocality();

        String country = addressListIntialize.get(0).getCountryName();
        JSONObject json = new JSONObject();

        json.put("city" , city);
        json.put("country" , country);
        json.put("userId" , ParkID);
        JSONObject Geom = new JSONObject();
        Log.d("lat lon are:", geoPoint.toString());
        Geom.put("_latitude", geoPoint.getLatitude());
        Geom.put("_longitude", geoPoint.getLongitude());
        json.accumulate("Geom" , Geom);
        if(image!=null)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            json.put("image",encodedImage);

        }
        if(centerPoint!=null)
        {
            JSONObject centerP = new JSONObject();
            centerP.put("x", centerPoint.x);
            centerP.put("y", centerPoint.y);
            json.accumulate("centerP" , centerP);
        }
        if(park_size_percentage!=0 && image!=null)
        {
            park_size_percentage = park_size_percentage / image.getHeight();
            json.put("sizePercentage" , park_size_percentage);
        }
        return json;
    }
    public void checkIfParkingExist(final MapsActivity mapsActivity, GeoPoint userlocation, final Location location) throws JSONException
    {
        Log.d("geom","got this geom: "+userlocation);
        url = "http://176.228.53.84:3000/parks/check/";
        if (Calendar.getInstance().getTimeInMillis() - mLastUpdate <= 2000)
            return;

        JSONObject json  =jsonBuilder(userlocation,mFirebaseUser.getUid(),Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),new PointF(1,3),0);
        if(json!=null) {
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest lastFMAuthRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                final String ParkingId = response.getString("id");
                                if (ParkingId != null) {
                                    mapsActivity.openDialog(location, ParkingId);
                                }
                                Log.d("parlingID", ParkingId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
            queue.add(lastFMAuthRequest);

        }
        else
        {
            Log.d("response", "address could not be found");
        }
    }


    public boolean addParking(Parking freePark,Bitmap image, PointF centerPoint, float width) throws JSONException {
        url = "http://176.228.53.84:3000/parks/add/";

        if (Calendar.getInstance().getTimeInMillis() - mLastUpdateAdd <= 2000) {
            long thisTimerFalse = Calendar.getInstance().getTimeInMillis() - mLastUpdateAdd;
            Log.d("TimerFalse" ," "+thisTimerFalse);
            return false;
        }
        long thisTimerTrue = Calendar.getInstance().getTimeInMillis() - mLastUpdateAdd;
        Log.d("TimerTrue" ," "+thisTimerTrue);
        JSONObject json = jsonBuilder(freePark.getGeom(), freePark.getID(), image, centerPoint, width);
        if (json != null) {
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest lastFMAuthRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "response: " + response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error2.Response", error.toString());
                        }
                    }
            );

            mLastUpdateAdd = Calendar.getInstance().getTimeInMillis();
// add it to the RequestQueue
            queue.add(lastFMAuthRequest);
            return true;
        }
            else {
            Log.d("response", "address could not be found");
                return false;
            }

    }

}
