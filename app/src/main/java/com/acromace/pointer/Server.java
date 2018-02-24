package com.acromace.pointer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by acromace on 2018-02-17.
 */

interface PingCallbackInterface {
    void pingResponse(final String response);
}

interface CreatePointCallbackInterface {
    // Success is true if the point was successfully created, false if not
    // On error, sets errorMessage
    void createPointResponse(final boolean success, final String errorMessage);
}

interface GetPointsCallbackInterface {
    // Success if points successfully fetched, false if not
    // On success, points is filled with Points from around the requested location
    // On error, sets errorMessage, points is empty
    void getPointsResponse(final boolean success, final ArrayList<Point> points, final String errorMessage);
}

public class Server {

    private static final String TAG = "Server";
    private static final String SERVER_LOCALHOST = "http://10.0.2.2:5000/";
    private static final String SERVER_REMOTE = "http://acromace.pythonanywhere.com/";

    private static final String SERVER = SERVER_REMOTE; // Change this to SERVER_LOCALHOST to test locally

    void createPoint(final Point point, final CreatePointCallbackInterface callback) {
        Log.d(TAG, "Creating point: " + point.toString());
        Log.d(TAG, "Creating points is not implemented, returning success");
        callback.createPointResponse(true, null);
        //callback.createPointResponse(false, "Error: failed to send message");
    }

    void getPoints(final long latitude, final long longitude, final GetPointsCallbackInterface callback) {
        Log.d(TAG, "Getting points at " + latitude + ", " + longitude);
        Log.d(TAG, "Getting points is not implemented, returning fake points");
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(43.472113, -80.543936, "I am a fake point #1"));
        points.add(new Point(43.471772, -80.545337, "I am a fake point #2"));
        callback.getPointsResponse(true, points, null);
    }

    void makePingRequest(final PingCallbackInterface callback) {
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
