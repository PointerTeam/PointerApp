package com.acromace.pointer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class CreatePointActivity extends AppCompatActivity implements CreatePointCallbackInterface {

    Server server = new Server();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_point);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the point when the user presses the button
        final CreatePointActivity self = this;
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.createPoint(new Point(getCurrentLocation(), getEnteredMessage()), self);
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

    private LatLng getCurrentLocation() {
        // TODO: Change this to get the actual location (see findViewById)
        return new LatLng(43.47, -80.54);
    }

    private String getEnteredMessage() {
        // TODO: Change this to get the message from the text box (see findViewById)
        return "Hello";
    }
}
