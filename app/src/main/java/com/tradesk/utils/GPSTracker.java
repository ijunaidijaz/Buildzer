package com.tradesk.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GPSTracker implements LocationListener {
    private final LocationManager locationManager;

    private Location loc;
    private Double latitude, longitude;
    private final Context context;


    @Inject
    public GPSTracker(Context context) {
        this.context = context;

        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);

        checkGps();

    }

    public void checkGps() {
        // getting GPS statusb
        Boolean checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        Boolean checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (!checkGPS && !checkNetwork) {


            Toast.makeText(context, "No Service Provider Available", Toast.LENGTH_SHORT).show();
        } else {

            // First get location from Network Provider
            if (checkNetwork) {
                // Toast.makeText(getActivity(), "Network", Toast.LENGTH_SHORT).show();

                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000, 0, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {

                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }


                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        // if GPS Enabled get lat/long using GPS Services
        if (checkGPS) {
            //Toast.makeText(getActivity(), "GPS", Toast.LENGTH_SHORT).show();
            if (loc == null) {
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000, 0, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {

                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_COARSE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public boolean checkGpsStatus() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /**
     * Function to get latitude
     */
    public Double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public Double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }

        // return longitude
        return longitude;
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


}