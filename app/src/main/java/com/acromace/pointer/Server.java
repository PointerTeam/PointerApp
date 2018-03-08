package com.acromace.pointer;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String SERVER = SERVER_REMOTE; // Change this to SERVER_LOCALHOST to test locally

    void createPoint(final Point point, final CreatePointCallbackInterface callback) {
        // TODO: Implement making the network call
        Log.d(TAG, "Creating point: " + point.toString());
        Log.d(TAG, "Creating points is not implemented, returning success");
        callback.createPointResponse(true, null);
        //callback.createPointResponse(false, "Error: failed to send message");
    }

    void getPoints(final double latitude, final double longitude, final GetPointsCallbackInterface callback) {
        // TODO: Implement making the network call
        Log.d(TAG, "Getting points at " + latitude + ", " + longitude);
        final ArrayList<Point> points = new ArrayList<Point>();
        AsyncTask.execute(new Runnable() {
            public void run() {
                String getspoint;
                try {
                    // This magic URL is used to connect to "localhost" on your computer
                    // instead of the "localhost" on the emulator
                    // lat=123,long=456
                    final URL getEndpoint = new URL(SERVER + "messages?lat=" + latitude + ",long=" + longitude);
                    final HttpURLConnection myConnection =
                            (HttpURLConnection) getEndpoint.openConnection();
                    try {
                        InputStream in = myConnection.getInputStream();
//                        URL(HttpURLConnection.getInputStream()) = latitude + longitude;
                        ;

                    } finally {
                        myConnection.disconnect();
                    }
                    final int responseCode = myConnection.getResponseCode();
                    if (responseCode != 200) {
                        Log.w(TAG, String.format("Server returned status code: %d", responseCode));
                    }

                     /* public Message readMessage(JsonReader reader) throws IOException {
                    long id = -1;
                    String text = null;
                    User user = null;
                    List<Double> geo = null;

                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("id")) {
                            id = reader.nextLong();
                        } else if (name.equals("text")) {
                            text = reader.nextString();
                        } else if (name.equals("geo") && reader.peek() != JsonToken.NULL) {
                            geo = readDoublesArray(reader);
                        } else if (name.equals("user")) {
                            user = readUser(reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    return new Message(id, text, user, geo);
                }

                  Scanner sc = new Scanner(System.in); */


                    final InputStream inputStream = myConnection.getInputStream();
                    final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z");
                    final String response = scanner.next();
                    // [{"message": "Hello world!", "location": {"lat": 43.472113, "long": -80.543936}}, {"message": "thank mr goose", "location": {"lat": 43.471772, "long": -80.545337}}]

                    final JSONArray token = new JSONArray(response);
                    for(int i = 0 ; i < token.length() ; i++) {
                        // {"message": "Hello world!", "location": {"lat": 43.472113, "long": -80.543936}}
                        final JSONObject json = token.getJSONObject(i);
                        final String messages = json.getString("message");
                        final JSONObject location = json.getJSONObject("location");
                        final double lat = location.getDouble("lat");
                        final double lon = location.getDouble("long");
                        Point newpoint = new Point(lat, lon, messages);
                        points.add(newpoint);
                    }
                    System.out.println(response);
                    Log.d(TAG, response);
                    callback.getPointsResponse(true, points, null);
                    /*final String message = response;
                    double latitude = scanner.next();
                    double longitude = scanner.next();*/
                   getspoint = response;
                } catch (MalformedURLException e) {
                    Log.e(TAG, "URL provided was malformed");
                    getspoint = e.getLocalizedMessage();
                } catch (java.io.IOException e) {
                    Log.e(TAG, "Error while opening connection to the server");
                    getspoint = e.getLocalizedMessage();
                } catch (JSONException token) {
                    Log.e(TAG, "JSON Exception error");
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
