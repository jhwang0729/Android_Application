package edu.illinois.cs.cs125.fall2019.mp;

/**
 * Holds a method to determine whether two lines cross.
 * <p>
 * The implementation given here works. You do not need to change the logic, but there are some style
 * problems that you do need to correct.
 * <p>
 * This file will be revisited in Checkpoint 3.
 */
public class LineCrossDetector {

    /**
     * Determines whether two lines cross.
     * <p>
     * <i>Crossing</i> is not always the same as <i>intersecting</i>. Lines that share a tip
     * intersect but do not cross for purposes of this function. However, a line that has an endpoint
     * on the <i>middle</i> of another line must be considered to cross that line (to prevent
     * circumventing the snake rule).
     * <p>
     * For simplicity, longitude and latitude are treated as X and Y, respectively, on a 2D coordinate plane.
     * This ignores the roundness of the earth, but it's undetectable at reasonable scales of the game.
     * <p>
     * All parameters are assumed to be valid: both lines have positive length.
     * @param firstStart an endpoint of one line.
     * @param firstEnd the other endpoint of that line.
     * @param secondStart an endpoint of another line.
     * @param secondEnd the other endpoint of that other line.
     * @return wheteher two lines cross.
     */
    public static boolean linesCross(final com.google.android.gms.maps.model.LatLng firstStart,
                                     final com.google.android.gms.maps.model.LatLng firstEnd,
                                     final com.google.android.gms.maps.model.LatLng secondStart,
                                     final com.google.android.gms.maps.model.LatLng secondEnd) {
        if (LatLngUtils.same(firstStart, secondStart)
                || LatLngUtils.same(firstStart, secondEnd)
                || LatLngUtils.same(firstEnd, secondStart)
                || LatLngUtils.same(firstEnd, secondEnd)) {
            // The lines are just sharing endpoints, not crossing each other
            return false;
        }

        // A line is vertical (purely north-south) if its longitude is constant
        boolean firstVertical = LatLngUtils.same(firstStart.longitude, firstEnd.longitude);
        boolean secondVertical = LatLngUtils.same(secondStart.longitude, secondEnd.longitude);
        if (firstVertical && secondVertical) {
            // They're parallel vertical line
            return false;
        } else if (firstVertical) {
            return lineCrossesVertical(firstStart.latitude, firstEnd.latitude,
                    firstStart.longitude, secondStart.latitude,
                    secondStart.longitude, secondEnd.latitude, secondEnd.longitude);
        } else if (secondVertical) {
            return lineCrossesVertical(secondStart.latitude, secondEnd.latitude,
                    secondStart.longitude, firstStart.latitude, firstStart.longitude,
                    firstEnd.latitude, firstEnd.longitude);
        }

        // At this point, neither line is vertical
        double firstSlope = lineSlope(firstStart.latitude, firstStart.longitude,
                firstEnd.latitude, firstEnd.longitude);
        double secondSlope = lineSlope(secondStart.latitude, secondStart.longitude,
                secondEnd.latitude, secondEnd.longitude);
        if (LatLngUtils.same(firstSlope, secondSlope)) {
            // They're parallel
            return false;
        }

        // At this point, the lines are non-parallel (would intersect if infinitely extended)
        double firstIntercept = firstStart.latitude - firstSlope * firstStart.longitude;
        double secondIntercept = secondStart.latitude - secondSlope * secondStart.longitude;
        double intersectionX = -(firstIntercept - secondIntercept) / (firstSlope - secondSlope);
        if (LatLngUtils.same(intersectionX, firstStart.longitude)
                || LatLngUtils.same(intersectionX, firstEnd.longitude)
                || LatLngUtils.same(intersectionX, secondStart.longitude)
                || LatLngUtils.same(intersectionX, secondEnd.longitude)) {
            // Endpoint of one line is in the middle of the other line
            return true;
        }
        boolean onFirst = intersectionX > Math.min(firstStart.longitude,
                firstEnd.longitude)
                && intersectionX < Math.max(firstStart.longitude,
                firstEnd.longitude);
        boolean onSecond = intersectionX > Math.min(secondStart.longitude,
                secondEnd.longitude)
                && intersectionX < Math.max(secondStart.longitude,
                secondEnd.longitude);
        return onFirst && onSecond;
    }

    /**
     * Determines if a non-vertical line crosses a vertical line.
     *
     * @param verticalStartLat the latitude of one endpoint of the vertical line
     * @param verticalEndLat   the latitude of the other endpoint of the vertical line
     * @param verticalLng      the longitude of the vertical line
     * @param lineStartLat     the latitude of one endpoint of the non-vertical line
     * @param lineStartLng     the longitude of that endpoint
     * @param lineEndLat       the latitude of the other endpoint of the line
     * @param lineEndLng       the longitude of that other endpoin
     * @return whether the lines cross
     */
    private static boolean lineCrossesVertical(final double verticalStartLat, final double verticalEndLat,
                                               final double verticalLng,
                                               final double lineStartLat, final double lineStartLng,
                                               final double lineEndLat, final double lineEndLng) {
        if (Math.max(lineStartLng, lineEndLng) < verticalLng
                || Math.min(lineStartLng, lineEndLng) > verticalLng) {
            // The non-vertical line is completely off to the side of the vertical line
            return false;
        }
        double slope = lineSlope(lineStartLat, lineStartLng, lineEndLat, lineEndLng);
        double yAtVert = slope * (verticalLng - lineStartLng) + lineStartLat;
        if (LatLngUtils.same(yAtVert, verticalStartLat) || LatLngUtils.same(yAtVert, verticalEndLat)) {
            // Ends on the middle of the non-vertical line
            return true;
        }
        // See if the intersection of the lines is between the endpoints of the vertical line segment
        return yAtVert > Math.min(verticalStartLat, verticalEndLat)
                && yAtVert < Math.max(verticalStartLat, verticalEndLat);
    }

    /**
     * Determines the slope of a non-vertical line.
     *
     * @param startLat the latitude of one endpoint of the line
     * @param startLng the longitude of that endpoint
     * @param endLat   the latitude of the other endpoint of the line
     * @param endLng   the longitude of that other endpoint
     * @return the slope, treating longitude as X and latitude as Y
     */
    private static double lineSlope(final double startLat, final double startLng,
                                    final double endLat, final double endLng) {
        return (endLat - startLat) / (endLng - startLng);
    }

}
