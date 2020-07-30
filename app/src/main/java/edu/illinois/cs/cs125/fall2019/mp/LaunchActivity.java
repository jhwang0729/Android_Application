package edu.illinois.cs.cs125.fall2019.mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

/**
 * LaunchActivity.
 */
public class LaunchActivity extends AppCompatActivity {
    /**
     * intent2 intent2.
     */
    private Intent intent2;
    /**
     * RC_SIGN_IN RC_SIGN_IN.
     */
    private static int rcSignIn = 0;

    /**
     * OnCreate.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        intent2 = new Intent(this, MainActivity.class);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(intent2);
            finish();
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    rcSignIn);
        }
    }

    /**
     * Invoked by the Android system when a request
     * launched by startActivityForResult completes.
     * @param myRequestCode requestCode the request code
     *                      passed by to startActivityForResult.
     * @param resultCode resultCode a value indicating
     *                   how the request finished (e.g. completed or canceled)
     * @param data data an Intent containing results
     *             (e.g. as a URI or in extras)
     */
    protected void onActivityResult(final int myRequestCode,
                                    final int resultCode,
                                    final Intent data) {
        super.onActivityResult(rcSignIn, resultCode, data);
        if (myRequestCode == rcSignIn) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startActivity(intent2);
                finish();
            } else {
                ConstraintLayout layOut = findViewById(R.id.ConstraintLayout);
                layOut.setVisibility(View.VISIBLE);
                Button goLogIngButton = findViewById(R.id.goLogin);
                goLogIngButton.setVisibility(View.VISIBLE);
                goLogIngButton.setOnClickListener(unused -> recreate());
            }
            // Do something that depends on the result of that request
        }
    }

}
