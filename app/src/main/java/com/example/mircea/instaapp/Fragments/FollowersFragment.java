package com.example.mircea.instaapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mircea.instaapp.Adapters.FollowsListAdapter;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Friend;

import java.util.ArrayList;

public class FollowersFragment extends Fragment {

    private Bundle bundle;

    private ListView followersListView;
    private FollowsListAdapter followsListAdapter;
    private ArrayList<Friend> followsArray;


    public FollowersFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.followers_fragment_layout, container, false);

        followsArray = getFollowersArray();
        followsListAdapter = new FollowsListAdapter(followsArray, getActivity().getApplicationContext());

        followersListView = view.findViewById(R.id.followersList);
        followersListView.setAdapter(followsListAdapter);

        return view;
    }

    private ArrayList<Friend> getFollowersArray() {

        bundle = this.getArguments();

        return bundle.getParcelableArrayList("followers");
    }
}
