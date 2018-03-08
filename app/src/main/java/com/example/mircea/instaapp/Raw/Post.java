package com.example.mircea.instaapp.Raw;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.time.LocalDateTime;
import java.util.List;

public class Post {

    private String username;
    private ImageView userProfilePicture;
    private Bitmap userImage;
    private int likes;
    private int comments;
    private long dateTime;
    private String imageUrl;

    public Post(){}

    public Post(int c, int l, String u, long lt, String iUrl){

        this.imageUrl = iUrl;
        this.dateTime = lt;
        this.username = u;
        this.likes = l;
        this.comments = c;
    }

    public String getImageUrl() {return imageUrl;}

    public long getLocalDateTime() { return dateTime;}

    public String getUsername() {
        return username;
    }

    public ImageView getUserProfilePicture() {
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
