package com.example.mroojBE.UtilityHelperClass;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeoUtilsTest {
    @Test
    void createsWgs84PointWithLongitudeAsXAndLatitudeAsY() {
        Point point = GeoUtils.createPoint(23.5880, 58.3829);
        assertEquals(58.3829, point.getX(), 0.000001);
        assertEquals(23.5880, point.getY(), 0.000001);
        assertEquals(4326, point.getSRID());
    }
}
