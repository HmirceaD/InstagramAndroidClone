package com.example.mircea.instaapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.mircea.instaapp.Fragments.FollowersFragment;
import com.example.mircea.instaapp.Fragments.FollowingFragment;
import com.example.mircea.instaapp.R;

public class FollowsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Bundle bundle;

    public FollowsPagerAdapter(Context mContext, FragmentManager fm, Bundle bl){
        super(fm);
        this.mContext = mContext;
        this.bundle = bl;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            FollowersFragment followersFragment = new FollowersFragment();
            followersFragment.setArguments(bundle);
            return followersFragment;
        }else {
            FollowingFragment followingFragment = new FollowingFragment();
            followingFragment.setArguments(bundle);
            return followingFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if(position == 0){
            return mContext.getString(R.string.followers_tab);
        }else if(position == 1){
            return mContext.getString(R.string.following_tab);
        }else{
            return null;
        }
    }
}
