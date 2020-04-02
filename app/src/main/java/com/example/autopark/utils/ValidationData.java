package com.example.autopark.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.widget.EditText;

import com.example.autopark.map.MapsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidationData {

    public static boolean validEmptyStr(EditText text) {
        return validEmptyStr(text, "Please fill this section");
    }

    public static boolean validEmptyStr(EditText text, String err) {
        if (text.getText().toString().isEmpty()) {
            text.setError(err);
            return false;
        }

        return true;
    }

    public static boolean validLocation(EditText country, EditText city, EditText street,
                                        EditText houseNumber, Context context) {
        boolean isValid = true;
        if (!validEmptyStr(country))
            isValid = false;
        if (!validEmptyStr(city))
            isValid = false;
        if (!validEmptyStr(street))
            isValid = false;
        if (!validEmptyStr(houseNumber))
            isValid = false;

        if (!isValid)
            return false;

        String addressStr = getStr(country.getText(), city.getText(), street.getText(), houseNumber.getText());
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(addressStr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.size() == 0)
        {
            city.setError("The address you entered is incorrect");
            return false;
        }

        return true;
    }

    public static boolean validPhoneNumber(EditText phoneNumber) {
        final String phonePattern = "^(?=(?:[0]){1})(?=[0-9]{9}).*";
        if (!phoneNumber.getText().toString().matches(phonePattern)) {
            phoneNumber.setError("Phone number doesn't match conditions");
            return false;
        }

        return true;
    }

    public static boolean validPassword(EditText password) {
        final String passPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (!password.getText().toString().matches(passPattern)) {
            password.setError("Password doesn't match conditions");
            return false;
        }

        return true;
    }

    public static boolean validEmail(EditText email) {
        final String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (!email.getText().toString().matches(emailPattern)) {
            email.setError("Email doesn't match conditions");
            return false;
        }

        return true;
    }

    private static String getStr(Editable ... args) {
        String res = "";
        for (Editable edit : args)
            res += edit.toString() + " ";

        return res;
    }

}
