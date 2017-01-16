/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.geojsonkmlabs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An abstraction that shares the common properties of KmlMultiGeometry
 * and GeoJsonMultiLineString, GeoJsonMultiPoint and GeoJsonMultiPolygon
 */
public class MultiGeometry implements Geometry {

    private String geometryType = "MultiGeometry";

    private List<Geometry> mGeometries;

    /**
     * Creates a new MultiGeometry object
     * @param geometries contains list of Polygons, Linestrings or Points
     */
    public MultiGeometry(List<?> geometries) {
        if (geometries == null) {
            throw new IllegalArgumentException("Geometries cannot be null");
        }

        //convert unknown geometry type (due to GeoJSON types) to Geometry type
        ArrayList geometriesList = new ArrayList();
        Iterator<?> geometriesIterator = geometries.iterator();
        while (geometriesIterator.hasNext()) {
            Geometry geometry = (Geometry) geometriesIterator.next();
            geometriesList.add(geometry);
        }

        mGeometries = geometriesList;
    }

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    public String getGeometryType() {
        return geometryType;
    }

    /**
     * Gets the stored geometry object
     *
     * @return geometry object
     */
    public List<Geometry> getGeometryObject() {
        return mGeometries;
    }

    /**
     * Set the type of geometry
     *
     * @param type String describing type of geometry
     */
    public void setGeometryType(String type) {
        geometryType = type;
    }

    @Override
    public String toString() {
        String typeString = "Geometries=";
        if (geometryType.equals("MultiPoint")) {
            typeString = "LineStrings=";
        }
        if (geometryType.equals("MultiLineString")) {
            typeString = "points=";
        }
        if (geometryType.equals("MultiPolygon")) {
            typeString = "Polygons=";
        }

        StringBuilder sb = new StringBuilder(getGeometryType()).append("{");
        sb.append("\n " + typeString).append(getGeometryObject());
        sb.append("\n}\n");
        return sb.toString();
    }
}
