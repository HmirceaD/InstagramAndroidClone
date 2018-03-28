package com.example.mircea.instaapp.Listeners;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.example.mircea.instaapp.Activities.FriendsListActivity;
import com.example.mircea.instaapp.Activities.SettingsActivity;
import com.example.mircea.instaapp.Activities.UploadPostActivity;
import com.example.mircea.instaapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class DrawerItemsSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private String crrUserEmail;
    private Context mContext;

    public DrawerItemsSelectedListener(Context mContext){

        this.mAuth = FirebaseAuth.getInstance();
        this.crrUserEmail = mAuth.getCurrentUser().getEmail();
        this.mContext = mContext;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            new GoToUserProfileListener(crrUserEmail, mContext).goToUserProfile();

        } else if (id == R.id.nav_add_post) {
            mContext.startActivity(new Intent(mContext, UploadPostActivity.class));

        } else if (id == R.id.nav_friends) {
            mContext.startActivity(new Intent(mContext, FriendsListActivity.class));

        } else if (id == R.id.nav_settings) {
            mContext.startActivity(new Intent(mContext, SettingsActivity.class));

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
