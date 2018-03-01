package com.example.mircea.instaapp.Raw;

import android.widget.ImageView;

import java.util.List;

public class Post {

    private String username;
    private ImageView userProfilePicture;
    private ImageView userImage;
    private int likes;
    private int comments;

    public Post(String u, ImageView uPP, ImageView uI, int l, int c){

        this.username = u;
        this.userProfilePicture = uPP;
        this.userImage = uI;
        this.likes = l;
        this.comments = c;
    }

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
