package com.acromace.pointer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    private static final String SERVER = SERVER_LOCALHOST; // Change this to SERVER_LOCALHOST to test locally

    void createPoint(final Point point, final CreatePointCallbackInterface callback) {
        // TODO: Implement making the network call
        Log.v(TAG, "Ping!");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String pingOutputText;
                try {
                    // This magic URL is used to connect to "localhost" on your computer
                    // instead of the "localhost" on the emulator
                    final URL createEndpoint = new URL(SERVER + "messages");
                    final HttpURLConnection myConnection =
                            (HttpURLConnection) createEndpoint.openConnection();
                    myConnection.setDoOutput(true);
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setRequestProperty("Accept", "application/json");
                    myConnection.setRequestMethod("POST");
                    myConnection.connect();

                    LatLng position = point.getPosition();
                    byte[] outputBytes = ("{\"lat\": "+ position.latitude +", \"lon\": "+ position.longitude+"  , \"message\": " + point.getMessage() + "}").getBytes("UTF-8");
                    OutputStream os = myConnection.getOutputStream();
                    os.write(outputBytes);
                    os.close();

                    final int responseCode =myConnection.getResponseCode();
                                    switch(responseCode) {
                        case 200: //all ok
                            break;
                        case 401:
                        case 403:
                            // authorized
                            break;
                        default:
                            //whatever else...
                            String httpResponse = myConnection.getResponseMessage();
                            BufferedReader br = new BufferedReader(new InputStreamReader(myConnection.getErrorStream()));
                            Log.w(TAG, String.format("Server returned status code: %d", responseCode));
                            String line;
                            try {
                                while ((line = br.readLine()) != null) {
                                    Log.d("error", "    " + line);
                                }
                            } catch (Exception ex) {
                                //nothing to do here
                            }

                            break;
                    }
                    myConnection.disconnect();

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
                //callback.pingResponse(pingOutputText);
            }
        });
    }

    void getPoints(final double latitude, final double longitude, final GetPointsCallbackInterface callback) {
        // TODO: Implement making the network call
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
