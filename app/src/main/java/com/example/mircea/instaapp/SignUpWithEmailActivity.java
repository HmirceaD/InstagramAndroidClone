package com.example.mircea.instaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpWithEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailTextField;
    private EditText confEmailTextField;
    private EditText passwordTextField;
    private EditText usernameTextField;
    private Button backButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);

        mAuth = FirebaseAuth.getInstance();

        setupUi();
    }

    private void setupUi() {

        usernameTextField = findViewById(R.id.usernameTextField);
        emailTextField = findViewById(R.id.emailTextField);
        confEmailTextField = findViewById(R.id.confEmailTextField);
        passwordTextField = findViewById(R.id.passwordTextField);

        backButton = findViewById(R.id.backButton);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener((View v) -> checkLogin());

    }

    private void checkLogin() {

        if((!emailTextField.getText().toString().equals("") && emailTextField.getText().length() > 6) &&
                (!confEmailTextField.getText().toString().equals("") && confEmailTextField.getText().length() > 6) &&
                (!passwordTextField.getText().toString().equals("") && passwordTextField.getText().length() > 6) &&
                (!usernameTextField.getText().toString().equals("") && usernameTextField.getText().length() > 6) &&
                (emailTextField.getText().toString().equals(confEmailTextField.getText().toString()))){

            mAuth.createUserWithEmailAndPassword(emailTextField.getText().toString(), passwordTextField.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(usernameTextField.getText().toString()).build();

                            try{
                                user.updateProfile(profileUpdates);

                                Toast.makeText(getApplicationContext(), "We good", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            }catch(NullPointerException ex){

                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                         public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
        }
    }
}
