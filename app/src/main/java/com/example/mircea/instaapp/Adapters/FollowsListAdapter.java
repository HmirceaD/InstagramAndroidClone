package com.example.mircea.instaapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Friend;

import java.util.ArrayList;

public class FollowsListAdapter extends ArrayAdapter<Friend> {

    private ArrayList<Friend> friendArrayList;
    private Context mContext;

    private LayoutInflater lI;

    public FollowsListAdapter(ArrayList<Friend> fA, Context c){
        super(c, R.layout.follows_row, fA);

        this.friendArrayList = fA;
        this.mContext = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = lI.from(mContext).inflate(R.layout.follows_row, null);
            viewHolder = new ViewHolder();

            viewHolder.followsProfilePicture = convertView.findViewById(R.id.followProfilePicture);
            viewHolder.followsEmail = convertView.findViewById(R.id.followEmail);

            convertView.setTag(viewHolder);
        }else{

            viewHolder = (FollowsListAdapter.ViewHolder) convertView.getTag();
        }

        //init the ui
        if(friendArrayList.get(position).getPhotoUrl() != null){
            Glide.with(mContext)
                    .load(friendArrayList.get(position).getPhotoUrl())
                    .into(viewHolder.followsProfilePicture);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.instagram_default2)
                    .into(viewHolder.followsProfilePicture);
        }


        viewHolder.followsEmail.setText(friendArrayList.get(position).getEmail());

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Friend getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Friend item) {
        return super.getPosition(item);
    }

    public static class ViewHolder{

        ImageView followsProfilePicture;
        TextView followsEmail;

    }
}
