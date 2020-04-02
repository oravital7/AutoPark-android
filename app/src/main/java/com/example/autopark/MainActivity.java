package com.example.autopark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.autopark.login.LoginActivity;
import com.example.autopark.login.RegistrationActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
    }

    public void signInHandler(View v) { startActivity(new Intent(this, LoginActivity.class)); }

    public void signUpInHandler(View v) { startActivity(new Intent(this, RegistrationActivity.class)); }

}