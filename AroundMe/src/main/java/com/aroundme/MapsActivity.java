package com.aroundme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMeCollection;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements LocationListener,ConnectionCallbacks {

    //private float lat= (float) 32.44343,lon= (float) 34.34324;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    double lat;
    double lon;
    String email;

    GoogleApiClient mApiClient ;
    LocationRequest mLocationRequest;
    LocationManager mLocManager;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_2);

        mApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        email = sharedPref.getString("email", "");

        setUpMapIfNeeded();

    }

    private void sendMeLocation(String mail) {
        try {
            Aroundmeapi api = EndpointApiCreator.getApi(Aroundmeapi.class);

            GeoPt GTP = new GeoPt();

            LocationManager mLocManager;
            mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            String provider = mLocManager.getBestProvider(criteria, true);

            Location location = mLocManager.getLastKnownLocation(provider);

            GTP.setLatitude((float) location.getLatitude());
            GTP.setLongitude((float) location.getLongitude());

            api.reportUserLocation(mail, GTP).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



        @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                try {
                    setUpMap();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() throws IOException {

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = mLocManager.getBestProvider(criteria, true);

        loc = mLocManager.getLastKnownLocation(provider);

        if(loc!=null) {

            lat = loc.getLatitude();
            lon = loc.getLongitude();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 20));
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));

            mMap.moveCamera(center);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        }

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                sendMeLocation(email);
                showAllUsers(email);
                return null;
            }

        }.execute();

    }

    private void showAllUsers(String mail) {

        try {

            Bitmap bMapMe = BitmapFactory.decodeResource(getResources(), R.drawable.me);
            final Bitmap bMapScaledMe = Bitmap.createScaledBitmap(bMapMe, 70, 70, true);

            final ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

            Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec0);
            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec1);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec2);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec3);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec4);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec5);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec6);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec7);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);

            Aroundmeapi api = EndpointApiCreator.getApi(Aroundmeapi.class);
            UserAroundMeCollection a;

            a = api.getUsersAroundMe((float) lat, (float) lon, 1000000, mail).execute();

            List<UserAroundMe> usersList = a.getItems();

            int size = usersList.size();

            for (int i=0 ; i<size ; i++) {

                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                final String user_email = sharedPref.getString("email", "");

                final Float lat = usersList.get(i).getLocation().getLatitude();
                final Float lng = usersList.get(i).getLocation().getLongitude();
                final String name = usersList.get(i).getDisplayName();
                final String email = usersList.get(i).getMail();

                    runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if((email.equals(user_email))) {
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                            .icon(BitmapDescriptorFactory.fromBitmap(bMapScaledMe))).setTitle("ME");
                                }
                                else {
                                    Random r = new Random();
                                    int i = r.nextInt(7 - 1) + 0;
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapArray.get(i)))).setTitle(name);
                                }
                            }
                    });

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
            loc=location;
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
