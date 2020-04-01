package com.example.autopark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.autopark.model.GeoPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List<GeoPoint> points;
    private DatabaseReference reff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }
    public void addUsers(){
        GeoPoint point1 = new GeoPoint((float) 31.7402952f, (float) 34.9845028);
        GeoPoint point2 = new GeoPoint((float) 31.7402952,(float)34.9845028);
        GeoPoint point3 = new GeoPoint((float) 31.7404594,(float)34.9815202);
        points.add(point1);
        points.add(point2);
        points.add(point3);
        String id = reff.push().getKey();
        reff.child(id).setValue("data");
        reff.child(id).setValue("location");
        reff.child(id).setValue("size");
        reff.child(id).setValue("UserID");
    }
}
