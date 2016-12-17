package com.yanclement.geophone.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yanclement.geophone.Constants;
import com.yanclement.geophone.R;

import static com.yanclement.geophone.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Location searchedPhoneLocation;
    private String searchedPhoneID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        searchedPhoneLocation = getIntent().getExtras().getParcelable(Constants.SEARCHED_PHONE_LOCATION);
        searchedPhoneID = getIntent().getExtras().getString(Constants.SEARCHED_PHONE_ID);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng searchedPhoneLatLng = new LatLng(searchedPhoneLocation.getLatitude(), searchedPhoneLocation.getLongitude());


        MarkerOptions searchedPhoneMarker = new MarkerOptions().position(searchedPhoneLatLng).title(searchedPhoneID);

        gMap.addMarker(searchedPhoneMarker).showInfoWindow();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedPhoneLatLng,16),2000,null);
    }
}
