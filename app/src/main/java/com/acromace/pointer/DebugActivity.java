package com.acromace.pointer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class DebugActivity extends AppCompatActivity implements PingCallbackInterface, CreatePointCallbackInterface, GetPointsCallbackInterface {

    private static final String TAG = "DebugActivity";

    private final Server server = new Server();
    private TextView serverResponseTextView;
    private Button pingButton;
    private Button createPointButton;
    private Button getPointsButton;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button

        serverResponseTextView = findViewById(R.id.server_response_textview);
        pingButton = findViewById(R.id.ping_button);
        createPointButton = findViewById(R.id.create_point_button);
        getPointsButton = findViewById(R.id.get_points_button);
        latitudeEditText = findViewById(R.id.latitude_edittext);
        longitudeEditText = findViewById(R.id.longitude_edittext);
        messageEditText = findViewById(R.id.message_edittext);

        setupPingButton();
        setupCreatePointButton();
        setupGetPointsButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupPingButton() {
        if (pingButton == null) {
            Log.e(TAG, "Could not find ping button");
            return;
        }

        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePingRequest();
            }
        });
    }

    private void setupCreatePointButton() {
        if (createPointButton == null) {
            Log.e(TAG, "Could not find create point button");
            return;
        }

        createPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCreatePointRequest();
            }
        });
    }

    private void setupGetPointsButton() {
        if (getPointsButton == null) {
            Log.e(TAG, "Could not find the get points button");
            return;
        }

        getPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeGetPointsRequest();
            }
        });
    }

    private void makePingRequest() {
        serverResponseTextView.setText(R.string.server_request_placeholder);
        server.makePingRequest(this);
    }

    private void makeCreatePointRequest() {
        serverResponseTextView.setText(R.string.server_request_placeholder);
        server.createPoint(new Point(getLatitude(), getLongitude(), getMessage()), this);
    }

    private void makeGetPointsRequest() {
        serverResponseTextView.setText(R.string.server_request_placeholder);
        server.getPoints(getLatitude(), getLongitude(), this);
    }

    @Override
    public void pingResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverResponseTextView.setText(response);
            }
        });
    }

    @Override
    public void createPointResponse(final boolean success, final String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverResponseTextView.setText("Success: " + Boolean.toString(success) + ", error: " + errorMessage);
            }
        });
    }

    @Override
    public void getPointsResponse(final boolean success, final ArrayList<Point> points, final String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String response = "Success: " + Boolean.toString(success) + "\n";
                for (Point point: points) {
                    response += point.toString() + "\n";
                }
                response += "error: " + errorMessage;
                serverResponseTextView.setText(response);
            }
        });
    }

    private long getLatitude() {
        return Long.valueOf(latitudeEditText.getText().toString());
    }

    private long getLongitude() {
        return Long.valueOf(longitudeEditText.getText().toString());
    }

    private String getMessage() {
        return messageEditText.getText().toString();
    }
}
