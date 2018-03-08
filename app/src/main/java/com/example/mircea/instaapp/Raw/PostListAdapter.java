package com.example.mircea.instaapp.Raw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mircea.instaapp.R;

import java.util.ArrayList;

public class PostListAdapter extends ArrayAdapter<Post>{

    //Todo(7) Refactor this to work for images from the db

    private ArrayList<Post> postList;
    private Context mContext;

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

        Post post = getItem(position);

        if(post != null){

            ImageView userProfPic = postView.findViewById(R.id.postUserPicture);
            TextView username = postView.findViewById(R.id.postUsername);
            ImageView image = postView.findViewById(R.id.postImage);
            TextView likesText = postView.findViewById(R.id.noLikesText);
            TextView commentsText = postView.findViewById(R.id.commentsText);

            ImageView likeButton = postView.findViewById(R.id.likeButton);
            ImageView commentsButton = postView.findViewById(R.id.commentButton);
            ImageView shareButton = postView.findViewById(R.id.shareButton);

            if(userProfPic != null){
                //userProfPic.setImageDrawable(post.getUserProfilePicture().getDrawable());
                userProfPic.setImageResource(R.drawable.instagram_default);
            }

            if(username != null){
                username.setText(post.getUsername());
            }

            if(image != null){

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
                likeButton.setImageResource(R.drawable.likebutton);
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
}
