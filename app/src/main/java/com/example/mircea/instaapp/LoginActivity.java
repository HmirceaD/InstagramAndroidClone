package com.example.mircea.instaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText userTextField;
    private EditText passwordTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUi();
    }

    private void setupUi() {

        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener((View v) -> checkLogin());

        userTextField = findViewById(R.id.userTextField);
        passwordTextField = findViewById(R.id.passwordTextField);
    }

    private void checkLogin() {

        startActivity(new Intent(this, MainActivity.class));

        //TODO(1) ADD THE FUNCTIONALITY HERE
        //TODO(2) Add firebase
    }
}
