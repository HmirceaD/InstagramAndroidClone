package com.example.mircea.instaapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mircea.instaapp.Activities.CommentsActivity;
import com.example.mircea.instaapp.HelperClasses.EmailRefactor;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Post;
import com.example.mircea.instaapp.Activities.UserProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostListAdapter extends ArrayAdapter<Post>{

    //String TAG
    final String LIKETAGFAILURE = "LikeTagFailure";

    //Array adapter
    private ArrayList<Post> postList;
    private Context mContext;

    //
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //Logic
    private boolean  doubleClick = false;
    private Handler doubleHandler;
    private boolean isLike = true;

    //inflater
    private LayoutInflater lI;

    private int globalPosition;

    public PostListAdapter(ArrayList<Post> p, Context c){
        super(c, R.layout.insta_post, p);

        this.mAuth = FirebaseAuth.getInstance();
        this.postList = p;
        this.mContext = c;
        this.lI = LayoutInflater.from(mContext);
        this.doubleHandler = new Handler();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //TODO(5): ADD THE CHECK FOR IF LIKED

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

        Glide.with(mContext)
                .load("")
                .apply(new RequestOptions()
                        .placeholder(R.drawable.whiteheart)
                        .fitCenter())
                .into(viewHolder.likeButton);

        Glide.with(mContext)
                .load("")
                .apply(new RequestOptions()
                        .placeholder(R.drawable.commentbutton)
                        .fitCenter())
                .into(viewHolder.commentsButton);

        Glide.with(mContext)
                .load("")
                .apply(new RequestOptions()
                    .placeholder(R.drawable.sharebutton)
                    .fitCenter())
                .into(viewHolder.shareButton);

        Glide.with(viewHolder.image)
                .load(postList.get(globalPosition).getUserImage())
                .into(viewHolder.image);

        if(postList.get(globalPosition).getUserProfilePicture() != null){

            Glide.with(mContext)
                    .load(postList.get(globalPosition).getUserProfilePicture())
                    .into(viewHolder.userProfPic);

        }else{

            Glide.with(mContext)
                    .load(R.drawable.instagram_default2)
                    .into(viewHolder.userProfPic);
        }

        viewHolder.likeButton.setTag(R.id.likeButton, position);
        viewHolder.likeButton.setOnClickListener((View v) -> heartImageClick(v, viewHolder));

        viewHolder.userProfPic.setTag(R.id.postUserPicture, position);
        viewHolder.userProfPic.setOnClickListener(new ProfileImageClickListener());

        viewHolder.image.setOnClickListener((View v) -> doubleClickImage(v, viewHolder));

        viewHolder.commentsButton.setTag(R.id.commentButton, position);
        viewHolder.commentsButton.setOnClickListener(new CommentClickListener());

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Post getItem(int position) {return super.getItem(position);}

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

    private String refactorEmailOfLike() throws NullPointerException{

        EmailRefactor emailRefactor = new EmailRefactor();
        user = mAuth.getCurrentUser();
        return emailRefactor.refactorEmail(user.getEmail());
    }

    private void heartImageClick(View v, ViewHolder viewHolder){

        if(isLike){
            //like the image
            addLikeArray(v);
            handleLikeCounter(v, isLike);

            //handle the ui stuff
            viewHolder.likeButton.setImageResource(R.drawable.redheart);
            viewHolder.likesText.setText((postList.get((Integer)v.getTag(R.id.likeButton)).getLikes() + 1) + " likes");

            isLike = false;

        }else{
            //take like back
            removeLikeArray(v);
            handleLikeCounter(v, isLike);

            //handle the ui stuff
            viewHolder.likeButton.setImageResource(R.drawable.whiteheart);
            viewHolder.likesText.setText(postList.get((Integer)v.getTag(R.id.likeButton)).getLikes() + " likes");

            isLike = true;
        }

    }

    private void removeLikeArray(View v) {

        final String reference = "Post/" + postList.get((Integer) v.getTag(R.id.likeButton)).getPushId() + "/Likes/" + refactorEmailOfLike();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(reference);

        databaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LIKETAGFAILURE, "could remove array of likes");
            }
        });
    }

    private void addLikeArray(View v) {

        final String reference = "Post/" + postList.get((Integer) v.getTag(R.id.likeButton)).getPushId() + "/Likes";
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(reference);

        Map<String, Object> likesMap = new HashMap<>();

        try{
            likesMap.put(refactorEmailOfLike(), "userId");
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }

        databaseReference.updateChildren(likesMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LIKETAGFAILURE, "could add array of likes");
            }
        });
    }

    private void handleLikeCounter(View v, boolean like) {

        final String reference = "Post/" + postList.get((Integer) v.getTag(R.id.likeButton)).getPushId() + "/likes";
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(reference);

            databaseReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    if(mutableData.getValue() != null){
                        int value = mutableData.getValue(Integer.class);

                        if(like){
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


    private void doubleClickImage(View v, ViewHolder viewHolder){
        /*HANDLE THE LOGIC HERE*/
        Runnable r = new Runnable() {
            @Override
            public void run() {
                doubleClick = false;
            }
        };

        if (doubleClick) {

            //add like
            addLikeArray(v);
            handleLikeCounter(v, isLike);

            //handle the ui stuff
            viewHolder.likeButton.setImageResource(R.drawable.redheart);
            viewHolder.likesText.setText(postList.get((Integer)v.getTag(R.id.likeButton) +1) + " likes");

            isLike = false;
            doubleClick = false;

        }else {

            doubleClick = true;
            doubleHandler.postDelayed(r, 500);
        }
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
}
