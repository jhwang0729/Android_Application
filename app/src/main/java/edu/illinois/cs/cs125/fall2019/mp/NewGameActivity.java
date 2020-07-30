package edu.illinois.cs.cs125.fall2019.mp;

import android.content.Intent;
import android.graphics.Point;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
//import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.ScrollView;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents the game creation screen, where the user configures a new game.
 */
public final class NewGameActivity extends AppCompatActivity {

    // This activity doesn't do much at first - it'll be worked on in Checkpoints 1 and 3

    /**
     * The Google Maps view used to set the area for area mode. Null until getMapAsync finishes.
     */
    private GoogleMap areaMap;
    /**
     * The Google Maps view used to set the area for target mode. Null until getMapAsync finishes.
     */
    private GoogleMap targetsMap;
    /** List of Google Maps. */
    private List<Marker> googleMapsList;
    /** List of invited players List. */
    private List<Invitee> invitedPlayersList;
    /**
     * Called by the Android system when the activity is created.
     * @param savedInstanceState state from the previously terminated instance (unused)
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game); // app/src/main/res/layout/activity_new_game.xml
        Button addInviteeButton = findViewById(R.id.addInvitee);
        googleMapsList = new ArrayList<>();
        invitedPlayersList = new ArrayList<>();
        invitedPlayersList.add(new Invitee(FirebaseAuth.getInstance().getCurrentUser()
                .getEmail(), 0));
        updatePlayersUI();
        addInviteeButton.setOnClickListener(v -> {
            addInvitee();
        });
        setTitle(R.string.create_game); // Change the title in the top bar
        // Now that setContentView has been called, findViewById and findFragmentById work

        // checkedId is the R.id constant of the currently checked RadioButton
        // Your code here: make only the selected mode's settings group visible
        SupportMapFragment areaMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.areaSizeMap);
        // Start the process of getting a Google Maps object
        areaMapFragment.getMapAsync(newMap -> {
            // NONLINEAR CONTROL FLOW: Code in this block is called later, after onCreate ends
            // It's a "callback" - it will be called eventually when the map is ready

            // Set the map variable so it can be used by other functions
            areaMap = newMap;
            // Center it on campustown
            centerMap(areaMap);
        });

        SupportMapFragment targetsMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.targetsMap);
        targetsMapFragment.getMapAsync(newMap -> {
            targetsMap = newMap;
            centerMap(targetsMap);
            System.out.println(targetsMap);
            targetsMap.setOnMapLongClickListener(location -> {
                MarkerOptions currentLocationMarkerOption =
                        new MarkerOptions().position(location);
                Marker currentLocationMarker = targetsMap.addMarker(currentLocationMarkerOption);
                googleMapsList.add(currentLocationMarker);
                // Code here runs whenever the user presses on the map.
                // location is the LatLng position where the user pressed.
                // 1. Create a Google Maps Marker at the provided coordinates.
                // 2. Add it to your targets list instance variable.
            });

            targetsMap.setOnMarkerClickListener(clickedMarker -> {
                clickedMarker.remove();
                googleMapsList.remove(clickedMarker);
                // Code here runs whenever the user taps a marker.
                // clickedMarker is the Marker object the user clicked.
                // 1. Remove the marker from the map with its remove function.
                // 2. Remove it from your targets list.
                return true; // This makes Google Maps not pan the map again
            });
        });
        /*
         * Setting an ID for a control in the UI designer produces a constant on R.id
         * that can be passed to findViewById to get a reference to that control.
         * Here we get a reference to the Create Game button.
         */
        Button createGame = findViewById(R.id.createGame);
        /*
         * Now that we have a reference to the control, we can use its setOnClickListener
         * method to set the handler to run when the user clicks the button. That function
         * takes an OnClickListener instance. OnClickListener, like many types in Android,
         * has exactly one function which must be filled out, so Java allows instances of it
         * to be written as "lambdas", which are like small functions that can be passed around.
         * The part before the arrow is the argument list (Java infers the types); the part
         * after is the statement to run. Here we don't care about the argument, but it must
         * be there for the signature to match.
         */
        createGame.setOnClickListener(unused -> createGameClicked());
        LinearLayout areaSettings = findViewById(R.id.areaSettings);
        LinearLayout targetSettings = findViewById(R.id.targetSettings);
        RadioGroup modeGroup = findViewById(R.id.gameModeGroup);
        areaSettings.setVisibility(View.GONE);
        targetSettings.setVisibility((View.GONE));
        modeGroup.setOnCheckedChangeListener((unused, checkedId) -> {
            areaSettings.setVisibility(View.GONE);
            targetSettings.setVisibility(View.GONE);
            if (checkedId == R.id.areaModeOption) {
                areaSettings.setVisibility(View.VISIBLE);
                targetSettings.setVisibility(View.GONE);
            } else if (checkedId == R.id.targetModeOption) {
                areaSettings.setVisibility(View.GONE);
                targetSettings.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * This function is responsible for repopulating the players list in the UI
     * with the information stored in the players list instance variable.
     */
    private void updatePlayersUI() {
        LinearLayout playersList = findViewById(R.id.playersList);
        System.out.println(playersList);
        playersList.removeAllViews();
        for (int i = 0; i < invitedPlayersList.size(); i++) {
            View inviteeChunk = getLayoutInflater().inflate(R.layout.chunk_invitee,
                    playersList, false);
            TextView inviteeEmail = inviteeChunk.findViewById(R.id.inviteeEmail);
            //Spinner
            Spinner inviteeTeam = inviteeChunk.findViewById(R.id.inviteeTeam);
            inviteeEmail.setText(invitedPlayersList.get(i).getEmail());
            inviteeTeam.setSelection(invitedPlayersList.get(i).getTeamId());
            Invitee invitedPlayer;
            invitedPlayer = invitedPlayersList.get(i);
            // Suppose spinner is a Spinner variable.
            inviteeTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view,
                                           final int position, final long id) {
                    invitedPlayer.setTeamId(position);
                    updatePlayersUI();
                    // Called when the user selects a different item in the dropdown
                    // The position parameter is the selected index
                    // The other parameters can be ignored
                }
                @Override
                public void onNothingSelected(final AdapterView<?> parent) {
                    // Called when the selection becomes empty
                    // Not relevant to the MP - can be left blank
                }
            });
            // Button
            playersList.addView(inviteeChunk);
            Button removeButton = inviteeChunk.findViewById(R.id.removeInvitee);
            if (i == 0) {
                removeButton.setVisibility(View.GONE);
            } else {
                removeButton.setVisibility(View.VISIBLE);
            }
            removeButton.setOnClickListener(v -> {
                invitedPlayersList.remove(invitedPlayer);
                updatePlayersUI();
            });
        }
    }

    /**
     * This functions should be called when the user presses the addInvitee button.
     */
    private void addInvitee() {
        EditText newInviteeEmail = findViewById(R.id.newInviteeEmail);
        if (!(newInviteeEmail.getText().toString().equals(""))) {
            Invitee inviteeAsObject = new Invitee(newInviteeEmail
                    .getText().toString(), TeamID.OBSERVER);
            invitedPlayersList.add(inviteeAsObject);
            newInviteeEmail.getText().clear();
            updatePlayersUI();
        }
    }
        /*
         * It's also possible to make lambdas for functions that take zero or multiple parameters.
         * In those cases, the parameter list needs to be wrapped in parentheses, like () for a
         * zero-argument lambda or (someArg, anotherArg) for a two-argument lambda. Lambdas that
         * run multiple statements, like the one passed to getMapAsync above, look more like
         * normal functions in that they need their body wrapped in curly braces. Multi-statement
         * lambdas for functions with a non-void return type need return statements, again like
         * normal functions.
         */

    /**
     * Sets up the area sizing map with initial settings: centering on campustown.
     * <p>
     * You don't need to alter or understand this function, but you will want to use it when
     * you add another map control in Checkpoint 3.
     *
     * @param map the map to center
     */
    private void centerMap(final GoogleMap map) {
        // Bounds of campustown and some surroundings
        final double swLatitude = 40.098331;
        final double swLongitude = -88.246065;
        final double neLatitude = 40.116601;
        final double neLongitude = -88.213077;

        // Get the window dimensions (for the width)
        Point windowSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(windowSize);

        // Convert 300dp (height of map control) to pixels
        final int mapHeightDp = 300;
        float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mapHeightDp,
                getResources().getDisplayMetrics());

        // Submit the camera update
        final int paddingPx = 10;
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                new LatLng(swLatitude, swLongitude),
                new LatLng(neLatitude, neLongitude)), windowSize.x, (int) heightPx, paddingPx));
    }

    /**
     * Code to run when the Create Game button is clicked.
     */
    private void createGameClicked() {
        JsonObject newGame = new JsonObject();
        Intent intent = new Intent(this, GameActivity.class);
        RadioGroup modeGroup = findViewById(R.id.gameModeGroup);
        EditText proximityThreshold = findViewById(R.id.proximityThreshold);
        String proximityThresholdString = proximityThreshold.getText().toString();
        if (modeGroup.getCheckedRadioButtonId() == R.id.targetModeOption) {
            if ((proximityThresholdString.length() == 0)) {
                return;
            } else {
                int proximityThresholdInteger = Integer.parseInt(proximityThresholdString);
                intent.putExtra("mode", "target");
                intent.putExtra("proximityThreshold", proximityThresholdInteger);
                //startActivity(intent);
                //finish();
            }
        }
        EditText cellSize = findViewById(R.id.cellSize);
        String cellSizeString = cellSize.getText().toString();
        if (modeGroup.getCheckedRadioButtonId() == R.id.areaModeOption) {
            if (cellSizeString.length() == 0) {
                return;
            } else {
                LatLngBounds bounds = areaMap.getProjection().getVisibleRegion().latLngBounds;
                double areaNorth = bounds.northeast.latitude;
                double areaEast = bounds.northeast.longitude;
                double areaSouth = bounds.southwest.latitude;
                double areaWest = bounds.southwest.longitude;
                int cellSizeInteger = Integer.parseInt(cellSizeString);
                intent.putExtra("mode", "area");
                intent.putExtra("areaNorth", areaNorth);
                intent.putExtra("areaEast", areaEast);
                intent.putExtra("areaSouth", areaSouth);
                intent.putExtra("areaWest", areaWest);
                intent.putExtra("cellSize", cellSizeInteger);
                //startActivity(intent);
                //finish();
            }
        }
        //For target mode only
        if (modeGroup.getCheckedRadioButtonId() == R.id.targetModeOption) {
            newGame.addProperty("mode", "target");
            int proximityThresholdInt = Integer.parseInt(proximityThresholdString);
            newGame.addProperty("proximityThreshold", proximityThresholdInt);
            JsonArray targetsArray = new JsonArray();
            for (Marker targets: googleMapsList) {
                JsonObject targetsAsJsonObject = new JsonObject();
                double targetsLatitude = targets.getPosition().latitude;
                double targetsLongitude = targets.getPosition().longitude;
                targetsAsJsonObject.addProperty("latitude", targetsLatitude);
                targetsAsJsonObject.addProperty("longitude", targetsLongitude);
                targetsArray.add(targetsAsJsonObject);
            }
            newGame.add("targets", targetsArray);
            JsonArray inviteesArray = new JsonArray();
            for (Invitee invitees: invitedPlayersList) {
                JsonObject inviteesAsJsonObject = new JsonObject();
                String inviteesEmail = invitees.getEmail();
                int inviteesTeam = invitees.getTeamId();
                inviteesAsJsonObject.addProperty("email", inviteesEmail);
                inviteesAsJsonObject.addProperty("team", inviteesTeam);
                inviteesArray.add(inviteesAsJsonObject);
            }
            newGame.add("invitees", inviteesArray);
            System.out.println(newGame.toString());
        //For area mode only
        } else if (modeGroup.getCheckedRadioButtonId() == R.id.areaModeOption) {
            LatLngBounds bounds = areaMap.getProjection().getVisibleRegion().latLngBounds;
            newGame.addProperty("mode", "area");
            int cellSizeInt = Integer.parseInt(cellSizeString);
            double areaNorth = bounds.northeast.latitude;
            double areaEast = bounds.northeast.longitude;
            double areaSouth = bounds.southwest.latitude;
            double areaWest = bounds.southwest.longitude;
            newGame.addProperty("cellSize", cellSizeInt);
            newGame.addProperty("areaNorth", areaNorth);
            newGame.addProperty("areaEast", areaEast);
            newGame.addProperty("areaSouth", areaSouth);
            newGame.addProperty("areaWest", areaWest);
            JsonArray inviteesArray = new JsonArray();
            for (Invitee invitees: invitedPlayersList) {
                JsonObject inviteesAsJsonObject = new JsonObject();
                String inviteesEmail = invitees.getEmail();
                int inviteesTeam = invitees.getTeamId();
                inviteesAsJsonObject.addProperty("email", inviteesEmail);
                inviteesAsJsonObject.addProperty("team", inviteesTeam);
                inviteesArray.add(inviteesAsJsonObject);
            }
            newGame.add("invitees", inviteesArray);
        }
        WebApi.startRequest(this, WebApi.API_BASE
                        + "/games/create",
                Request.Method.POST, newGame, response -> {
                Intent intent2 = new Intent(this, GameActivity.class);
                intent2.putExtra("game", response.get("game").getAsString());
                startActivity(intent2);
                finish();
            }, error -> {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            });

    }
}

