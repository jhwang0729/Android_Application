package edu.illinois.cs.cs125.fall2019.mp;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Divides a rectangular area into identically sized, roughly square cells.
 * Each cell is given an X and Y coordinate. X increases from the west boundary
 * toward the east boundary; Y increases from south to north. So (0, 0) is the
 * cell in the southwest corner.
 * Instances of this class are created with a desired cell size.
 * However, it is unlikely that the area dimensions will be an exact multiple of that length,
 * so placing fully sized cells would leave a small "sliver" on the east or north side.
 * Length should be redistributed so that each cell is exactly the same size.
 * If the area is 70 meters long in one dimension and the cell size is 20 meters,
 * there will be four cells in that dimension (there's room for three full cells
 * plus a 10m sliver), each of which is 70 / 4 = 17.5 meters long. Redistribution
 * happens independently for the two dimensions, so a 70x40 area would be divided
 * into 17.5x20.0 cells with a 20m cell size.
 */
public class AreaDivider {
    /**
     * @north north
     */
    private double north;
    /**
     * @east east
     */
    private double east;
    /**
     * @south south
     */
    private double south;
    /**
     * @west west
     */
    private double west;
    /**
     * @cellSize cellSize
     */
    private double cellSize;

    /**
     * Creates an AreaDivider for an area.
     * @param setNorth - latitude of the north boundary
     * @param setEast - longitude of the east boundary
     * @param setSouth - latitude of the south boundary
     * @param setWest - longitude of the west boundary
     * @param setCellSize - the requested side length of each cell, in meters
     */
    public AreaDivider(final double setNorth, final double setEast, final double setSouth,
                       final double setWest, final double setCellSize) {
        north = setNorth;
        east = setEast;
        south = setSouth;
        west = setWest;
        cellSize = setCellSize;
    }

    /**
     * Gets the boundaries of the specified cell as a Google Maps LatLngBounds object.
     * @param x - the cell's X coordinate
     * @param y - the cell's Y coordinate
     * @return the boundaries of the cell
     */

    public com.google.android.gms.maps.model.LatLngBounds getCellBounds(final int x, final int y) {

        double xNum = (east - west) / getXCells();
        double yNum = (north - south) / getYCells();
        double xDegree = x * xNum + west;
        double yDegree = y * yNum + south;
        double xDegree2 = (x + 1) * xNum + west;
        double yDegree2 = (y + 1) * yNum + south;
        LatLngBounds outputBound = new LatLngBounds(new LatLng(yDegree, xDegree), new LatLng(yDegree2, xDegree2));
        return outputBound;
    }

    /**
     * Gets the number of cells between the west and east boundaries.
     * See the class description for more details on area division.
     * @return the number of cells in the X direction
     */
    public int getXCells() {
        double num = Math.ceil(LatLngUtils.distance(south, west, south, east) / cellSize);
        return (int) num;
        //Gets the number of cells between the west and east boundaries.
        // See the class description for more details on area division.
    }
    /**
     * Gets the X coordinate of the cell containing the specified location.
     * The point is not necessarily within the area.
     * @param location - the location.
     * @return the X coordinate of the cell containing the lat-long point.
     */
    public int getXCoordinate(final com.google.android.gms.maps.model.LatLng location) {
        double dist = (location.longitude - west) / ((east - west) / getXCells());
        return (int) dist;
        // Gets the X coordinate of the cell containing
        // the specified location.
        // The point is not necessarily within the area.
        // double dist = LatLngUtils.distance(location, , location, longitude);
    }

    /**
     * Gets the number of cells between the south and north boundaries.
     * See the class description for more details on area division.
     * @return the number of cells in the Y direction
     */
    public int getYCells() {
        double num = Math.ceil(LatLngUtils.distance(south, east, north, east) / cellSize);
        return (int) num;
    }
    /**
     * Gets the Y coordinate of the cell containing the specified location.
     * The point is not necessarily within the area.
     * @param location - the location
     * @return the Y coordinate of the cell containing the lat-long point
     */
    public int getYCoordinate(final com.google.android.gms.maps.model.LatLng location) {
        //Gets the Y coordinate of the cell containing the specified location.
        double dist = Math.floor((location.latitude - south) / ((north - south) / getYCells()));
        return (int) dist;
    }

    /**
     * Draws the grid to a map using solid black polylines.
     * There should be one line on each of the four boundaries of the overall area and as many internal lines
     * as necessary to divide the rows and columns of the grid. Each line should span the whole width or height
     * of the area rather than the side of just one cell. For example, an area divided into a 2x3 grid would be
     * drawn with 7 lines total: 4 for the outer boundaries, 1 vertical line to divide the west half from
     * the east half (2 columns), and 2 horizontal lines to divide the area into 3 rows.
     *See the provided addLine function from GameActivity for how to add a line to the map.
     * Since these lines should be black, you do not need the extra line to make the line appear to have a border.
     * @param map - the Google map to draw on
     */
    public void renderGrid(final com.google.android.gms.maps.GoogleMap map) {
        final int border = 3;
        double xNum = (east - west) / getXCells();
        double yNum = (north - south) / getYCells();
        for (int i = 0; i <= getYCells(); i++) {
            LatLng first = new LatLng(i * yNum + south, west);
            LatLng last = new LatLng(i * yNum + south, east);
            final int line = 12;
            PolylineOptions xCells = new PolylineOptions().add(first, last)
                    .color(android.R.color.black).width(line).zIndex(1);
            map.addPolyline(xCells);
        }
        for (int j = 0; j <= getXCells(); j++) {
            LatLng first = new LatLng(south, j * xNum + west);
            LatLng last = new LatLng(north, j * xNum + west);
            final int line = 12;
            PolylineOptions yCells = new PolylineOptions().add(first, last)
                    .color(android.R.color.black).width(line).zIndex(1);
            map.addPolyline(yCells);
        }
    }
}
