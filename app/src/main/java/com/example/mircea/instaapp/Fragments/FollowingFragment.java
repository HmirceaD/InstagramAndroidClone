package com.example.mircea.instaapp.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mircea.instaapp.Adapters.FollowsListAdapter;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Friend;

import java.util.ArrayList;

public class FollowingFragment extends Fragment {

    private Bundle bundle;

    private ListView followingListView;
    private FollowsListAdapter followsListAdapter;
    private ArrayList<Friend> followsArray;

    public FollowingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.following_fragment_layout, container, false);

        followsArray = getFollowersArray();
        followsListAdapter = new FollowsListAdapter(followsArray, getActivity().getApplicationContext());

        followingListView = view.findViewById(R.id.followingList);
        followingListView.setAdapter(followsListAdapter);

        return view;
    }

    private ArrayList<Friend> getFollowersArray() {

        bundle = this.getArguments();

        return bundle.getParcelableArrayList("following");
    }
}
