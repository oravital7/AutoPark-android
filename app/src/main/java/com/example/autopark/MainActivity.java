package com.example.autopark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.autopark.model.Parking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore mFstore;
    Parking park;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFstore = FirebaseFirestore.getInstance();
        mFstore.collection("parking").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list){
                                park = d.toObject(Parking.class);
                                Toast toast = Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                });



    }
    public void addUsers(){
//        GeoPoint point1 = new GeoPoint((float) 31.7402952f, (float) 34.9845028);
//        GeoPoint point2 = new GeoPoint((float) 31.7402952,(float)34.9845028);
//        GeoPoint point3 = new GeoPoint((float) 31.7404594,(float)34.9815202);
//        points.add(point1);
//        points.add(point2);
//        points.add(point3);
//        String id = reff.push().getKey();
//        reff.child(id).setValue("data");
//        reff.child(id).setValue("location");
//        reff.child(id).setValue("size");
//        reff.child(id).setValue("UserID");
    }
}
