package com.example.mircea.instaapp.Raw;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.time.LocalDateTime;
import java.util.List;

public class Post {

    private String username;
    private String email;
    private Bitmap userProfilePicture;
    private Bitmap userImage;
    private int likes;
    private int comments;
    private long dateTime;
    private String imageUrl;
    private String userImageUri;
    private String pushId;

    public Post(){}

    public Post(int c, int l, String u, String em, long lt, String iUrl, String userProf, String p){

        this.userImageUri = userProf;
        this.email = em;
        this.imageUrl = iUrl;
        this.dateTime = lt;
        this.username = u;
        this.likes = l;
        this.comments = c;
        this.pushId = p;
    }

    public String getPushId() {return pushId;}

    public String getUserImageUri() {return userImageUri;}

    public void setUserProfilePicture(Bitmap userProfilePicture) {this.userProfilePicture = userProfilePicture;}

    public String getImageUrl() {return imageUrl;}

    public String getEmail() {return email;}

    public long getLocalDateTime() { return dateTime;}

    public String getUsername() {
        return username;
    }

    public Bitmap getUserProfilePicture() {
        return userProfilePicture;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public void setUserImage(Bitmap userImage) {this.userImage = userImage;}
}
