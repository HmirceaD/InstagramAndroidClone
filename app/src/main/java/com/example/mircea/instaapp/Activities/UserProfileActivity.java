package com.example.mircea.instaapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mircea.instaapp.Firebase.SetPostsList;
import com.example.mircea.instaapp.HelperClasses.EmailRefactor;
import com.example.mircea.instaapp.Listeners.FollowClickListener;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Post;
import com.example.mircea.instaapp.Adapters.PostListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private ImageButton followButton;

    //Logic
    private int postsCounter = 0;
    private ArrayList<String> followersArray = new ArrayList<>();
    private boolean isFollow;

    //posts list
    private ListView postsList;
    private PostListAdapter postAdp;
    private ArrayList<Post> posts;

    //User profile
    private FirebaseUser crrUser;

    //Miscelasnios nu stiu cum se scrie plm
    private Intent myIntent;
    private String pageUserEmail;

    @Override
    protected void onNewIntent(Intent intent) {

        if(intent != null && intent.hasExtra("Email")){

            myIntent = intent;
            EmailRefactor emailRefactor = new EmailRefactor();
            pageUserEmail = emailRefactor.refactorEmail(myIntent.getStringExtra("Email"));
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

        initCurrentUserEmail();

        initFirebase();

        //check if crr user already has follow or not

        profilePicture = findViewById(R.id.profilePicture);
        usernameText = findViewById(R.id.usernameText);
        postsNumber = findViewById(R.id.postsNumber);
        followersNumber = findViewById(R.id.followersNumber);
        followingNumber = findViewById(R.id.followingNumber);
        followButton = findViewById(R.id.followButton);

        postsList = findViewById(R.id.postsView);
        posts = new ArrayList<>();

        postAdp = new PostListAdapter(posts, getApplication());
        postsList.setAdapter(postAdp);

    }

    private void setupFirebase() {
        //Get the actual info of the user
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(pageUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    //set the user's username
                    String usernameStr = dataSnapshot.child("username").getValue().toString();
                    usernameText.setText(usernameStr);

                    //set the user's profile picture
                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue().toString();
                    setProfilePicture(profilePictureUrl);

                    //check the number of follows and who they are
                    for(DataSnapshot data: dataSnapshot.child("Followers").getChildren()){
                        followersArray.add(data.getKey());
                    }

                    initFollows();

                    String followerStr = dataSnapshot.child("followersNum").getValue().toString();
                    followersNumber.setText(followerStr);

                    //set the number of follows
                    String followingStr = dataSnapshot.child("followingNum").getValue().toString();
                    followingNumber.setText(followingStr);

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

    private boolean initCurrentUserEmail() {

        EmailRefactor emailRefactor = new EmailRefactor();

        if(myIntent == null){
            myIntent = getIntent();
            pageUserEmail = emailRefactor.refactorEmail(myIntent.getStringExtra("Email"));
        }
        return false;
    }

    private void initFirebase() {

        mAuth = FirebaseAuth.getInstance();
        crrUser = mAuth.getCurrentUser();
    }

    private void initFollows(){

        //initialize if the current user already follows the page user
        EmailRefactor emailRefactor = new EmailRefactor();

        String crrUserString = emailRefactor.refactorEmail(crrUser.getEmail());
        String pageUserString = emailRefactor.refactorEmail(pageUserEmail);

        isFollow = checkFollows(crrUserString);

        if(!isFollow){
            followButton.setImageResource(R.drawable.unfollow_user_button);
        }

        followButton.setOnClickListener(new FollowClickClass());

    }

    private boolean checkFollows(String crrStr) {
        /**
         * true == the crr user doesnt already follow the page user
         *
         */
        for(String follower: followersArray){
            if(crrStr.equals(follower)){
                return false;
            }
        }
        return true;
    }

    private void addPostToListView(String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post/" + key);

        SetPostsList setPostList = new SetPostsList();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    postAdp = new PostListAdapter(posts, getApplicationContext());

                    Post p = dataSnapshot.getValue(Post.class);
                    postsNumber.setText(Integer.toString(++postsCounter));

                    setPostList.getPostImage(p, posts, postsList, postAdp);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setProfilePicture(String profilePictureUrl) {
        /*Set the profile picture*/

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profilePictureUrl);

        final long ONE_MEGABYTE = 1024 * 1024*5;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                if(bytes != null){

                    Glide.with(profilePicture)
                            .load(bytes)
                            .into(profilePicture);
                }else{

                    Glide.with(profilePicture)
                            .load(R.drawable.instagram_default2)
                            .into(profilePicture);
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Glide.with(profilePicture)
                        .load(R.drawable.instagram_default2)
                        .into(profilePicture);

                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class FollowClickClass implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            EmailRefactor emailRefactor = new EmailRefactor();

            FollowClickListener followClickListener = new FollowClickListener(emailRefactor.refactorEmail(crrUser.getEmail()),
            emailRefactor.refactorEmail(pageUserEmail), isFollow, followButton);

            if(!emailRefactor.refactorEmail(crrUser.getEmail()).equals(emailRefactor.refactorEmail(pageUserEmail))){

                changeClientCode(emailRefactor.refactorEmail(pageUserEmail));

            }

        }
    }

    private void changeClientCode(String crrUserString) {
        //add the changes to the client code
        if(isFollow){
            //Follow
            followButton.setImageResource(R.drawable.unfollow_user_button);
            followersArray.add(crrUserString);
            isFollow = false;

        }else{
            //Unfollow
            followButton.setImageResource(R.drawable.follow_user_button);
            followersArray.remove(crrUserString);
            isFollow = true;
        }
    }
}
