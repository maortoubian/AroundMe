package com.aroundme;

import android.annotation.TargetApi;
import android.app.Activity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Gmap extends FragmentActivity {

    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);
        setUpMapIfNeeded();
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    private void setUpMap(){
        if (mMap != null){
            Log.d("mytag", "googlemap is null, making it available");
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.mapView)).getMap();

            Log.d("mytag", "googlemap is not null");
            mMap.setMyLocationEnabled(true);
            //provider = lm.getBestProvider(new Criteria(), true);



        }
    }
}
