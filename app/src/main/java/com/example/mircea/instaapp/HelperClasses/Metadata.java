package com.example.mircea.instaapp.HelperClasses;

public class Metadata {

    private String imageUrl;

    public Metadata(){}

    public Metadata(String im){

        this.imageUrl = im;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
