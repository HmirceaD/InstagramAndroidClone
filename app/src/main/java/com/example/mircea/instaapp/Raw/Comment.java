package com.example.mircea.instaapp.Raw;

import android.graphics.Bitmap;

public class Comment {

    private Bitmap miniProfilePicture;
    private String photoUrl;
    private String commentText;
    private String commentUsername;
    private long timePosted;

    public Comment() {}

    public Comment(String photoUrl, String commentUsername, String commentText, long timePosted) {

        this.photoUrl = photoUrl;
        //TODO(7): ADD THE SET IMAGE FROM FIREBASE Here
        setMiniProfilePicture(null);
        this.commentText = commentText;
        this.commentUsername = commentUsername;
        this.timePosted = timePosted;
    }

    public Bitmap getMiniProfilePicture() {return miniProfilePicture;}

    public void setMiniProfilePicture(Bitmap miniProfilePicture) {this.miniProfilePicture = miniProfilePicture;}

    public String getPhotoUrl() {return photoUrl;}

    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public String getCommentText() {return commentText;}

    public void setCommentText(String commentText) {this.commentText = commentText;}

    public String getCommentUsername() {return commentUsername;}

    public void setCommentUsername(String commentUsername) {this.commentUsername = commentUsername;}

    public long getTimePosted() {return timePosted;}

    public void setTimePosted(long timePosted) {this.timePosted = timePosted;}
}
