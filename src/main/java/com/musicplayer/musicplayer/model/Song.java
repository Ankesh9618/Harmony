package com.musicplayer.musicplayer.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Song {
    private int id;
    private String name;
    private String artist;
    private String duration;
    private String fileSong;
    public Song() {
    }

    public Song(int id, String name, String artist, String duration, String fileSong) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.fileSong = fileSong;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileSong() {
        return fileSong;
    }

    public void setFileSong(String fileSong) {
        this.fileSong = fileSong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", duration='" + duration + '\'' +
                ", fileSong='" + fileSong + '\'' +
                '}';
    }
}
