package com.university.attendance.attendance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AttendanceServiceTests {

    // Haversine distance calculator helper to test logic in isolation
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Test
    public void testHaversineDistance_CloseProximity() {
        // Classroom coordinates
        double classLat = 12.9715987;
        double classLng = 77.5945627;

        // Student close coordinate (~44.6 meters away)
        double studentLat = 12.9718987;
        double studentLng = 77.5948627;

        double distance = calculateDistance(classLat, classLng, studentLat, studentLng);
        assertTrue(distance < 50.0, "Distance should be under 50 meters geofence");
    }

    @Test
    public void testHaversineDistance_OutsideGeofence() {
        // Classroom coordinates
        double classLat = 12.9715987;
        double classLng = 77.5945627;

        // Student far coordinate (~150 meters away)
        double studentLat = 12.9725987;
        double studentLng = 77.5955627;

        double distance = calculateDistance(classLat, classLng, studentLat, studentLng);
        assertTrue(distance > 50.0, "Distance should exceed 50 meters geofence");
    }
}
