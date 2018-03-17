package com.acromace.pointer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by acromace on 2018-02-17.
 */

public class Point implements ClusterItem {

    final private LatLng position;
    final private String message;

    Point(final double latitude, final double longitude, final String message) {
        this.position = new LatLng(latitude, longitude);
        this.message = message;
    }

    Point(final LatLng position, final String message) {
        this.position = position;
        this.message = message;
    }

    String getMessage() {
        return this.message;
    }

    public String toString() {
        return "Point[ Location: " + this.position.toString() + ", Message: " + this.message + " ]";
    }

    JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lat", position.latitude);
        json.put("lon", position.longitude);
        json.put("message", message);
        return json;
    }
    @Override
    public LatLng getPosition() {
        return this.position;
    }

    @Override
    public String getTitle() {
        return this.message;
    }

    @Override
    public String getSnippet() {
        return "";
    }
}
