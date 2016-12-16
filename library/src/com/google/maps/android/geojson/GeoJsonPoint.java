package com.google.maps.android.geojson;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojsonkmlabstraction.Point;

/**
 * A GeoJsonPoint geometry contains a single {@link com.google.android.gms.maps.model.LatLng}.
 */
public class GeoJsonPoint extends Point {

    /**
     * Creates a new GeoJsonPoint
     *
     * @param coordinates coordinates of GeoJsonPoint to store
     */
    public GeoJsonPoint(LatLng coordinates) {
        super(coordinates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.GEOMETRY_TYPE).append("{");
        sb.append("\n coordinates=").append(super.mCoordinates);
        sb.append("\n}\n");
        return sb.toString();
    }
}
