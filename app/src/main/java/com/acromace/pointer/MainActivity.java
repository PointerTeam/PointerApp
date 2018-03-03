package com.acromace.pointer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

// TODO: Add a button to recenter the map at your your location (see Maps app)
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private FloatingActionButton fab;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLocation = getCurrentLocation();
        // Create a marker in Waterloo
        // TODO: Separate out map marker creation to another function
        // TODO: Change the Google Maps pin to something that looks better
        // TODO: Add the points from Server.getPoints() to the map instead
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Waterloo, Ontario"));
        // Move the map's camera to the location
        // TODO: Also zoom into the camera on the map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    // getPointsResponse(success, points, errorMessage) puts points on the map, with success
    // indicating if the points have been successfully fetched. errorMessage if it is not a
    // success. If they have been successfully fetched, then points contains all of the points
    // in the requested area.
    void getPointsResponse(final boolean success, final ArrayList<Point> points, final String errorMessage) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.add_fab);

        setupFab();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_debug:
                Intent intent = new Intent(this, DebugActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupFab() {
        final MainActivity mainActivity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, CreatePointActivity.class);
                startActivity(intent);
            }
        });
    }

    private LatLng getCurrentLocation() {
        // TODO: Implement this to return the actual location
        return new LatLng(43.47, -80.54);
    }
}
