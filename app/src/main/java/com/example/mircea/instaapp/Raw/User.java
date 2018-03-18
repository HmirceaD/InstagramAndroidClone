package com.example.mircea.instaapp.Raw;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String email;
    private String username;
    private String profilePicture;
    private static List<String> Posts = new ArrayList<>();
    private int followersNum;
    private int followingNum;

    public User(){}

    public User(String em, String us, int feN, int fiN){

        this.email = em;
        this.username = us;
        this.followersNum = feN;
        this.followingNum = fiN;
    }

    public int getFollowersNum() {return followersNum;}

    public void setFollowersNum(int followersNum) {this.followersNum = followersNum;}

    public int getFollowingNum() {return followingNum;}

    public void setFollowingNum(int followingNum) {this.followingNum = followingNum;}

    public List<String> getPostArray() {return Posts;}

    public void setPostArray(ArrayList<String> postArray) {this.Posts = postArray;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getProfilePicture() {return profilePicture;}

    public void setProfilePicture(String profilePicture) {this.profilePicture = profilePicture;}
}
