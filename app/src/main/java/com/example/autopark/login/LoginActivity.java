package com.example.autopark.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.autopark.MainMenu;
import com.example.autopark.R;
import com.example.autopark.utils.ValidationData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    final String loginActivityTag = "LoginActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void signInHandler(View v) {
        final ProgressBar progressBar = findViewById(R.id.loginProgress);
        progressBar.setVisibility(VideoView.VISIBLE);

        final EditText email = findViewById(R.id.login_email);
        final EditText password = findViewById(R.id.login_password);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (!isValid(email, password)) {
            progressBar.setVisibility(VideoView.GONE);
            return;
        }

        auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(VideoView.GONE);

                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                        } else {
                            email.setError("Email or Password is incorrect");
                            Log.d(loginActivityTag, "Login failed ", task.getException());
                        }
                    }
                });
    }

    private boolean isValid(EditText email, EditText password) {
        boolean isValid = true;

        if (!ValidationData.validEmail(email))
            isValid = false;
        if (!ValidationData.validPassword(password))
            isValid = false;

        return isValid;
    }
}
