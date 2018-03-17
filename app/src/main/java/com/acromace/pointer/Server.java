package com.acromace.pointer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    private static final String SERVER = SERVER_REMOTE ; // Change this to SERVER_LOCALHOST to test locally
    private java.lang.String msg;

    void createPoint(final Point point, final CreatePointCallbackInterface callback) {
        Log.d(TAG, "Creating point " + point.toString());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL createEndpoint = new URL(SERVER + "messages");
                    final HttpURLConnection myConnection =
                            (HttpURLConnection) createEndpoint.openConnection();
                    myConnection.setDoOutput(true);
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setRequestProperty("Accept", "application/json");
                    myConnection.setRequestMethod("POST");
                    myConnection.connect();

                    // Send the JSON to the server
                    OutputStream os = myConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
                    outputStreamWriter.write(point.toJSON().toString());
                    outputStreamWriter.flush();
                    os.close();

                    // Handle error
                    final int responseCode = myConnection.getResponseCode();
                    if (responseCode != 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(myConnection.getErrorStream()));
                        Log.w(TAG, String.format("Server returned status code: %d", responseCode));
                        String line;
                        while ((line = br.readLine()) != null) {
                            Log.e(TAG, line);
                        }
                        callback.createPointResponse(false, "Server responded with failure: " + responseCode);
                        return;
                    }

                    myConnection.disconnect();

                    // Get the response from the server
                    final InputStream inputStream = myConnection.getInputStream();
                    final Scanner scanner = new Scanner(inputStream, "UTF-8");
                    final String response = scanner.next();
                    Log.d(TAG, response);
                    callback.createPointResponse(true,null);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "URL provided was malformed");
                    callback.createPointResponse(false, e.getLocalizedMessage());
                } catch (java.io.IOException e) {
                    Log.e(TAG, "Error while opening connection to the server");
                    callback.createPointResponse(false, e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e(TAG,"Error in creating JSON Object");
                    callback.createPointResponse(false, e.getLocalizedMessage());
                }
            }
        });
    }

    void getPoints(final double latitude, final double longitude, final GetPointsCallbackInterface callback) {
        Log.d(TAG, "Getting points at " + latitude + ", " + longitude);
        final ArrayList<Point> points = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    final URL getEndpoint = new URL(SERVER + "messages?lat=" + latitude + "&lon=" + longitude);
                    final HttpURLConnection myConnection =
                            (HttpURLConnection) getEndpoint.openConnection();
                    final int responseCode = myConnection.getResponseCode();
                    if (responseCode != 200) {
                        Log.w(TAG, String.format("Server returned status code: %d", responseCode));
                    }
                    final InputStream inputStream = myConnection.getInputStream();
                    final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z");
                    final String response = scanner.next();
                    final JSONArray token = new JSONArray(response);
                    for(int i = 0 ; i < token.length() ; i++) {
                        final JSONObject json = token.getJSONObject(i);
                        final String messages = json.getString("message");
                        final JSONObject location = json.getJSONObject("location");
                        final double lat = location.getDouble("lat");
                        final double lon = location.getDouble("lon");
                        Point point= new Point(lat, lon, messages);
                        points.add(point);
                    }
                    myConnection.disconnect();
                    Log.d(TAG, response);
                    callback.getPointsResponse(true, points, null);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "URL provided was malformed");
                    callback.getPointsResponse(false,null,  e.getLocalizedMessage());
                } catch (java.io.IOException e) {
                    Log.e(TAG, "Error while opening connection to the server");
                    callback.getPointsResponse(false,null,  e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception error");
                    callback.getPointsResponse(false,null,  e.getLocalizedMessage());
                }
            }
        });
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
                final int responseCode = myConnection.getResponseCode();
                if (responseCode != 200) {
                    Log.w(TAG, String.format("Server returned status code: %d", responseCode));
                }

                final InputStream inputStream = myConnection.getInputStream();
                final Scanner scanner = new Scanner(inputStream, "UTF-8");
                final String response = scanner.next();
                Log.d(TAG, response);
                myConnection.disconnect();
                callback.pingResponse(response);
            } catch (MalformedURLException e) {
                Log.e(TAG, "URL provided was malformed");
                callback.pingResponse(e.getLocalizedMessage());
            } catch (java.io.IOException e) {
                Log.e(TAG, "Error while opening connection to the server");
                callback.pingResponse(e.getLocalizedMessage());
            }
            }
        });
    }

}
