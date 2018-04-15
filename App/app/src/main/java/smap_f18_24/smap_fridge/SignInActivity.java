package smap_f18_24.smap_fridge;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

//Sign in implemented with FirebaseUI

public class SignInActivity extends AppCompatActivity {

    Button btn_logout;

    private static final int RC_SIGN_IN = 123;

    private static final String TAG = "SignInActivity";

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btn_logout = findViewById(R.id.signin_btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.stinus_face)      // Set logo drawable
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Successfully signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Successfully signed in as " + user.getDisplayName());
                // ...
            } else {
                Toast.makeText(this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Could not sign in.");
            }
        }
    }

    public void logOut()
    {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SignInActivity.this, "Successfully logget out.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}