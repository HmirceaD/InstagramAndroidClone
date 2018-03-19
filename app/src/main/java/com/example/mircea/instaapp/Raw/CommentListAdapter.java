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
import java.util.concurrent.TimeUnit;

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

            //TODO(7): DOWNLOAD THE USER IMAGE POR FAVOR
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

                getTime(comment.getTimePosted());
            }

            if(answerText != null){

                answerText.setText("Answer");
            }
        }

        return commentView;
    }

    private void getTime(long mili){

        long difference = System.currentTimeMillis() - mili;
        long time = TimeUnit.MILLISECONDS.toSeconds(difference);

        if(time >= 60){

            time = TimeUnit.MILLISECONDS.toMinutes(difference);

            if(time >= 60){
                time = TimeUnit.MINUTES.toHours(time);

                if(time >= 24){

                    time = TimeUnit.HOURS.toDays(time);
                    commentPostedText.setText(time + "d ago");
                }else{
                    commentPostedText.setText(time + "h ago");
                }
            }else {
                commentPostedText.setText(time + "m ago");
            }
        }else{
            commentPostedText.setText(time + "s ago");
        }
    }

    @Override
    public int getCount() {return commentList.size();}

    @Nullable
    @Override
    public Comment getItem(int position) {return super.getItem(position);}

    @Override
    public int getPosition(@Nullable Comment item) {return super.getPosition(item);}
}
