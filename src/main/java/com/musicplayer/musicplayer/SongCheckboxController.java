package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Song;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class SongCheckboxController {
    private Song song;
    private List<String> lstSongs;
    @FXML
    private CheckBox checkBoxSong;
    private MyListener<Song> myListener;
//    @FXML
//    public void click(MouseEvent mouseEvent){
//        myListener.onClickListener(song);
//    }
//    @FXML
//    public void click(ActionEvent e){
//        myListener.onClickListener(song);
//    }
    public SongCheckboxController() {

    }

    void setData(Song s,MyListener<Song> myListener1){
        this.song = s;
        this.myListener = myListener1;
        checkBoxSong.setText(s.getName());
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e)
            {
                if (checkBoxSong.isSelected()) {
                    myListener.onClickListener(song);
                }
                else{
                    myListener.onClickListener(song);
                }

            }

        };

        // set event to checkbox
        checkBoxSong.setOnAction(event);
    }


}
