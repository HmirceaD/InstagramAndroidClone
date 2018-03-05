package com.example.mircea.instaapp;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.mircea.instaapp.Raw.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

public class UploadPostActivity extends AppCompatActivity {

    private Button uploadToFirebaseButton;
    private Button backButton;

    private ImageButton imageUpload;
    private Bitmap bitmap;
    private Uri pathToImage;

    private static String currentUser;
    private static long currentTime;

    private static int PICK_IMAGE = 1;
    private static int SUCCESPHOTOUPDATE = 0;

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

        bitmap = null;
        pathToImage = null;
        currentUser = getCurrentUser();
        currentTime = System.currentTimeMillis();

        uploadToFirebaseButton = findViewById(R.id.uploadToFirebaseButton);
        backButton = findViewById(R.id.backButton);
        imageUpload = findViewById(R.id.imageUpload);

        imageUpload.setOnClickListener((View v) -> imageGalleryIntent());
        backButton.setOnClickListener((View v) -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        uploadToFirebaseButton.setOnClickListener((View v) -> uploadPostToFirebase());

    }

    private void uploadPostToFirebase() {

        if(pathToImage != null && bitmap != null) {

            DatabaseReference postDatabase = FirebaseDatabase.getInstance().getReference("Post");
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + pathToImage.getLastPathSegment());

            postStorage(storageRef);

            Post post = new Post(0, 0, currentUser, currentTime);
            postDatabase(postDatabase, post);

        }
    }

    private void postStorage(StorageReference storageRef){

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

    private void postDatabase(DatabaseReference postData, Post crrPost) {

        postData.push().setValue(crrPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if(SUCCESPHOTOUPDATE == 1){
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
    }

    private String getCurrentUser(){
        /**This will be a firebase thing, but in the mean time hardcodding this will work***/

        Random rng = new Random();
        return "defaultUser" + rng.nextInt(9999) + 1;
    }

    private void imageGalleryIntent() {

        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(it, "Select Picture"), PICK_IMAGE);

    }

}
