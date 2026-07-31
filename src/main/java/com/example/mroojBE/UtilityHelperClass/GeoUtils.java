package com.example.mroojBE.UtilityHelperClass;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Central helper for converting between JTS {@link Point} (SRID 4326)
 * and latitude/longitude doubles used in DTOs.
 *
 * Note: JTS uses (x, y) = (longitude, latitude).
 */
//helper class to convert between simple lat/lng numbers (used in DTOs/JSON)
public final class GeoUtils {

    private static final int SRID = 4326;
    //factory used to build any JTS geometry (Point, Line, Polygon...)
    private static final GeometryFactory FACTORY = new GeometryFactory(new PrecisionModel(), SRID);

    private GeoUtils() {
        // Prevent instantiation
    }

    public static Point createPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        Point point = FACTORY.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID);
        return point;
    }

    public static Double latitudeOf(Point point) {
        return point == null ? null : point.getY();
    }

    public static Double longitudeOf(Point point) {
        return point == null ? null : point.getX();
    }
}