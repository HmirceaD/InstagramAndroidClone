package com.example.mircea.instaapp.Raw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mircea.instaapp.R;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    //List things
    private ArrayList<Comment> commentList;
    private Context mContext;
    private Comment comment;

    //Ui
    private ImageView miniProfilePicture;
    private TextView commentUsername;
    private TextView commentText;
    private TextView commentPostedText;
    private TextView answerText;

    public CommentListAdapter(ArrayList<Comment> cL, Context c){
        super(c, R.layout.single_comment, cL);

        this.commentList = cL;
        this.mContext = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View commentView = convertView;

        if(commentView == null){

            LayoutInflater lI = LayoutInflater.from(getContext());
            commentView = lI.inflate(R.layout.single_comment, null);
        }

        comment = getItem(position);

        if(comment != null){

            miniProfilePicture = commentView.findViewById(R.id.commentProfilePicture);
            commentUsername = commentView.findViewById(R.id.commentUserText);
            commentText = commentView.findViewById(R.id.commentText);
            commentPostedText = commentView.findViewById(R.id.commentTimeText);
            answerText = commentView.findViewById(R.id.answerTextView);

            /*Initiate all the values*/
            if(miniProfilePicture != null){

                if(comment.getMiniProfilePicture() != null){

                    miniProfilePicture.setImageBitmap(comment.getMiniProfilePicture());
                }else{

                    miniProfilePicture.setImageResource(R.drawable.instagram_default2);
                }
            }

            if(commentUsername != null){

                commentUsername.setText(comment.getCommentUsername());
            }

            if(commentText != null){

                commentText.setText(comment.getCommentText());
            }

            if(commentPostedText != null){

                commentPostedText.setText(comment.getTimePosted() + " ago");
            }

            if(answerText != null){

                answerText.setText("Answer");
            }
        }

        return commentView;
    }

    @Override
    public int getCount() {return commentList.size();}

    @Nullable
    @Override
    public Comment getItem(int position) {return super.getItem(position);}

    @Override
    public int getPosition(@Nullable Comment item) {return super.getPosition(item);}
}
