package com.example.mircea.instaapp.Listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.mircea.instaapp.Activities.UserProfileActivity;

public class GoToUserProfileListener{

    private String userEmail;
    private Context mContext;

    public GoToUserProfileListener(String userEmail, Context mContext){

        this.userEmail = userEmail;
        this.mContext = mContext;
    }

    public void goToUserProfile(){
        Intent it = new Intent(mContext, UserProfileActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        it.putExtra("Email", userEmail);
        mContext.startActivity(it);
    }

    public class ProfileClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            goToUserProfile();
        }
    }
}
