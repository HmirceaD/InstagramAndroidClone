package com.example.mircea.instaapp.Firebase;

import android.support.annotation.NonNull;
import android.widget.ListView;

import com.example.mircea.instaapp.Adapters.PostListAdapter;
import com.example.mircea.instaapp.Raw.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SetPostsList {

    public SetPostsList(){}

    public void getPostImage(Post p, ArrayList<Post> posts, ListView postsList, PostListAdapter postAdp) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(p.getImageUrl());

        final long ONE_MEGABYTE = 1024*1024 *5;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                p.setUserImage(bytes);

                //set the profile picture
                getUserProfilePicture(p, posts, postsList, postAdp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {}
        });
    }

    private void getUserProfilePicture(Post p, ArrayList<Post> posts, ListView postsList, PostListAdapter postAdp) {
        if (p.getUserImageUri() != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(p.getUserImageUri());

            final long ONE_MEGABYTE = 1024 * 1024 * 5;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    p.setUserProfilePicture(bytes);
                    posts.add(p);
                    postsList.setAdapter(postAdp);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        } else {
            /*no profile picture found*/
            p.setUserProfilePicture(null);
            posts.add(p);
            postsList.setAdapter(postAdp);
        }
    }
}
