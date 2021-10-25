package com.example.android2mediaplayer;

import android.graphics.Bitmap;

public class Song {
    String name;
    String link;
    int imgResId;
    Bitmap bitmap;
    String enc;

    public Song(String name, String link,String enc){
        this.name = name;
        this.link = link;
        this.enc = enc;
    }
    public String getName(){
        return this.name;
    }
    public String getLink(){
        return this.link;
    }
    public int getResId(){
        return this.imgResId;
    }
    public Bitmap getBitmap(){return this.bitmap;}
    public String getEnc(){return this.enc;};
}
