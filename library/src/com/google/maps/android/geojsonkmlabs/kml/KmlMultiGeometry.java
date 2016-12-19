package com.google.maps.android.geojsonkmlabs.kml;

import com.google.maps.android.geojsonkmlabs.Geometry;

import java.util.ArrayList;

/**
 * Represents a KML MultiGeometry. Contains an array of KmlGeometry objects.
 */
public class KmlMultiGeometry implements Geometry<ArrayList<Geometry>> {

    private static final String GEOMETRY_TYPE = "MultiGeometry";

    private ArrayList<Geometry> mGeometries = new ArrayList<Geometry>();

    /**
     * Creates a new KmlMultiGeometry object
     *
     * @param geometries array of KmlGeometry objects contained in the MultiGeometry
     */
    public KmlMultiGeometry(ArrayList<Geometry> geometries) {
        if (geometries == null) {
            throw new IllegalArgumentException("Geometries cannot be null");
        }
        mGeometries = geometries;
    }

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    @Override
    public String getGeometryType() {
        return GEOMETRY_TYPE;
    }

    /**
     * Gets an ArrayList of KmlGeometry objects
     *
     * @return ArrayList of KmlGeometry objects
     */

    public ArrayList<Geometry> getGeometryObject() {
        return mGeometries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(GEOMETRY_TYPE).append("{");
        sb.append("\n geometries=").append(mGeometries);
        sb.append("\n}\n");
        return sb.toString();
    }
}
