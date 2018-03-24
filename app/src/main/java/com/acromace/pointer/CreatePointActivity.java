package com.acromace.pointer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreatePointActivity extends AppCompatActivity implements CreatePointCallbackInterface {

    Server server = new Server();
    private static final String TAG = "CreatePointActivity";
    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_point);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get currLoc transferred from Main
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentLocation = (LatLng) extras.get("currLoc");
        }

        // Create the point when the user presses the button
        final CreatePointActivity self = this;
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.createPoint(new Point(currentLocation, getEnteredMessage()), self);
            }
        });

        // Add the back button
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void createPointResponse(final boolean success, final String errorMessage)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    // Exit page
                    finish();
                }
                else {
                    // Show user errorMessage
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, errorMessage, duration);
                    toast.show();
                }
            }
        });
    }

    private String getEnteredMessage() {
        EditText text = findViewById(R.id.edit);
        Log.i(TAG, "TextBox: " + text.getText().toString());
        return text.getText().toString();
    }
}
