package com.musicplayer.musicplayer.model;

public class DetailSongPlaylist {
    private int id;
    private int id_song;
    private int id_playlist;

    public DetailSongPlaylist() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_song() {
        return id_song;
    }

    public void setId_song(int id_song) {
        this.id_song = id_song;
    }

    public int getId_playlist() {
        return id_playlist;
    }

    public void setId_playlist(int id_playlist) {
        this.id_playlist = id_playlist;
    }
}
