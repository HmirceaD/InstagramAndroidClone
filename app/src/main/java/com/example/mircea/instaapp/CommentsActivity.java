package com.example.mircea.instaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.example.mircea.instaapp.Raw.BitmapSampleDecoder;
import com.example.mircea.instaapp.Raw.Comment;
import com.example.mircea.instaapp.Raw.CommentListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
                    getUserImage(c.getPhotoUrl(), c, comments, commentsList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void getUserImage(String photoUrl, Comment c, ArrayList<Comment> comments, ListView commentsList) {

        if(photoUrl != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);

            final long ONE_MEGABYTE = 1024 * 1024*5;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    c.setMiniProfilePicture(bytes);
                    comments.add(c);
                    commentsList.setAdapter(commentAdp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            /*no profile picture found*/
            c.setMiniProfilePicture(null);
            comments.add(c);
            commentsList.setAdapter(commentAdp);
        }
    }

    private void setupUi() {

        //setup the firebase auth
        mAuth = FirebaseAuth.getInstance();
        crrUser = mAuth.getCurrentUser();

        //setup the ui elements
        commentsList = findViewById(R.id.commentsList);
        commentsEditText = findViewById(R.id.commentEditText);
        miniProfilePicture = findViewById(R.id.miniProfilePicture);
        getYourProfilePicture();
        postCommentButton = findViewById(R.id.postCommentButton);
        postCommentButton.setOnClickListener(new AddCommentListener());
    }

    private void getYourProfilePicture() {

        String profilePictureUrl = crrUser.getPhotoUrl().toString();

        if(profilePictureUrl != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profilePictureUrl);

            final long ONE_MEGABYTE = 1024 * 1024*5;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Glide.with(miniProfilePicture)
                            .load(bytes)
                            .into(miniProfilePicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            /*no profile picture found*/
            Glide.with(miniProfilePicture)
                    .load(R.drawable.instagram_default2)
                    .into(miniProfilePicture);
        }

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
                            incrementCommentNumber(crrPostPushId);

                        }else{
                            Toast.makeText(getApplicationContext(), "Something went wrong bro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void incrementCommentNumber(String pushId) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post/" + pushId + "/comments");

        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                if(mutableData == null)
                    return null;

                int commentNumber = mutableData.getValue(Integer.class);

                commentNumber += 1;

                mutableData.setValue(commentNumber);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

    }

    private Comment buildComment(String commentStr){
        //build the comment
        return new Comment(crrUser.getPhotoUrl().toString(), crrUser.getDisplayName(), commentStr, System.currentTimeMillis());

    }
}
