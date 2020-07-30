package edu.illinois.cs.cs125.fall2019.mp;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents a target in an ongoing target-mode game
 * and manages the marker displaying it.
 * The marker's color (hue, technically)
 * changes to indicate the team owning it.
 * The Google Maps marker's hue should be BitmapDescriptorFactory.
 * HUE_RED for the red team, BitmapDescriptorFactory.HUE_YELLOW for
 * the yellow team, BitmapDescriptorFactory.HUE_GREEN for the green team,
 * BitmapDescriptorFactory.HUE_BLUE for the blue team, and
 * BitmapDescriptorFactory.HUE_VIOLET if unclaimed.
 */

public class Target extends java.lang.Object {
    /** position the position of the target. */
    private static com.google.android.gms.maps.model.LatLng position;
    /** team the TeamID code of the team currently owning the target. */
    private static int team;
    /** moptions. */
    private MarkerOptions options;
    /** marker.*/
    private Marker marker;
    /** icon. */
    private BitmapDescriptor icon;
    /**
     * Creates a target in a target-mode game by placing
     * an appropriately colored marker on the map.
     * @param setMap the map to render to
     * @param setPosition the position of the target
     * @param setTeam the TeamID code of the team currently owning the target
     */
    Target(final com.google.android.gms.maps.GoogleMap setMap,
           final com.google.android.gms.maps.model.LatLng
           setPosition, final int setTeam) {
        position = setPosition;
        team = setTeam;
        options = new MarkerOptions().position(position);
        marker = setMap.addMarker(options);
        switch (team) {
            case TeamID.TEAM_RED:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            case TeamID.TEAM_YELLOW:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                break;
            case TeamID.TEAM_GREEN:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                break;
            case TeamID.TEAM_BLUE:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                break;
            default:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        }
        marker.setIcon(icon);
    }
    /**
     * Gets the position of the target.
     * @return the coordinates of the target.
     */
    public com.google.android.gms.maps.model.LatLng getPosition() {
        return position;
    }
    /**
     * Gets the ID of the team currently owning this target.
     * @return the owning team ID or OBSERVER if unclaimed.
     */
    public int getTeam() {
        return team;
    }
    /**
     * Updates the owning team of this target and
     * changes the marker hue appropriately.
     * @param newTeam the ID of the team that captured the target.
     */
    public void setTeam(final int newTeam) {
        team = newTeam;
        switch (team) {
            case TeamID.TEAM_RED:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            case TeamID.TEAM_YELLOW:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                break;
            case TeamID.TEAM_GREEN:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                break;
            case TeamID.TEAM_BLUE:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                break;
            default:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        }
        marker.setIcon(icon);
    }
}
