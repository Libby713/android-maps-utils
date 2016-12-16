package com.google.maps.android.geojsonkmlabstraction;

/**
 * Represents a geometry object.
 *
 * @param <T> type of object that the coordinates are stored in
 */
public interface Geometry<T> {

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    public String getGeometryType();

    /**
     * Gets the stored KML Geometry object
     *
     * @return geometry object
     */
    public T getGeometryObject();

}
