package com.acromace.pointer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by acromace on 2018-02-17.
 */

interface PingCallbackInterface {
    void pingResponse(final String response);
}

public class Server {

    private static final String TAG = "Server";
    private static final String SERVER_LOCALHOST = "http://10.0.2.2:5000/";
    private static final String SERVER_REMOTE = "http://acromace.pythonanywhere.com/";

    private static final String SERVER = SERVER_REMOTE; // Change this to SERVER_LOCALHOST to test locally

    public void makePingRequest(final PingCallbackInterface callback) {
        Log.v(TAG, "Ping!");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
            String pingOutputText;
            try {
                // This magic URL is used to connect to "localhost" on your computer
                // instead of the "localhost" on the emulator
                final URL pingEndpoint = new URL(SERVER + "ping");
                final HttpURLConnection myConnection =
                        (HttpURLConnection) pingEndpoint.openConnection();
                final int responseCode =myConnection.getResponseCode();
                if (responseCode != 200) {
                    Log.w(TAG, String.format("Server returned status code: %d", responseCode));
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
            callback.pingResponse(pingOutputText);
            }
        });
    }
}
