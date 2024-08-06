package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Playlist;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemPlaylistSideBarController {
    @FXML
    private Label namePlaylist;
    @FXML
    public void click(MouseEvent mouseEvent){
        myListener.onClickListener(playlist);
    }
    private MyListener<Playlist> myListener;
    private Playlist playlist;

    public void setData(Playlist playlist,MyListener<Playlist> myListener){
        this.playlist = playlist;
        this.myListener = myListener;
        namePlaylist.setText(playlist.getName());
    }
}
