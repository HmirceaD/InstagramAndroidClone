package com.example.mircea.instaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SignUpWithEmailActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;

    //Other Stuff
    private Bitmap imageBitmap;
    private Uri pathToImage;
    private static int PICK_IMAGE = 1;
    private UserProfileChangeRequest profileUpdates;

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


                            if(imageBitmap == null || pathToImage == null){

                                 profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(usernameTextField.getText().toString())
                                        .build();
                            }else{

                                String imageUrl = getImageUrl(emailTextField.getText().toString());

                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures").child(imageUrl);

                                UploadTask uploadTask = storageReference.putFile(pathToImage);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(usernameTextField.getText().toString())
                                                .setPhotoUri(taskSnapshot.getDownloadUrl())
                                                .build();
                                    }
                                });
                            }

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

    private String getImageUrl(String em) {

        return em + "*" + pathToImage.getLastPathSegment();
    }
}
