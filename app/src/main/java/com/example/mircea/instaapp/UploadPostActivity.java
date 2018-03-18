package com.example.mircea.instaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.example.mircea.instaapp.Raw.EmailRefactor;
import com.example.mircea.instaapp.Raw.Metadata;
import com.example.mircea.instaapp.Raw.Post;
import com.example.mircea.instaapp.Raw.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UploadPostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Bitmap bitmap;
    private Uri pathToImage;

    //Post info
    private static String currentUser;
    private static long currentTime;
    private String profilePictureUri;

    private static int PICK_IMAGE = 1;
    private static int SUCCESPHOTOUPDATE = 0;

    //Ui
    private Button uploadToFirebaseButton;
    private Button backButton;
    private ImageButton imageUpload;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_IMAGE) {

                pathToImage = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathToImage);
                    imageUpload.setImageBitmap(bitmap);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (NullPointerException ex) {
            imageUpload.setImageResource(R.drawable.defaultpost);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        setupUi();
    }

    private void setupUi() {

        mAuth = FirebaseAuth.getInstance();

        bitmap = null;
        pathToImage = null;
        currentUser = mAuth.getCurrentUser().getDisplayName();
        currentTime = System.currentTimeMillis();

        if(mAuth.getCurrentUser().getPhotoUrl() != null){

            profilePictureUri = mAuth.getCurrentUser().getPhotoUrl().toString();
        }else{

            profilePictureUri = null;
        }

        uploadToFirebaseButton = findViewById(R.id.uploadToFirebaseButton);
        backButton = findViewById(R.id.backButton);
        imageUpload = findViewById(R.id.imageUpload);

        imageUpload.setOnClickListener((View v) -> imageGalleryIntent());
        backButton.setOnClickListener((View v) -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        uploadToFirebaseButton.setOnClickListener((View v) -> uploadPostToFirebase());

    }

    private void uploadPostToFirebase() {

        if(pathToImage != null && bitmap != null) {

            EmailRefactor emRef = new EmailRefactor();

            DatabaseReference postDatabase = FirebaseDatabase.getInstance().getReference("Post");

            String imageUrl = "images/" + createImageUrl(pathToImage.getLastPathSegment());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageUrl);

            postStorage(storageRef);

            postDatabase(postDatabase, imageUrl);

        }
    }

    private void postStorage(StorageReference storageRef){

        /**
         * This pushses the photo to storage
         */

        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                //this won't be needed one I add
                .setCustomMetadata("username", currentUser)
                .setCustomMetadata("time", Long.toString(currentTime))
                .build();

        UploadTask uploadTask = storageRef.putFile(pathToImage, storageMetadata);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                SUCCESPHOTOUPDATE = 1;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                SUCCESPHOTOUPDATE = -1;
            }
        });
    }

    private void postDatabase(DatabaseReference postData, String imageUrl) {



        /**
         * This pushes the post data to the database
         */

        //Get the id
        DatabaseReference pushReference = postData.push();
        String postId = pushReference.getKey();

        String currentEmail = mAuth.getCurrentUser().getEmail();

        Post crrPost = new Post(0, 0, currentUser, currentEmail, currentTime, imageUrl, profilePictureUri, postId);

        //push the post to the post database
        pushReference.setValue(crrPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if(SUCCESPHOTOUPDATE == 1 || SUCCESPHOTOUPDATE == 0){
                    Toast.makeText(getApplicationContext(), "Post uploaded succesfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            }
        });

        //add to the users database

        EmailRefactor emailRefactor = new EmailRefactor();

        String reference = "Users/" + emailRefactor.refactorEmail(mAuth.getCurrentUser().getEmail() + "/Posts");

        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference(reference);

        Map<String, Object> idValue = new HashMap<>();

        idValue.put(postId, "postId");

        usersDatabase.updateChildren(idValue).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Log.d("USER UPDATE", "SUCCES");
                }
            }
        });


    }

    private String createImageUrl(String imageUrl) throws NullPointerException{

        return mAuth.getCurrentUser().getEmail() + "*" + currentTime + "*" + imageUrl;
    }

    private void imageGalleryIntent() {

        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(it, "Select Picture"), PICK_IMAGE);

    }

}
