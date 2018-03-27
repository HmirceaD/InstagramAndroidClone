package com.example.mircea.instaapp.Listeners;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mircea.instaapp.Raw.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class AddCommentListener {

    private View v;
    private EditText commentsEditText;
    private String crrPostPushId;
    private Context context;

    public AddCommentListener(View v, EditText commentEditText, String crrPostPushId, Context context){

        this.v = v;
        this.commentsEditText = commentEditText;
        this.crrPostPushId = crrPostPushId;
        this.context = context;
        addCommentListener();
    }

    public void addCommentListener(){
        //create the comment object in the database and add it to the post object with specific id

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
                        Toast.makeText(context, "Comment posted succesfully", Toast.LENGTH_SHORT).show();
                        incrementCommentNumber(crrPostPushId);

                    }else{
                        Toast.makeText(context, "Something went wrong bro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void incrementCommentNumber(String pushId) {
        //the counter for how many comments the post has

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post/" + pushId + "/comments");

        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                if(mutableData.getValue() != null){
                    int commentNumber = mutableData.getValue(Integer.class);

                    commentNumber += 1;

                    mutableData.setValue(commentNumber);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
        });

    }

    private Comment buildComment(String commentStr){

        FirebaseUser crrUser = FirebaseAuth.getInstance().getCurrentUser();
        //build the comment
        return new Comment(crrUser.getPhotoUrl().toString(), crrUser.getDisplayName(), commentStr, System.currentTimeMillis());

    }
}
