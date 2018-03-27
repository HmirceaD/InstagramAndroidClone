package com.example.mircea.instaapp.Firebase;


import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mircea.instaapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SetCommentsUserPicture {

    private FirebaseUser crrUser;
    private ImageView miniProfilePicture;

    public SetCommentsUserPicture(FirebaseUser crrUser, ImageView miniProfilePicture){

        this.crrUser = crrUser;
        this.miniProfilePicture = miniProfilePicture;

        getYourProfilePicture();
    }

    private void getYourProfilePicture() {

        String profilePictureUrl = crrUser.getPhotoUrl().toString();

        if (profilePictureUrl != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profilePictureUrl);

            final long ONE_MEGABYTE = 1024 * 1024 * 5;
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
        } else {
            /*no profile picture found*/
            Glide.with(miniProfilePicture)
                    .load(R.drawable.instagram_default2)
                    .into(miniProfilePicture);
        }

    }
}
