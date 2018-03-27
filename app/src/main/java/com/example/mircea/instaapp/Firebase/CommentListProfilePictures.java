package com.example.mircea.instaapp.Firebase;


import android.support.annotation.NonNull;
import android.widget.ListView;

import com.example.mircea.instaapp.Adapters.CommentListAdapter;
import com.example.mircea.instaapp.Raw.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CommentListProfilePictures {

    public void getUserImage(String photoUrl, Comment c, ArrayList<Comment> comments, ListView commentsList, CommentListAdapter commentAdp) {

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
}
