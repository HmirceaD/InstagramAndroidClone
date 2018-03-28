package com.example.mircea.instaapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mircea.instaapp.Firebase.CommentListProfilePictures;
import com.example.mircea.instaapp.Firebase.SetUserProfilePicture;
import com.example.mircea.instaapp.Listeners.AddCommentListener;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Comment;
import com.example.mircea.instaapp.Adapters.CommentListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    //List
    private CommentListAdapter commentAdp;
    private ArrayList<Comment> comments;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser crrUser;

    //Ui
    private ListView commentsList;
    private EditText commentsEditText;
    private CircleImageView miniProfilePicture;
    private Button postCommentButton;

    //Miscellaneous
    private Intent myIntent;
    private String crrPostPushId;
    //firebase helpers
    private CommentListProfilePictures commentListProfilePictures;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null && intent.hasExtra("PushId")){

            myIntent = intent;
            crrPostPushId = myIntent.getStringExtra("PushId");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setupUi();
        setupList();
    }

    private void setupList() {

        if(myIntent == null){

            myIntent = getIntent();

            if(myIntent.hasExtra("PushId")){
                crrPostPushId = myIntent.getStringExtra("PushId");

            }else{
                crrPostPushId = null;
            }
        }

        comments = new ArrayList<>();
        commentAdp = new CommentListAdapter(comments, getApplication());
        commentsList.setAdapter(commentAdp);
        commentListProfilePictures = new CommentListProfilePictures();

        populateList();
    }

    private void populateList() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post/" + crrPostPushId + "/Comments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                commentAdp = new CommentListAdapter(comments, getApplicationContext());
                comments.clear();

                for(DataSnapshot data: dataSnapshot.getChildren()){

                    Comment c = data.getValue(Comment.class);
                    commentListProfilePictures.getUserImage(c.getPhotoUrl(), c, comments, commentsList, commentAdp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setupUi() {

        //setup the firebase auth
        mAuth = FirebaseAuth.getInstance();
        crrUser = mAuth.getCurrentUser();

        //setup the ui elements
        commentsList = findViewById(R.id.commentsList);
        commentsEditText = findViewById(R.id.commentEditText);
        miniProfilePicture = findViewById(R.id.miniProfilePicture);

        new SetUserProfilePicture(crrUser, miniProfilePicture);

        postCommentButton = findViewById(R.id.postCommentButton);
        postCommentButton.setOnClickListener((View v) -> new AddCommentListener(v, commentsEditText, crrPostPushId, getApplicationContext()));
    }
}
