package com.acromace.pointer;

/**
 * Created by acromace on 2018-02-17.
 */

public class Point {

    final private double latitude;
    final private double longitude; // We can't name this "long" since that's a type
    final private String message;

    public Point(double latitude, double longitude, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }

    public String toString() {
        return "Point[ Location: (" + this.latitude + ", " + this.longitude
                + "), Message: " + this.message + " ]";
    }

}
