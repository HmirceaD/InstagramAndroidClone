package com.example.mircea.instaapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mircea.instaapp.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build()

    );

    //Firebase Auth
    private FirebaseAuth mAuth;

    //UI
    private Button emailButton;
    private Button facebookButton;
    private Button twitterButton;

    private Button emailSignUpButton;
    private Button facebookSignUpButton;
    private Button twitterSignUpButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            } else {

                Toast.makeText(getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser crrUser = mAuth.getCurrentUser();

        if(crrUser != null){

            goToMainActivity();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUi();
    }

    private void setupUi() {

        setupFirebaseAuth();
        logInSetup();
        signUpSetup();

    }

    private void setupFirebaseAuth() {

        mAuth = FirebaseAuth.getInstance();
    }

    private void signUpSetup() {

        emailSignUpButton = findViewById(R.id.emailSignUpButton);
        emailSignUpButton.setOnClickListener((View v) -> startActivity(new Intent(getApplicationContext(), SignUpWithEmailActivity.class)));

        facebookSignUpButton = findViewById(R.id.facebookSignUpButton);
        twitterSignUpButton = findViewById(R.id.twitterSignUpButton);
    }

    private void logInSetup() {

        emailButton = findViewById(R.id.emailSingInButton);
        emailButton.setOnClickListener((View v) -> {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN);
        });

        facebookButton = findViewById(R.id.facebookSignInButton);
        twitterButton = findViewById(R.id.twitterSignInButton);
    }

    private void goToMainActivity() {startActivity(new Intent(this, MainActivity.class));}
}
