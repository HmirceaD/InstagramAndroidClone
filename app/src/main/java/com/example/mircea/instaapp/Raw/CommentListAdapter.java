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

import com.bumptech.glide.Glide;
import com.example.mircea.instaapp.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    //List things
    private ArrayList<Comment> commentList;
    private Context mContext;
    private Comment comment;
    private LayoutInflater lI;
    private int globalPosition;

    public CommentListAdapter(ArrayList<Comment> cL, Context c){
        super(c, R.layout.single_comment, cL);

        this.commentList = cL;
        this.mContext = c;
        lI = LayoutInflater.from(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        globalPosition = position;

        if(convertView == null){

            convertView = lI.inflate(R.layout.single_comment, null);
            viewHolder = new ViewHolder();

            viewHolder.miniProfilePicture = convertView.findViewById(R.id.commentProfilePicture);
            viewHolder.commentUsername = convertView.findViewById(R.id.commentUserText);
            viewHolder.commentText = convertView.findViewById(R.id.commentText);
            viewHolder.commentPostedText = convertView.findViewById(R.id.commentTimeText);
            viewHolder.answerText = convertView.findViewById(R.id.answerTextView);

            /*Initiate all the values*/
            if(viewHolder.commentUsername != null){

                viewHolder.commentUsername.setText(commentList.get(globalPosition).getCommentUsername());
            }

            if(viewHolder.commentText != null){

                viewHolder.commentText.setText(commentList.get(globalPosition).getCommentText());
            }

            if(viewHolder.commentPostedText != null){

                getTime(commentList.get(globalPosition).getTimePosted(), viewHolder);
            }

            if(viewHolder.answerText != null){

                viewHolder.answerText.setText("Answer");
            }

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (CommentListAdapter.ViewHolder) convertView.getTag();
        }

        if(commentList.get(globalPosition).getMiniProfilePicture() != null){

            Glide.with(viewHolder.miniProfilePicture)
                    .load(commentList.get(globalPosition).getMiniProfilePicture())
                    .into(viewHolder.miniProfilePicture);
        }else{
            Glide.with(viewHolder.miniProfilePicture)
                    .load(R.drawable.instagram_default2)
                    .into(viewHolder.miniProfilePicture);
        }

        return convertView;
    }

    static class ViewHolder{
        ImageView miniProfilePicture;
        TextView commentUsername;
        TextView commentText;
        TextView commentPostedText;
        TextView answerText;

    }

    private void getTime(long mili, ViewHolder viewHolder){

        long difference = System.currentTimeMillis() - mili;
        long time = TimeUnit.MILLISECONDS.toSeconds(difference);

        if(time >= 60){

            time = TimeUnit.MILLISECONDS.toMinutes(difference);

            if(time >= 60){
                time = TimeUnit.MINUTES.toHours(time);

                if(time >= 24){

                    time = TimeUnit.HOURS.toDays(time);
                    viewHolder.commentPostedText.setText(time + "d ago");
                }else{
                    viewHolder.commentPostedText.setText(time + "h ago");
                }
            }else {
                viewHolder.commentPostedText.setText(time + "m ago");
            }
        }else{
            viewHolder.commentPostedText.setText(time + "s ago");
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
