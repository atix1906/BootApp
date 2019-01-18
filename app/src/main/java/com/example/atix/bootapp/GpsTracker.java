package com.example.atix.bootapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import static android.content.Context.LOCATION_SERVICE;


public class GpsTracker implements LocationListener {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    Context context;
    Activity activity;
    private String TAG = "GpsTracker";

    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    public GpsTracker(Context context,Activity activity) {
        super();
        this.context = context;
        this.activity = activity;
    }

    public Location getLocation(){
        int tmp = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        Log.e(TAG, "FineLocation: "+Integer.toString(tmp));
        //tmp = PackageManager.PERMISSION_GRANTED;
        //Log.e(TAG, "FineLocation: "+Integer.toString(tmp));

        tmp = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.e(TAG, "CoarseLocation: "+Integer.toString(tmp));

        if (ActivityCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.e(TAG,"Grant FineLocation access");
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION )){
                Log.e(TAG,"FineLocation granted?");
            }
            else{
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                tmp = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
                Log.e(TAG,"FineLocation granted: " + tmp);
            }
        }

        if( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            Log.e(TAG,"Grant CoarseLocation access");
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){
                Log.e(TAG,"CoarseLocation granted?");
            }
            else{
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                tmp = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                Log.e(TAG,"CoarseLocation granted: " +tmp);
            }
        }
        try {
            LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,10,this);
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                return loc;
            }else{
                Log.e(TAG,"error2");
            }
        }catch (Exception e){
            e.printStackTrace();
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
/*
    private boolean isSameProvider(String provider1, String provider2){
        if(provider1 == null){
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }*/

}