package com.satyamapp.teamtekken;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GPS_Service extends Service
{

    private LocationListener listener;
    private LocationManager locationManager;
    private DatabaseReference mDatabase,mDatabase2;
    Double lat;
    Double lng;
    int x = 1;

    private FirebaseUser mAuth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.d("MainActivity", String.valueOf(lat) + " " +String.valueOf(lng));
                updatelocaton(lat,lng);
                currentlocation(lat,lng);

                //Intent i = new Intent("location_update");
                //i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude());
                //sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }

    private void updatelocaton(Double lat, Double lng)
    {
        final String uid = mAuth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users Lists");


        if(x <=100) {
            mDatabase.child(uid).child("tekken").child("" + x).child("lat").setValue(lat);
            mDatabase.child(uid).child("tekken").child("" + x).child("lng").setValue(lng);

            x++;
        }

        else {
            x = 0;
        }
    }

    private void currentlocation(Double lat, Double lng) {

            mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Current Location");

            GeoFire geoFire = new GeoFire(mDatabase2);
            //geoFire.setLocation(uid, new GeoLocation(lat, lng));

            //GeoFire geoFire = new GeoFire(mDatabase2.getReference().child("geofire_location"));

            String key = geoFire.getDatabaseReference().push().getKey();
            geoFire.setLocation(key, new GeoLocation(lat, lng), new
                    GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //Do some stuff if you want to
                        }
                    });

    }

}