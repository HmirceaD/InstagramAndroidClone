package com.example.mircea.instaapp.Listeners;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.ImageButton;

import com.example.mircea.instaapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class FollowClickListener {

    //crrEmail FOLLOWED followedEmail
    private String crrUserEmail;
    //followedEmail WAS FOLLOWED by crrEmail
    private String followedEmail;
    //if this is a follow or unfollow
    private boolean isFollow;

    private ImageButton followButton;

    private final static String followersArray = "Followers";
    private final static String followingArray = "Following";
    private final static String followersNum = "followersNum";
    private final static String followingNum = "followingNum";

    public FollowClickListener(String crrUserEmail, String followedEmail, boolean isFollow, ImageButton followButton){

        this.crrUserEmail = crrUserEmail;
        this.followedEmail = followedEmail;
        this.isFollow = isFollow;
        this.followButton = followButton;

        if(!followedEmail.equals(crrUserEmail)){
            //Handle The datbase for the person that was followed
            followedUserDatabase(followersArray, followersNum, followedEmail);
            //Handle the database for the person that follows
            followedUserDatabase(followingArray, followingNum, crrUserEmail);

        }
    }

    private void followedUserDatabase(String fArray, String fNum, String user) {
        //The user that was followed by the crrUser
        //Todo: Add to the followedEmail database node the crrUSeremail to the Followers array, and increment the followers counter

        firebaseFollowersArray(fArray, user);
        handleFollowerCount(fNum, user, isFollow);
    }

    private void firebaseFollowersArray(String fArray, String user) {

        final DatabaseReference followersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users/" + user + "/" + fArray);

        if(isFollow){
            //follow
            addTheFollow(followersDatabaseReference);
        }else{
            //unfollow
            removeFollow(followersDatabaseReference);
        }
    }

    private void removeFollow(DatabaseReference followersDatabaseReference) {
        //Remove from the follow array
        followersDatabaseReference.child(crrUserEmail).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            //TODO: HANDLE THIS
                        }
                    }
                });
    }

    private void addTheFollow(DatabaseReference followersDatabaseReference) {
        //add to the follow array
        Map<String, Object> followerMap = new HashMap<>();
        followerMap.put(crrUserEmail, "followerId");

        followersDatabaseReference.updateChildren(followerMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            //TODO: HANDLE THIS
                        }
                    }
                });
    }

    private void handleFollowerCount(String fNum, String user, boolean follow) {
        //Handle the followers count variable

        final DatabaseReference followersCount = FirebaseDatabase.getInstance().getReference("Users/" + user + "/" + fNum);

        followersCount.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                if(mutableData.getValue() != null){
                    int value = mutableData.getValue(Integer.class);

                    if(follow){
                        //like
                        value ++;
                    }else{
                        //take like back
                        value --;
                    }
                    mutableData.setValue(value);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
        });
    }


}
