package edu.illinois.cs.cs125.fall2019.mp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * Represents the main screen of the app, where the user will be able to view invitations and enter games.
 */
public final class MainActivity extends AppCompatActivity {
    /**
     * Called by the Android system when the activity is created.
     *
     * @param savedInstanceState saved state from the previously terminated instance of this activity (unused)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // This "super" call is required for all activities
        super.onCreate(savedInstanceState);
        // Create the UI from a layout resource
        setContentView(R.layout.activity_main);
        // This activity doesn't do anything yet - it immediately launches the game activity
        // Work on it will start in Checkpoint 1
        Button onGoingEnter = findViewById(R.id.enter);
        //Button onGoingLeave = findViewById(R.id.leave);
        Button invitationsAccept = findViewById(R.id.accept);
        Button invitationsDecline = findViewById(R.id.decline);
        // Intents are Android's way of specifying what to do/launch
        // Here we create an Intent for launching GameActivity and act on it with startActivity
        Button createGame = findViewById(R.id.createGame);
        createGame.setOnClickListener(v -> {
            startActivity(new Intent(this, NewGameActivity.class));
        });
        // End this activity so that it's removed from the history
        // Otherwise pressing the back button in the game would come back to a blank screen here
        connect();
        finish();
    }
    /*
    JsonArray lectureDays = cs125.get("lecture_days").getAsJsonArray();
    for (JsonElement d : lectureDays) {
    String day = d.getAsString();
    // Do something with day?
    }
    */

    /**
     * Invoked by the Android system when a request launched
     * by startActivityForResult completes.
     * @param myRequestCode the request code passed by to startActivityForResult
     * @param resultCode a value indicating how the request finished
     *        (e.g. completed or canceled)
     * @param data an Intent containing results
     *        (e.g. as a URI or in extras)
     */
    // The functions below are stubs that will be filled out in Checkpoint 2

    /**
     * Starts an attempt to connect to the server to fetch/refresh games.
     */
    private void connect() {
        WebApi.startRequest(this, WebApi.API_BASE + "/games", response -> {
            if (response != null) {
                setUpUi(response);
            }
            // Code in this handler will run when the request completes successfully
            // Do something with the response?
        }, error -> {
            // Code in this handler will run if the request fails
            // Maybe notify the user of the error?
                Toast.makeText(this, "Oh no!", Toast.LENGTH_LONG).show();
            });
        // Make any "loading" UI adjustments you like
        // Use WebApi.startRequest to fetch the games lists
        // In the response callback, call setUpUi with the received data
    }
    /**
     * Populates the games lists UI with data retrieved from the server.
     *
     * @param result parsed JSON from the server
     */
    private void setUpUi(final JsonObject result) {
        LinearLayout invitationsGroup = findViewById(R.id.invitationsGroup);
        LinearLayout invitationsList = findViewById(R.id.invitationsList);
        LinearLayout onGoingGamesList = findViewById(R.id.ongoingGamesList);
        LinearLayout onGoingGameGroup = findViewById(R.id.ongoingGamesGroup);
        //
        invitationsList.removeAllViews();
        onGoingGamesList.removeAllViews();
        JsonArray gamesList = result.get("games").getAsJsonArray();
        //
        for (JsonElement i : gamesList) {
            JsonObject iAsAJSonObject = (JsonObject) i;
            String id = iAsAJSonObject.get("id").getAsString();
            String owner = iAsAJSonObject.get("owner").getAsString();
            int state = iAsAJSonObject.get("state").getAsInt();
            String mode = iAsAJSonObject.get("mode").getAsString();
            JsonArray playersState = iAsAJSonObject.get("players").getAsJsonArray();
            final String finalId = id;
            for (JsonElement j : playersState) {
                JsonObject jAsAJSonObject = (JsonObject) j;
                String playersEmail = jAsAJSonObject.get("email").getAsString();
                int playersTeam = jAsAJSonObject.get("team").getAsInt();
                if (playersEmail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    int playersStateAsInt = jAsAJSonObject.get("state").getAsInt();
                    if (playersStateAsInt == PlayerStateID.INVITED) {
                        View invitationChunk = getLayoutInflater().inflate(R.layout.chunk_invitations,
                                invitationsList, false);
                        //When a accept button is pressed
                        Button invitationsAccept = invitationChunk.findViewById(R.id.accept);
                        invitationsAccept.setOnClickListener(unused -> {
                            WebApi.startRequest(this, WebApi.API_BASE
                                            + "/games/" + id + "/accept",
                                    Request.Method.POST, null, response -> {
                                    connect();
                                }, error -> {
                                });
                        });
                        //when a decline button is pressed
                        Button invitationsDecline = invitationChunk.findViewById(R.id.decline);
                        invitationsDecline.setOnClickListener(unused -> {
                            WebApi.startRequest(this, WebApi.API_BASE
                                            + "/games/" + id + "/decline",
                                    Request.Method.POST, null, response -> {
                                    connect();
                                }, error -> {
                                    Toast.makeText(this, "Oh no!", Toast.LENGTH_LONG).show();
                                });
                        });
                        TextView color = invitationChunk.findViewById(R.id.color);
                        TextView owner1 = invitationChunk.findViewById(R.id.owner);
                        owner1.setText(owner);
                        TextView mode1 = invitationChunk.findViewById(R.id.mode);
                        mode1.setText(mode + " mode");
                        if (TeamID.TEAM_BLUE == playersTeam) {
                            color.setText("BLUE");
                        } else if (TeamID.TEAM_GREEN == playersTeam) {
                            color.setText("GREEN");
                        } else if (TeamID.TEAM_RED == playersTeam) {
                            color.setText("RED");
                        } else if (TeamID.TEAM_YELLOW == playersTeam) {
                            color.setText("YELLOW");
                        } else if (TeamID.OBSERVER == playersTeam) {
                            color.setText("OBSERVER");
                        } else if (owner.equals(playersEmail)) {
                            color.setText("OWNER");
                        }
                        invitationsList.addView(invitationChunk);
                        invitationsGroup.setVisibility(View.VISIBLE);
                    } else if (playersStateAsInt == PlayerStateID.ACCEPTED
                            && state != GameStateID.ENDED) {
                        View onGoingChunk = getLayoutInflater().inflate(
                                R.layout.chunk_ongoing_game,
                                onGoingGamesList, false);
                        Button onGoingEnter = onGoingChunk.findViewById(R.id.enter);
                        //when enter button is pressed
                        onGoingEnter.setOnClickListener(unused -> enterGame(finalId));
                        Button onGoingLeave = onGoingChunk.findViewById(R.id.leave);
                        //when leave button is clicked
                        onGoingLeave.setOnClickListener(unused -> {
                            WebApi.startRequest(this, WebApi.API_BASE
                                + "/games/" + id + "/leave",
                                    Request.Method.POST, null, response -> {
                                    connect();
                                }, error -> {
                                    Toast.makeText(this, "Oh no!", Toast.LENGTH_LONG).show();
                                });
                        });
                        TextView color = onGoingChunk.findViewById(R.id.color);
                        TextView owner1 = onGoingChunk.findViewById(R.id.owner);
                        owner1.setText((owner));
                        TextView mode1 = onGoingChunk.findViewById(R.id.mode);
                        mode1.setText(mode + " mode");
                        if (TeamID.TEAM_BLUE == playersTeam) {
                            color.setText("BLUE");
                        } else if (TeamID.TEAM_GREEN == playersTeam) {
                            color.setText("GREEN");
                        } else if (TeamID.TEAM_RED == playersTeam) {
                            color.setText("RED");
                        } else if (TeamID.TEAM_YELLOW == playersTeam) {
                            color.setText("YELLOW");
                        } else if (TeamID.OBSERVER == playersTeam) {
                            color.setText("OBSERVER");
                        } else if (owner.equals(playersEmail)) {
                            color.setText("OWNER");
                        }
                        onGoingGamesList.addView(onGoingChunk);
                        onGoingGameGroup.setVisibility(View.VISIBLE);

                        if (owner.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            onGoingLeave.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        // Hide any optional "loading" UI you added
        // Clear the games lists
        // Add UI chunks to the lists based on the result data
    }
    /**
     * Enters a game (shows the map).
     *
     * @param gameId the ID of the game to enter
     */
    private void enterGame(final String gameId) {
        Intent intentEnterGame = new Intent(this, GameActivity.class);
        intentEnterGame.putExtra("game", gameId);
        startActivity(intentEnterGame);
        // Launch GameActivity with the game ID in an intent extra
        // Do not finish - the user should be able to come back here
    }
}
