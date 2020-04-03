package com.example.autopark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.autopark.map.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void mapHandlerBtn(View v)
    {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void logOutHandler(View v) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
    }
}
