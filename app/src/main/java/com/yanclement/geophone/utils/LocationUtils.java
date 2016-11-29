package com.yanclement.geophone.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by YPierru on 22/11/2016.
 */

public class LocationUtils {

    private LocationManager locationManager;


    public LocationUtils(Context context){
        locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
    }

    public Location getLastLocation() throws SecurityException{

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
}
