package com.example.mircea.instaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mircea.instaapp.Raw.EmailRefactor;
import com.example.mircea.instaapp.Raw.Post;
import com.example.mircea.instaapp.Raw.PostListAdapter;
import com.example.mircea.instaapp.Raw.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    //Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    //Ui
    private ImageView profilePicture;
    private TextView usernameText;

    //posts list
    private ListView postsList;
    private PostListAdapter postAdp;
    private ArrayList<Post> posts;

    //User profile
    private User crrUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        setupUi();
        setupFirebase();
    }

    private void setupUi() {

        profilePicture = findViewById(R.id.profilePicture);
        usernameText = findViewById(R.id.usernameText);

    }

    private void setupFirebase() {

        mAuth = FirebaseAuth.getInstance();

        Intent it = getIntent();
        EmailRefactor emailRefactor = new EmailRefactor();

        String crrEmail = emailRefactor.refactorEmail(it.getStringExtra("Email"));

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(crrEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    String usernameStr = dataSnapshot.child("username").getValue().toString();
                    usernameText.setText(usernameStr);

                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue().toString();

                    //get the profile picture
                    setProfilePicture(profilePictureUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setProfilePicture(String profilePictureUrl) {
        /*Set the profile picture*/

        //StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(p.getUserImageUri());
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profilePictureUrl);

        final long ONE_MEGABYTE = 1024 * 1024*5;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap profileBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                profilePicture.setImageBitmap(profileBitmap);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
