package com.example.mircea.instaapp.HelperClasses;


public class EmailRefactor {

    public String refactorEmail(String email){

        return email.replace(".", "__dot__");

    }
    public String decodeEmail(String email){

        return email.replace("__dot__", ".");

    }
}
