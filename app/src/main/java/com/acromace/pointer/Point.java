package com.acromace.pointer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by acromace on 2018-02-17.
 */

public class Point implements ClusterItem {

    final private LatLng position;
    final private String message;
    private Bitmap image = null;

    Point(final double latitude, final double longitude, final String message) {
        this.position = new LatLng(latitude, longitude);
        this.message = message;
    }

    Point(final LatLng position, final String message) {
        this.position = position;
        this.message = message;
    }

    Bitmap getImage() {
        return image;
    }

    void setImage(Bitmap b) {
        image = b;
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
        if (image != null) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            json.put("image",encodedImage);
        }
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
