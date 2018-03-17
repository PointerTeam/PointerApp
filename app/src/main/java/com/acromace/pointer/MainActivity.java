package com.acromace.pointer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


// TODO: Separate out map marker creation to another function
// TODO: Refresh the list of Points when coming back from CreatePointActivity


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GetPointsCallbackInterface {

    private static final String TAG = "MainActivity";
    public static final int LOCATION_REQUEST = 1;
    private FloatingActionButton fab;
    private FloatingActionButton fabCurrLoc;
    private GoogleMap googleMap;
    private Server server = new Server();
    private static LatLng currentLocation; // Current location of the user
    private ArrayList<Point> points; // Points fetched from getPoints
    private boolean hasScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.add_fab);
        fabCurrLoc = findViewById(R.id.curr_loc);

        setupCreateMessageFab();
        setupFabCurrLoc();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // Add a marker at current location and move the map's camera to the same location.
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                hasScrolled = true;
            }
        });

        // Check GPS is enabled
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        // Check for permissions
        // TODO: After checking for permissions, don't start accessing the location until it's enabled
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted so request
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);
        }

        final MainActivity self = this;

        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //when the location changes, update the map by zooming to the location
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.i(TAG, "Location changed: " + latitude + " " + longitude);

                //Asking for the points at the location
                server.getPoints(latitude, longitude, self);

                LatLng loc = new LatLng(latitude, longitude);
                currentLocation = loc;

                updateMap();

                if(!hasScrolled) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Get Coordinates
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, ll);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    // getPointsResponse(success, points, errorMessage) puts points on the map, with success
    // indicating if the points have been successfully fetched. errorMessage if it is not a
    // success. If they have been successfully fetched, then points contains all of the points
    // in the requested area.
    public void getPointsResponse(final boolean success, final ArrayList<Point> points, final String errorMessage) {
        final MainActivity self = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                self.points = points;
                if (googleMap == null ) {
                    Log.w(TAG, "Points loaded but map has not");
                    return;
                }
                updateMap();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // This is used to add the debug button (see onOptionsItemSelected)
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This adds the debug button to the action bar at the top right
            case R.id.action_debug:
                Intent intent = new Intent(this, DebugActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Setup the create message fab
    private void setupCreateMessageFab() {
        final MainActivity mainActivity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, CreatePointActivity.class);
                intent.putExtra("currLoc", currentLocation); //transfer currLoc to CreatePoint
                startActivity(intent);
            }
        });
    }

    // Setup the current location recentering fab
    private void setupFabCurrLoc() {
        fabCurrLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centreMap();
            }
        });
    }

    private void updateMap() {
        // Remove all points from the map and re-add them all
        //
        // This function should just be called when we receive new points (i.e. getPointsResponse)
        // and when our location is updated (i.e. onLocationChanged)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearPointsFromMap();

                if (points == null) return;

                // TODO: Change the Google Maps pin to something that looks better
                //Other Markers
                for (int i = 0; i < points.size(); i++)
                {
                    googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fire))
                            .position(points.get(i).getPosition())
                            .title(points.get(i).getMessage()));
                }
            }
        });
    }

    private void clearPointsFromMap() {
        // Remove all the points from the map
        if (googleMap != null) {
            googleMap.clear();
        }
    }

    private void centreMap() {
        // Centres map at current location
        hasScrolled = false;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
    }
}
