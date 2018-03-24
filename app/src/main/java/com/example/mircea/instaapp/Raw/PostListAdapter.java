package com.example.mircea.instaapp.Raw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide.*;
import com.bumptech.glide.request.RequestOptions;
import com.example.mircea.instaapp.CommentsActivity;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.UserProfileActivity;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class PostListAdapter extends ArrayAdapter<Post>{

    //Array adapter
    private ArrayList<Post> postList;
    private Context mContext;

    //Logic
    private boolean  doubleClick = false;
    private Handler doubleHandler;
    private boolean isLike = true;

    //inflater
    private LayoutInflater lI;

    private int globalPosition;

    public PostListAdapter(ArrayList<Post> p, Context c){
        super(c, R.layout.insta_post, p);

        this.postList = p;
        this.mContext = c;
        lI = LayoutInflater.from(mContext);
        doubleHandler = new Handler();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //TODO(8): TAKE GLIDE OUT

        ViewHolder viewHolder;
        globalPosition = position;

        if(convertView == null){
            convertView = lI.from(mContext).inflate(R.layout.insta_post, null);
            viewHolder = new ViewHolder();

            viewHolder.userProfPic = convertView.findViewById(R.id.postUserPicture);
            viewHolder.username = convertView.findViewById(R.id.postUsername);
            viewHolder.image = convertView.findViewById(R.id.postImage);
            viewHolder.likesText = convertView.findViewById(R.id.noLikesText);
            viewHolder.commentsText = convertView.findViewById(R.id.commentsText);
            viewHolder.likeButton = convertView.findViewById(R.id.likeButton);
            viewHolder.commentsButton = convertView.findViewById(R.id.commentButton);
            viewHolder.shareButton = convertView.findViewById(R.id.shareButton);
                //Set the Profile picture

            convertView.setTag(viewHolder);

        }else{

            viewHolder = (PostListAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.username.setText(postList.get(globalPosition).getUsername());

        viewHolder.likesText.setText(postList.get(globalPosition).getLikes() + " likes");

        viewHolder.commentsText.setText("See all " + postList.get(globalPosition).getComments() + " comments");

        viewHolder.likeButton.setImageResource(R.drawable.whiteheart);

        viewHolder.commentsButton.setImageResource(R.drawable.commentbutton);

        viewHolder.shareButton.setImageResource(R.drawable.sharebutton);

        Glide.with(viewHolder.image)
                .load(postList.get(globalPosition).getUserImage())
                .into(viewHolder.image);

        if(postList.get(globalPosition).getUserProfilePicture() != null){

            Glide.with(viewHolder.userProfPic)
                    .load(postList.get(globalPosition).getUserProfilePicture())
                    .into(viewHolder.userProfPic);

        }else{

            Glide.with(viewHolder.userProfPic)
                    .load(R.drawable.instagram_default2)
                    .into(viewHolder.userProfPic);
        }

        viewHolder.userProfPic.setTag(R.id.postUserPicture, position);
        viewHolder.userProfPic.setOnClickListener(new ProfileImageClickListener());

        viewHolder.image.setOnClickListener((View v) -> {

            doubleClickImage(v, viewHolder);
        });

        viewHolder.commentsButton.setTag(R.id.commentButton,position);
        viewHolder.commentsButton.setOnClickListener(new CommentClickListener());

        return convertView;

    }

    static class ViewHolder{
        ImageView userProfPic;
        TextView username;
        ImageView image;
        TextView likesText;
        TextView commentsText;
        ImageView likeButton;
        ImageView commentsButton;
        ImageView shareButton;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Post getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Post item) {
        return super.getPosition(item);
    }


    private class ProfileImageClickListener implements View.OnClickListener {
        //double click framework

        @Override
        public void onClick(View view) {

            Intent it = new Intent(mContext, UserProfileActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.putExtra("Email", postList.get((Integer)view.getTag(R.id.postUserPicture)).getEmail());
            mContext.startActivity(it);
        }
    }

    private class CommentClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            Intent it = new Intent(mContext, CommentsActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.putExtra("PushId", postList.get((Integer)view.getTag(R.id.commentButton)).getPushId());
            mContext.startActivity(it);
        }
    }

    public void doubleClickImage(View v, ViewHolder viewHolder){
        /*HANDLE THE LOGIC HERE*/
        Runnable r = new Runnable() {
            @Override
            public void run() {

                doubleClick = false;
            }
        };

        if (doubleClick) {

            if(isLike){

                viewHolder.likeButton.setImageResource(R.drawable.redheart);

                isLike = false;
            }else{

                viewHolder.likeButton.setImageResource(R.drawable.whiteheart);
                isLike = true;
            }

            doubleClick = false;

        }else {

            doubleClick = true;
            doubleHandler.postDelayed(r, 500);
        }
    }
}
