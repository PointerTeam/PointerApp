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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button pingButton;
    private TextView pingResponseTextView;
    private String pingOutputText; // This is terrible, will refactor

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
        Log.v(TAG, "Ping!");
        pingResponseTextView.setText("Making request to server...");
        final MainActivity mainActivity = this;
        String outputText;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // This magic URL is used to connect to "localhost" on your computer
                    // instead of the "localhost" on the emulator
                    final URL pingEndpoint = new URL("http://10.0.2.2:5000/ping");
                    final HttpURLConnection myConnection =
                            (HttpURLConnection) pingEndpoint.openConnection();
                    final int responseCode =myConnection.getResponseCode();
                    if (responseCode != 200) {
                        Log.w(TAG, String.format("Server returned status code: %d", responseCode));
                        pingOutputText = String.format("Server returned status code: %d", responseCode);
                    }

                    final InputStream inputStream = myConnection.getInputStream();
                    final Scanner scanner = new Scanner(inputStream, "UTF-8");
                    final String response = scanner.next();
                    Log.d(TAG, response);
                    pingOutputText = response;
                } catch (MalformedURLException e) {
                    Log.e(TAG, "URL provided was malformed");
                    pingOutputText = e.getLocalizedMessage();
                } catch (java.io.IOException e) {
                    Log.e(TAG, "Error while opening connection to the server");
                    pingOutputText = e.getLocalizedMessage();
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pingResponseTextView.setText(pingOutputText);
                    }
                });
            }
        });
    }
}
