package com.acromace.pointer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements PingCallbackInterface {

    private static final String TAG = "MainActivity";

    private Server server = new Server();
    private Button pingButton;
    private TextView pingResponseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pingButton = findViewById(R.id.ping_button);
        pingResponseTextView = findViewById(R.id.ping_response_textview);

        setupPingButton();
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
