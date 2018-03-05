package com.example.mircea.instaapp.Raw;

import android.widget.ImageView;

import java.time.LocalDateTime;
import java.util.List;

public class Post {

    private String username;
    private ImageView userProfilePicture;
    private ImageView userImage;
    private int likes;
    private int comments;
    private long dateTime;

    public Post(){}

    public Post(int c, int l, String u, long lt){

        this.dateTime = lt;
        this.username = u;
        this.likes = l;
        this.comments = c;
    }

    public long getLocalDateTime() { return dateTime;}

    public String getUsername() {
        return username;
    }

    public ImageView getUserProfilePicture() {
        return userProfilePicture;
    }

    public ImageView getUserImage() {
        return userImage;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }
}
