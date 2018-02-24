package com.acromace.pointer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DebugActivity extends AppCompatActivity implements PingCallbackInterface {

    private static final String TAG = "DebugActivity";

    private final Server server = new Server();
    private Button pingButton;
    private TextView pingResponseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pingButton = findViewById(R.id.ping_button);
        pingResponseTextView = findViewById(R.id.ping_response_textview);

        setupPingButton();
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

    private void makePingRequest() {
        pingResponseTextView.setText("Making request to server...");
        server.makePingRequest(this);
    }

    @Override
    public void pingResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pingResponseTextView.setText(response);
            }
        });
    }
}
