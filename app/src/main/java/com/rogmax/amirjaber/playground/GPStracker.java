package com.rogmax.amirjaber.playground;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Amir Jaber on 1/29/2018.
 */

public class GPStracker implements LocationListener {

    private Context context;

    GPStracker(Context c) {
        context = c;
    }

    Location getLocation() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showShortToast("Permission not granted");
            return null;
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
            return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            showShortToast("Please enable GPS");
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void showShortToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
