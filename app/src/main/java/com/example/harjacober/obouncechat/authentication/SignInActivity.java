package com.example.harjacober.obouncechat.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.ui.MainActivity;
import com.example.harjacober.obouncechat.utils.NetworkUtils;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import static com.example.harjacober.obouncechat.authentication.WelcomeActivity.SIGNEDIN;
import static com.example.harjacober.obouncechat.authentication.WelcomeActivity.SIGNED_IN_BEFORE;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private static final int RC_SIGN_IN = 97;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null){
            launchFirebaseUi();
        }else {
            launchCorrespondingActivity();
        }
    }

    private void launchFirebaseUi() {
        // Choose authentication providers
        if (NetworkUtils.isConnected(this)) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }else{
            //TODO display a retry button snd sllow user to click it to retry
            Toast.makeText(this, "Np Internet",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                /**Saves the user registration progress so incase whereby the user
                        decides to continue registration later, the app starts from where the user left off.*/
                SharedPreferenceUtils.saveRegistrationProgress(
                        this,
                        SIGNEDIN);
                launchCorrespondingActivity();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                if (response == null){

                }// response.getError().getErrorCode() and handle the error.
                else {
                    String error = response.getError().getMessage();
                    Log.i("errorMessageFirebaseui", error);
                }
            }
        }
    }

    private void launchCorrespondingActivity() {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        reference.child("users")
                .child(mUser.getUid())
                .child("registrationStatus").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String regStatus = dataSnapshot.getValue(String.class);
                        if (regStatus != null) {
                            if (regStatus.equals("completed")) {
                                SharedPreferenceUtils.saveRegistrationProgress(
                                        SignInActivity.this,
                                        SIGNED_IN_BEFORE
                                );
                                Intent intent = new Intent(SignInActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SignInActivity.this,
                                        UserInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            Intent intent = new Intent(SignInActivity.this,
                                    UserInfoActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
