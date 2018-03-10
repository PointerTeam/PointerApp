package com.acromace.pointer;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.StaticCluster;

/**
 * Created by Nick on 2018-03-10.
 * Thingy that does the clustering of points on the map so its more readable!
 * https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
 */

public class MyClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    public MyClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = "";
        mSnippet = "";
    }

    public MyClusterItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}