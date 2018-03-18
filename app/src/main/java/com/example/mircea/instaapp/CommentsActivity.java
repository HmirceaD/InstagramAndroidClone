package com.example.mircea.instaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mircea.instaapp.Raw.Comment;
import com.example.mircea.instaapp.Raw.CommentListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private ImageView miniProfilePicture;
    private Button postCommentButton;

    //Miscellaneous
    private Intent myIntent;
    private String crrPostPushId;

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
                    comments.add(c);
                    commentsList.setAdapter(commentAdp);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupUi() {

        //setup the firebase auth
        mAuth = FirebaseAuth.getInstance();
        crrUser = mAuth.getCurrentUser();
        //Initiate database reference


        //setup the ui elements
        commentsList = findViewById(R.id.commentsList);
        commentsEditText = findViewById(R.id.commentEditText);
        miniProfilePicture = findViewById(R.id.miniProfilePicture);
        postCommentButton = findViewById(R.id.postCommentButton);
        postCommentButton.setOnClickListener(new AddCommentListener());
    }

    private class AddCommentListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            String commentStr = null;

            if (!commentsEditText.getText().toString().equals("")) {

                commentStr = commentsEditText.getText().toString();

                //wipe editext
                commentsEditText.setText("");

                //save the comment to firebase
                //initiate the comment
                Comment auxComment = buildComment(commentStr);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post/" + crrPostPushId + "/Comments");

                databaseReference.push().setValue(auxComment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Comment posted succesfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Something went wrong bro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private Comment buildComment(String commentStr){
        //build the comment
        return new Comment(crrUser.getPhotoUrl().toString(), crrUser.getDisplayName(), commentStr, System.currentTimeMillis());

    }
}
