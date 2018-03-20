package com.example.mircea.instaapp.Raw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Comment {

    private byte[] miniProfilePicture;
    private String photoUrl;
    private String commentText;
    private String commentUsername;
    private long timePosted;

    public Comment() {}

    public Comment(String photoUrl, String commentUsername, String commentText, long timePosted) {

        this.photoUrl = photoUrl;
        this.commentText = commentText;
        this.commentUsername = commentUsername;
        this.timePosted = timePosted;
    }



    public byte[] getMiniProfilePicture() {return miniProfilePicture;}

    public void setMiniProfilePicture(byte[] miniProfilePicture) {this.miniProfilePicture = miniProfilePicture;}

    public String getPhotoUrl() {return photoUrl;}

    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public String getCommentText() {return commentText;}

    public void setCommentText(String commentText) {this.commentText = commentText;}

    public String getCommentUsername() {return commentUsername;}

    public void setCommentUsername(String commentUsername) {this.commentUsername = commentUsername;}

    public long getTimePosted() {return timePosted;}

    public void setTimePosted(long timePosted) {this.timePosted = timePosted;}
}
