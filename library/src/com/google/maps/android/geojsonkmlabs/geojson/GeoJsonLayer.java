package com.google.maps.android.geojsonkmlabs.geojson;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.geojsonkmlabs.Feature;
import com.google.maps.android.geojsonkmlabs.Layer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class that allows the developer to import GeoJSON data, style it and interact with the
 * imported data.
 *
 * To create a new GeoJsonLayer from a resource stored locally
 * {@code GeoJsonLayer layer = new GeoJsonLayer(getMap(), R.raw.resource,
 * getApplicationContext());}
 *
 * To render the imported GeoJSON data onto the layer
 * {@code layer.addLayerToMap();}
 *
 * To remove the rendered data from the layer
 * {@code layer.removeLayerFromMap();}
 */
public class GeoJsonLayer implements Layer {

    private final GeoJsonRenderer mRenderer;

    private LatLngBounds mBoundingBox;

    private GoogleMap mMap;

    /**
     * Creates a new GeoJsonLayer object. Default styles are applied to the GeoJsonFeature objects.
     *
     * @param map         map where the layer is to be rendered
     * @param geoJsonFile GeoJSON data to add to the layer
     */
    public GeoJsonLayer(GoogleMap map, JSONObject geoJsonFile) {
        if (geoJsonFile == null) {
            throw new IllegalArgumentException("GeoJSON file cannot be null");
        }

        mBoundingBox = null;
        GeoJsonParser parser = new GeoJsonParser(geoJsonFile);
        // Assign GeoJSON bounding box for FeatureCollection
        mBoundingBox = parser.getBoundingBox();
        HashMap<Feature, Object> geoJsonFeatures = new HashMap<>();
        for (GeoJsonFeature feature : parser.getFeatures()) {
            geoJsonFeatures.put(feature, null);
        }
        mRenderer = new GeoJsonRenderer(map, geoJsonFeatures);

    }

    /**
     * Creates a new GeoJsonLayer object. Default styles are applied to the GeoJsonFeature objects.
     *
     * @param map        map where the layer is to be rendered
     * @param resourceId GeoJSON file to add to the layer
     * @param context    context of the application, required to open the GeoJSON file
     * @throws IOException   if the file cannot be open for read
     * @throws JSONException if the JSON file has invalid syntax and cannot be parsed successfully
     */
    public GeoJsonLayer(GoogleMap map, int resourceId, Context context)
            throws IOException, JSONException {
        this(map, createJsonFileObject(context.getResources().openRawResource(resourceId)));
    }

    /**
     * Sets a single click listener for the entire GoogleMap object, that will be called
     * with the corresponding GeoJsonFeature object when an object on the map (Polygon,
     * Marker, Polyline) is clicked.
     *
     * Note that if multiple GeoJsonLayer objects are bound to a GoogleMap object, calling
     * setOnFeatureClickListener on one will override the listener defined on the other. In
     * that case, you must define each of the GoogleMap click listeners manually
     * (OnPolygonClickListener, OnMarkerClickListener, OnPolylineClickListener), and then
     * use the GeoJsonLayer.getFeature(mapObject) method on each GeoJsonLayer instance to
     * determine if the given mapObject belongs to the layer.
     *
     * If getFeature() returns null this means that the object is a MultiPolygon, MultiLineString
     * or MultiPoint and must be handled differently.
     *
     * @param listener Listener providing the onFeatureClick method to call.
     */
    public void setOnFeatureClickListener(final GeoJsonOnFeatureClickListener listener) {

        GoogleMap map = getMap();

        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                if (getFeature(polygon) != null) {
                    listener.onFeatureClick((GeoJsonFeature) getFeature(polygon));
                } else {
                    listener.onFeatureClick((GeoJsonFeature) getFeature(multiObjectHandler(polygon)));
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(getFeature(marker) != null) {
                    listener.onFeatureClick((GeoJsonFeature) getFeature(marker));
                } else {
                    listener.onFeatureClick((GeoJsonFeature) getFeature(multiObjectHandler(marker)));
                }
                return false;
            }
        });

        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if (getFeature(polyline) != null) {
                    listener.onFeatureClick((GeoJsonFeature) getFeature(polyline));
                } else {
                    listener.onFeatureClick((GeoJsonFeature) getFeature(multiObjectHandler(polyline)));
                }
            }
        });

    }

    /**
     * Called if the map object is a MultiPolygon, MultiLineString or a MultiPoint and returns
     * the corresponding ArrayList containing the singular Polygons, LineStrings or Points
     * respectively.
     *
     * Test in GeoJsonDemoActivity using earthquakes_with_usa.
     *
     * @param mapObject Object
     * @return an ArrayList of the individual
     */
    private ArrayList<?> multiObjectHandler(Object mapObject) {
        for (Object value : mRenderer.getValues()) {
            Class c = value.getClass();
            if (c.getSimpleName().equals("ArrayList")) {
                ArrayList<?> mapObjects = (ArrayList<?>) value;
                if (mapObjects.contains(mapObject)) {
                    return mapObjects;
                }
            }
        }
        return null;
    }

    /**
     * Callback interface for when a GeoJsonLayer's map object is clicked.
     */
    public interface GeoJsonOnFeatureClickListener {
        void onFeatureClick(GeoJsonFeature feature);
    }

    /**
     * Retrieves a corresponding GeoJsonFeature instance for the given Object
     * Allows maps with multiple layers to determine which layer the Object
     * belongs to.
     *
     * @param mapObject Object
     * @return GeoJsonFeature for the given object
     */
    public Feature getFeature(Object mapObject) { return mRenderer.getFeature(mapObject); }

    /**
     * Takes a character input stream and converts it into a JSONObject
     *
     * @param stream character input stream representing the GeoJSON file
     * @return JSONObject with the GeoJSON data
     * @throws IOException   if the file cannot be opened for read
     * @throws JSONException if the JSON file has poor structure
     */
    private static JSONObject createJsonFileObject(InputStream stream)
            throws IOException, JSONException {
        String line;
        StringBuilder result = new StringBuilder();
        // Reads from stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            // Read each line of the GeoJSON file into a string
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            reader.close();
        }
        // Converts the result string into a JSONObject
        return new JSONObject(result.toString());
    }

    /**
     * Gets an iterable of all GeoJsonFeature elements that have been added to the layer
     *
     * @return iterable of GeoJsonFeature elements
     */
    public Iterable<Feature> getFeatures() {
        return (mRenderer.getFeatures());
    }

    /**
     * Adds all the GeoJsonFeature objects parsed from the given GeoJSON data onto the map
     */
    public void addLayerToMap() {
        mRenderer.addLayerToMap();
    }

    /**
     * Adds a GeoJsonFeature to the layer. If the point, linestring or polygon style is set to
     * null, the relevant default styles are applied.
     *
     * @param feature GeoJsonFeature to add to the layer
     */
    public void addFeature(GeoJsonFeature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }
        mRenderer.addFeature(feature);
    }

    /**
     * Removes the given GeoJsonFeature from the layer
     *
     * @param feature feature to remove
     */
    public void removeFeature(GeoJsonFeature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }
        mRenderer.removeFeature(feature);
    }

    /**
     * Gets the map on which the layer is rendered
     *
     * @return map on which the layer is rendered
     */
    public GoogleMap getMap() {
        return mRenderer.getMap();
    }

    /**
     * Renders the layer on the given map. The layer on the current map is removed and
     * added to the given map.
     *
     * @param map to render the layer on, if null the layer is cleared from the current map
     */
    public void setMap(GoogleMap map) {
        mRenderer.setMap(map);
    }

    /**
     * Removes all GeoJsonFeatures on the layer from the map
     */
    public void removeLayerFromMap() {
        mRenderer.removeLayerFromMap();
    }

    /**
     * Get whether the layer is on the map
     *
     * @return true if the layer is on the map, false otherwise
     */
    public boolean isLayerOnMap() {
        return mRenderer.isLayerOnMap();
    }

    /**
     * Gets the default style used to render GeoJsonPoints. Any changes to this style will be
     * reflected in the features that use it.
     *
     * @return default style used to render GeoJsonPoints
     */
    public GeoJsonPointStyle getDefaultPointStyle() {
        return mRenderer.getDefaultPointStyle();
    }

    /**
     * Gets the default style used to render GeoJsonLineStrings. Any changes to this style will be
     * reflected in the features that use it.
     *
     * @return default style used to render GeoJsonLineStrings
     */
    public GeoJsonLineStringStyle getDefaultLineStringStyle() {
        return mRenderer.getDefaultLineStringStyle();
    }

    /**
     * Gets the default style used to render GeoJsonPolygons. Any changes to this style will be
     * reflected in the features that use it.
     *
     * @return default style used to render GeoJsonPolygons
     */
    public GeoJsonPolygonStyle getDefaultPolygonStyle() {
        return mRenderer.getDefaultPolygonStyle();
    }

    /**
     * Gets the LatLngBounds containing the coordinates of the bounding box for the
     * FeatureCollection. If the FeatureCollection did not have a bounding box or if the GeoJSON
     * file did not contain a FeatureCollection then null will be returned.
     *
     * @return LatLngBounds containing bounding box of FeatureCollection, null if no bounding box
     */
    public LatLngBounds getBoundingBox() {
        return mBoundingBox;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Collection{");
        sb.append("\n Bounding box=").append(mBoundingBox);
        sb.append("\n}\n");
        return sb.toString();
    }
}
