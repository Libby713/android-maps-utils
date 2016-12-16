package com.google.maps.android.geojsonkmlabstraction;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Geometry {

    public final static String GEOMETRY_TYPE = "Point";

    public final LatLng mCoordinates;

    /**
     * Creates a new Point
     *
     * @param coordinates coordinate of Point to store
     */
    public Point(LatLng coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        mCoordinates = coordinates;
    }

    @Override
    public String getGeometryType() {
        return GEOMETRY_TYPE;
    }

    @Override
    public LatLng getGeometryObject() {
        return mCoordinates;
    }

    /**
     * Gets the coordinates of the GeoJsonPoint
     *
     * @return coordinates of the GeoJsonPoint
     */
    public LatLng getCoordinates() {
        return mCoordinates;
    }

}
