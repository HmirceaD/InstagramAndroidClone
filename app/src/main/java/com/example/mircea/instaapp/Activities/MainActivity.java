package com.example.mircea.instaapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mircea.instaapp.Firebase.SetUserProfilePicture;
import com.example.mircea.instaapp.Listeners.DrawerItemsSelectedListener;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Post;
import com.example.mircea.instaapp.Adapters.PostListAdapter;
import com.example.mircea.instaapp.Raw.User;
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

public class MainActivity extends AppCompatActivity {

    //Firebase Stuff
    private FirebaseAuth mAuth;

    //Ui
    private ListView postsList;
    private ArrayList<Post> posts;
    private static PostListAdapter postAdp;

    private ArrayList<User> users;

    private TextView userText;
    private Button uploadButton;

    private ImageView postImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postImageView = findViewById(R.id.postImage);

        setupDrawer();
        mainLogic();

    }

    @Override
    protected void onResume() {
        super.onResume();


        posts = new ArrayList<>();
        populateLists();
    }

    private void setupDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        initFloatingButton(fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initCustomNavigationDrawer(navigationView);
        navigationView.setNavigationItemSelectedListener(new DrawerItemsSelectedListener(getApplicationContext()));
    }

    //Custom Floating Button behaviour
    private void initFloatingButton(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //Custom Navigation Drawer Behavior
    private void initCustomNavigationDrawer(NavigationView navigationView) {

        if(navigationView != null){

            LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);

            if(linearLayout != null){
                linearLayout.setBackgroundColor(Color.BLACK);
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser crrUser = mAuth.getCurrentUser();

                TextView usernameTextNav = linearLayout.findViewById(R.id.usernameTextNav);
                usernameTextNav.setText(crrUser.getDisplayName());

                TextView userEmailTextNav = linearLayout.findViewById(R.id.userEmailTextNav);
                userEmailTextNav.setText(crrUser.getEmail());

                ImageView userProfilePictureNav = linearLayout.findViewById(R.id.userProfileImageNav);
                SetUserProfilePicture setUserProfilePicture = new SetUserProfilePicture(crrUser, userProfilePictureNav);
            }
        }
    }

    /*Handle the logic*/
    private void mainLogic() {

        setupUi();
        setupList();
    }

    private void setupUi() {

        userText = findViewById(R.id.userText);

        FirebaseUser crrUser = mAuth.getCurrentUser();

        try {
            userText.setText(crrUser.getDisplayName());
        }catch (NullPointerException ex){

            ex.printStackTrace();
            userText.setText("DEFAULT");
        }

        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener((View v) -> startActivity(new Intent(getApplication(), UploadPostActivity.class)));
    }

    /*Setup the ui*/
    private void setupList() {

        postsList = findViewById(R.id.postLists);
        //Todo(4): Add the strings to the string folder
        //Todo(6): TEST AND DOCUMENT ALL THIS !!!!!
        //Todo(7): Resize the user picture

        postsList = findViewById(R.id.postLists);
        users = new ArrayList<>();

    }

    public void populateLists(){

        /*initialize all the posts that the user will see*/

        if(postAdp == null){

            postAdp = new PostListAdapter(posts, getApplicationContext());
            postsList.setAdapter(postAdp);
        }else{
            postAdp.notifyDataSetChanged();
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Post");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                postAdp = new PostListAdapter(posts, getApplicationContext());
                posts.clear();

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    //get the data for the post

                    Post p = data.getValue(Post.class);
                    //get main post image
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

                p.setUserImage(bytes);
                //set the profile picture
                getUserProfilePicture(p, posts, postsList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {}
        });
    }

    private void getUserProfilePicture(Post p, ArrayList<Post> posts, ListView postsList) {

        if(p.getUserImageUri() != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(p.getUserImageUri());

            final long ONE_MEGABYTE = 1024 * 1024*5;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    p.setUserProfilePicture(bytes);
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

    @Override
    protected void onDestroy() {

        try {
            super.onDestroy(); // I use try catch and it doesn't crash any more
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        if(id == R.id.sign_out){

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


}
