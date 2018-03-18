package com.example.mircea.instaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.mircea.instaapp.Raw.Comment;
import com.example.mircea.instaapp.Raw.CommentListAdapter;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    //List
    private CommentListAdapter commentAdp;
    private ArrayList<Comment> comments;

    //Ui
    private ListView commentsList;
    private EditText commentsEditText;
    private ImageView miniProfilePicture;
    private Button postCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setupUi();
        setupList();
    }

    private void setupList() {

        comments = new ArrayList<>();
        commentAdp = new CommentListAdapter(comments, getApplication());
        commentsList.setAdapter(commentAdp);

        populateList();
    }

    private void populateList() {

        comments.add(new Comment("z", "Doru", "AM FTUTUTUUTUT PE VECINA", 32));
        comments.add(new Comment("z", "poru", "zzzzz", 1));
        comments.add(new Comment("z", "moru", "AM dsaA", 4));
        comments.add(new Comment("z", "coru", "coaieA", 56));
        comments.add(new Comment("z", "loru", "ZILE GUTA", 1112));
    }

    private void setupUi() {

        commentsList = findViewById(R.id.commentsList);
        commentsEditText = findViewById(R.id.commentEditText);
        miniProfilePicture = findViewById(R.id.miniProfilePicture);
        postCommentButton = findViewById(R.id.postCommentButton);
    }
}
