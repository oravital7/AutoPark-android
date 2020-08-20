package com.example.autopark.park;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.TestLooperManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.autopark.R;
import com.example.autopark.model.Parking;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class ParkingInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;

    public ParkingInfoWindowAdapter(Context context) {
        this.mContext = context;
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.parking_info_window , null);
    }


    private void rendowWindowsText(Marker marker , View view){
        ImageView imageView = view.findViewById(R.id.image);
        Drawable imageMarker=null;
        String title = marker.getTitle();
        TextView address = (TextView) view.findViewById(R.id.title);
        if(!title.equals("")){
            address.setText(title);
        }
        Log.d("stringImage" , title);
        if(marker.getSnippet() != null) {
            String snippet = marker.getSnippet();
            byte[] decodedString = Base64.decode(snippet, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageMarker = new BitmapDrawable(mContext.getResources(), decodedImage);
            TextView tvSnippet = (TextView) view.findViewById(R.id.snippet);

            if (!snippet.equals("")) {
                imageView.setImageBitmap((((BitmapDrawable) imageMarker).getBitmap()));
            }
        }
        else{
            imageView.setImageResource(R.drawable.send_location);
        }
    }
    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowsText(marker , mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowsText(marker , mWindow);
        return mWindow;
    }
}
