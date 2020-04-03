package com.example.autopark.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.autopark.MainMenu;
import com.example.autopark.R;
import com.example.autopark.model.User;
import com.example.autopark.utils.ValidationData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    final String TAG = "RegistrationActivityTag";
    final String userCollection = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registerHandler(View v) {
        final User user = new User();
        final ProgressBar progressBar = findViewById(R.id.registrationProgress);
        progressBar.setVisibility(VideoView.VISIBLE);

        if (fillUserData(user)) {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            EditText password = findViewById(R.id.password);

            auth.createUserWithEmailAndPassword(user.getEmail(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                updateAdditionalInformation(auth.getCurrentUser(), user);
                                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Authentication failed. Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }

                            progressBar.setVisibility(VideoView.GONE);
                        }
                    });
        }
    }

    private void updateAdditionalInformation(FirebaseUser currentUser, User dataUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userCollection).document(currentUser.getUid()).set(dataUser);
    }

    private boolean fillUserData(User user) {
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText email = findViewById(R.id.email);
        EditText country = findViewById(R.id.country);
        EditText city = findViewById(R.id.city);
        EditText street = findViewById(R.id.street);
        EditText houseNumber = findViewById(R.id.houseNumber);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText password = findViewById(R.id.password);

        boolean isValid = true;
        if (!ValidationData.validEmptyStr(firstName))
            isValid = false;
        if (!ValidationData.validEmptyStr(lastName))
            isValid = false;
        if (!ValidationData.validLocation(country, city, street, houseNumber, this))
            isValid = false;
        if (!ValidationData.validPhoneNumber(phoneNumber))
            isValid = false;
        if (!ValidationData.validPassword(password))
            isValid = false;
        if (!ValidationData.validEmail(email))
            isValid = false;

        if (!isValid)
            return false;

        user.setFirstName(firstName.getText().toString())
                .setLastName(lastName.getText().toString())
                .setEmail(email.getText().toString())
                .setAddress(country.getText().toString(), city.getText().toString(),
                        street.getText().toString(), Integer.parseInt(houseNumber.getText().toString()))
                .setPhoneNumber(phoneNumber.getText().toString());

        return true;
    }
}
