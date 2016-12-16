package com.google.maps.android.geojson;

import com.google.maps.android.geojsonkmlabstraction.Geometry;

import java.util.List;

/**
 * A GeoJsonGeometryCollection geometry contains a number of GeoJsonGeometry objects.
 */
public class GeoJsonGeometryCollection implements GeoJsonGeometry {

    private final static String GEOMETRY_TYPE = "GeometryCollection";

    private final List<Geometry> mGeometries;

    /**
     * Creates a new GeoJsonGeometryCollection object
     *
     * @param geometries array of GeoJsonGeometry objects to add to the GeoJsonGeometryCollection
     */
    public GeoJsonGeometryCollection(
            List<Geometry> geometries) {
        if (geometries == null) {
            throw new IllegalArgumentException("Geometries cannot be null");
        }
        mGeometries = geometries;
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return GEOMETRY_TYPE;
    }

    /**
     * Gets the stored GeoJsonGeometry objects
     *
     * @return stored GeoJsonGeometry objects
     */
    public List<Geometry> getGeometries() {
        return mGeometries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(GEOMETRY_TYPE).append("{");
        sb.append("\n Geometries=").append(mGeometries);
        sb.append("\n}\n");
        return sb.toString();
    }
}
