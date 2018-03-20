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

import com.example.mircea.instaapp.Raw.BitmapSampleDecoder;
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
    private TextView postsNumber;
    private TextView followersNumber;
    private TextView followingNumber;

    //Logic
    private int postsCounter = 0;

    //posts list
    private ListView postsList;
    private PostListAdapter postAdp;
    private ArrayList<Post> posts;

    //User profile
    private User crrUser;

    //Miscelasnios nu stiu cum se scrie plm
    private Intent myIntent;
    private String crrUserEmail;

    //Initialize new Intent


    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null && intent.hasExtra("Email")){

            myIntent = intent;
            EmailRefactor emailRefactor = new EmailRefactor();
            crrUserEmail = emailRefactor.refactorEmail(myIntent.getStringExtra("Email"));
        }
        super.onNewIntent(intent);
    }

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
        postsNumber = findViewById(R.id.postsNumber);
        followersNumber = findViewById(R.id.followersNumber);
        followingNumber = findViewById(R.id.followingNumber);

        postsList = findViewById(R.id.postsView);
        posts = new ArrayList<>();

        postAdp = new PostListAdapter(posts, getApplication());
        postsList.setAdapter(postAdp);

    }

    private void setupFirebase() {

        mAuth = FirebaseAuth.getInstance();

        EmailRefactor emailRefactor = new EmailRefactor();

        if(myIntent == null){
            myIntent = getIntent();
            crrUserEmail = emailRefactor.refactorEmail(myIntent.getStringExtra("Email"));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(crrUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    //set the user's username
                    String usernameStr = dataSnapshot.child("username").getValue().toString();
                    usernameText.setText(usernameStr);

                    //set the user's profile picture
                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue().toString();
                    setProfilePicture(profilePictureUrl);

                    //set the number of followers
                    String followersStr = dataSnapshot.child("followersNum").getValue().toString();
                    followersNumber.setText(followersStr);

                    //set the number of follows
                    String followsStr = dataSnapshot.child("followingNum").getValue().toString();
                    followingNumber.setText(followsStr);

                    //get all the user's posts
                    for(DataSnapshot id: dataSnapshot.child("Posts").getChildren()){

                        if(id != null){
                            addPostToListView(id.getKey());

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void addPostToListView(String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post/" + key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    postAdp = new PostListAdapter(posts, getApplicationContext());

                    Post p = dataSnapshot.getValue(Post.class);
                    postsNumber.setText(Integer.toString(++postsCounter));

                    getPostImage(p, posts, postsList);


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void getPostImage(Post p, ArrayList<Post> posts, ListView postsList) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(p.getImageUrl());

        final long ONE_MEGABYTE = 1024*1024 *5;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                p.setUserImage(bitmap);
                //get the profile picture
                p.setUserProfilePicture(bitmap);
                //set the profile picture
                getUserProfilePicture(p, posts, postsList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void getUserProfilePicture(Post p, ArrayList<Post> posts, ListView postsList) {
        if(p.getUserImageUri() != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(p.getUserImageUri());

            final long ONE_MEGABYTE = 1024 * 1024*5;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    p.setUserProfilePicture(bitmap);
                    posts.add(p);
                    postsList.setAdapter(postAdp);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {}
            });

        }else{
            /*no profile picture found*/
            p.setUserProfilePicture(null);
            posts.add(p);
            postsList.setAdapter(postAdp);
        }

    }

    private void setProfilePicture(String profilePictureUrl) {
        /*Set the profile picture*/

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profilePictureUrl);

        final long ONE_MEGABYTE = 1024 * 1024*5;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                if(bytes != null){

                    BitmapSampleDecoder bitmapSampleDecoder = new BitmapSampleDecoder(bytes, profilePicture.getHeight(), profilePicture.getWidth());

                    Bitmap profileBitmap = bitmapSampleDecoder.decodeBitmap();

                    profilePicture.setImageBitmap(profileBitmap);
                }else{

                    profilePicture.setImageResource(R.drawable.instagram_default2);
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                profilePicture.setImageResource(R.drawable.instagram_default2);

                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
