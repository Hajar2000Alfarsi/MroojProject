package com.example.mroojBE.UtilityHelperClass;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;


//helper class to convert between simple lat/lng numbers (used in DTOs/JSON)
public class GeoUtils {

    //factory used to build any JTS geometry (Point, Line, Polygon...)
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point toPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        // JTS Coordinate order is (x = longitude, y = latitude)
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    public static Double getLatitude(Point point) {
        return point == null ? null : point.getY();
    }

    public static Double getLongitude(Point point) {
        return point == null ? null : point.getX();
    }
}
