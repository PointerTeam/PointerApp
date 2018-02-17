package com.acromace.pointer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements PingCallbackInterface {

    private static final String TAG = "MainActivity";

    private Server server = new Server();
    private Button pingButton;
    private TextView pingResponseTextView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pingButton = findViewById(R.id.ping_button);
        pingResponseTextView = findViewById(R.id.ping_response_textview);
        fab = findViewById(R.id.add_fab);

        setupFab();
        setupPingButton();
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

    private void setupPingButton() {
        if (pingButton == null) {
            Log.e(TAG, "Could not find the ping button");
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
