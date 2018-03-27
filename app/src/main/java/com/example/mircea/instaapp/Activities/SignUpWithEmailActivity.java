package com.example.mircea.instaapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mircea.instaapp.Firebase.SignUpUserWithEmail;
import com.example.mircea.instaapp.HelperClasses.EmailRefactor;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignUpWithEmailActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;

    //Other Stuff
    private Bitmap imageBitmap;
    private Uri pathToImage;
    private static int PICK_IMAGE = 1;

    private static final String TAG = "TAG MESSAGE";

    private String userPhotoUrl;
    private User temp_user;

    //Ui
    private EditText emailTextField;
    private EditText confEmailTextField;
    private EditText passwordTextField;
    private EditText usernameTextField;
    private Button backButton;
    private Button signUpButton;
    private ImageButton profilePicture;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_IMAGE) {

                pathToImage = data.getData();

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathToImage);
                    profilePicture.setImageBitmap(imageBitmap);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (NullPointerException ex) {
            profilePicture.setImageResource(R.drawable.instagram_default2);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);

        mAuth = FirebaseAuth.getInstance();

        setupUi();
    }

    private void setupUi() {

        //Init the other stuff
        imageBitmap = null;
        pathToImage = null;

        //Init the UI
        //Textfields
        usernameTextField = findViewById(R.id.usernameTextField);
        emailTextField = findViewById(R.id.emailTextField);
        confEmailTextField = findViewById(R.id.confEmailTextField);
        passwordTextField = findViewById(R.id.passwordTextField);

        //Buttons
        profilePicture = findViewById(R.id.profilePicture);
        backButton = findViewById(R.id.backButton);
        signUpButton = findViewById(R.id.signUpButton);

        //Listeners
        signUpButton.setOnClickListener((View v) -> checkLogin());
        profilePicture.setOnClickListener((View v) -> imageGalleryIntent());

    }

    private void imageGalleryIntent() {

        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(it, "Select Picture"), PICK_IMAGE);

    }

    private void checkLogin() {
        try {
            if (usernameTextField.getText().toString().equals("") || usernameTextField.getText().length() < 6) {
                Toast.makeText(this, "Username must be atleast 6 characters long", Toast.LENGTH_SHORT).show();
                clearTextFields(usernameTextField);
                return;
            }

            if (emailTextField.getText().toString().equals("") || emailTextField.getText().length() < 6) {
                Toast.makeText(this, "Email must be valid", Toast.LENGTH_SHORT).show();
                clearTextFields(emailTextField, confEmailTextField);
                return;
            }

            if (!emailTextField.getText().toString().equals(confEmailTextField.getText().toString())) {
                Toast.makeText(this, "Emails must match", Toast.LENGTH_SHORT).show();
                clearTextFields(confEmailTextField);
                return;
            }

            if ((passwordTextField.getText().toString().equals("") || passwordTextField.getText().length() < 6)) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                clearTextFields(passwordTextField);
                return;
            }

        }catch (NullPointerException ex){

            Toast.makeText(this, "Please fill out all the forms", Toast.LENGTH_SHORT).show();
        }

        SignUpUserWithEmail signUpUserWithEmail = new SignUpUserWithEmail(temp_user, pathToImage, userPhotoUrl);

        signUpUserWithEmail.firebaseSignUpWithEmail(mAuth, emailTextField, passwordTextField, usernameTextField,
                confEmailTextField, imageBitmap, getApplicationContext());

    }

    public void clearTextFields(EditText... textFields){

        for(EditText aux:textFields){
            aux.setText("");
        }

    }
}
