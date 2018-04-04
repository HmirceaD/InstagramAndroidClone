package com.example.mircea.instaapp.Raw;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Friend implements Parcelable{

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Friend createFromParcel(Parcel parcel) {
            return new Friend(parcel);
        }

        @Override
        public Friend[] newArray(int i) {
            return new Friend[i];
        }
    };

    private String email;
    private byte[] photoByteArray;


    public Friend(){}

    public Friend(Parcel par){

        this.email = par.readString();
        this.photoByteArray = new byte[par.readInt()];
        par.readByteArray(photoByteArray);
    }

    public Friend(String email, byte[] photoUrl){
        this.email = email;
        this.photoByteArray = photoUrl;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public byte[] getPhotoUrl() {return photoByteArray;}

    public void setPhotoUrl(byte[] photoUrl) {this.photoByteArray = photoUrl;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeInt(photoByteArray.length);
        parcel.writeByteArray(this.photoByteArray);
    }
}
