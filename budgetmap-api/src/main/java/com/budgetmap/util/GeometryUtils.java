package com.budgetmap.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtils {
    private static final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point crearPunto(Double latitud, Double longitud) {
        if (latitud == null || longitud == null)
            return null;
        return factory.createPoint(new Coordinate(longitud, latitud));
    }
}