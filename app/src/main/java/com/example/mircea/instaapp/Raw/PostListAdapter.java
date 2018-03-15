package com.example.mircea.instaapp.Raw;

import android.content.Context;
import android.content.Intent;
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

import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.UserProfileActivity;


import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class PostListAdapter extends ArrayAdapter<Post>{

    //Array adapter
    private ArrayList<Post> postList;
    private Context mContext;
    private Post post;

    //Logic
    private boolean  doubleClick = false;
    private Handler doubleHandler;
    private boolean isLike = true;

    //Ui
    private ImageView likeButton;
    private TextView commentsText;

    public PostListAdapter(ArrayList<Post> p, Context c){
        super(c, R.layout.insta_post, p);

        this.postList = p;
        this.mContext = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View postView = convertView;

        if(postView == null){

            LayoutInflater lI;

            lI = LayoutInflater.from(getContext());
            postView = lI.inflate(R.layout.insta_post, null);
        }

        post = getItem(position);

        if(post != null){

            ImageView userProfPic = postView.findViewById(R.id.postUserPicture);

            userProfPic.setOnClickListener(new MainImageClickListener());
            TextView username = postView.findViewById(R.id.postUsername);
            ImageView image = postView.findViewById(R.id.postImage);

            /*handle main image double clicks*/
            TextView likesText = postView.findViewById(R.id.noLikesText);
            commentsText = postView.findViewById(R.id.commentsText);

            likeButton = postView.findViewById(R.id.likeButton);
            ImageView commentsButton = postView.findViewById(R.id.commentButton);
            ImageView shareButton = postView.findViewById(R.id.shareButton);

            if(userProfPic != null){
                /*Set the Profile picture*/
                if(post.getUserProfilePicture() != null){

                    userProfPic.setImageBitmap(post.getUserProfilePicture());

                }else{

                    userProfPic.setImageResource(R.drawable.instagram_default2);
                }
            }

            if(username != null){
                username.setText(post.getUsername());
            }

            if(image != null){
                /*Set the main post picture*/
                if(post.getUserImage() != null){
                    image.setImageBitmap(post.getUserImage());
                }else{
                    image.setImageResource(R.drawable.defaultpost);
                }

            }

            if(likesText != null){
                likesText.setText(post.getLikes() + " likes");
            }

            if(commentsText != null){
                commentsText.setText("See all " + post.getComments() + " comments");
            }

            if(likeButton != null){
                likeButton.setImageResource(R.drawable.whiteheart);
            }

            if(commentsButton != null){
                commentsButton.setImageResource(R.drawable.commentbutton);
            }

            if(shareButton != null){
                shareButton.setImageResource(R.drawable.sharebutton);
            }
        }

        return postView;

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


    private class MainImageClickListener implements View.OnClickListener {
        //double click framework

        @Override
        public void onClick(View view) {

            Intent it = new Intent(mContext, UserProfileActivity.class);
            it.putExtra("Email", post.getEmail());
            mContext.startActivity(it);

        }
    }
}
