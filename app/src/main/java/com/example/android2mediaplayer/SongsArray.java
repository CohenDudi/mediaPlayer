package com.example.android2mediaplayer;

import java.util.ArrayList;
import java.util.List;

public class SongsArray {
    ArrayList<Song> songs;

    public SongsArray(){
        songs = new ArrayList<Song>();
    }

    public void add(Song p){
        songs.add(p);
    }

    public ArrayList<Song> getList(){
        return songs;
    }

    public String get(int i){
        return songs.get(i).getLink();
    }

    public String getName(int i){
        return songs.get(i).getName();

    }

    public int size(){
        return this.songs.size();
    }

    public Song getSong(int i){return this.songs.get(i);}
}
