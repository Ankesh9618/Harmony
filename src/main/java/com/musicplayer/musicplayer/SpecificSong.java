package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.MalformedURLException;
import java.nio.file.Paths;


public class SpecificSong {
    @FXML
    private Label art;

    @FXML
    private Label duration;

    @FXML
    private Label nameOfSong;
    @FXML
    private AnchorPane archorofSong;

    @FXML
    private Label stt;
    @FXML
    private ImageView imageOfPlay;
    private Song song;
    @FXML
    private HBox HBOX;
    private ImageView img = new ImageView(Paths.get("src/main/java/images/playButton.png").toUri().toURL().toExternalForm());

    private MyListener<Song> myListener;
    @FXML
    public void click(MouseEvent mouseEvent){
        myListener.onClickListener(song);
    }
    public SpecificSong() throws MalformedURLException {
    }

    void setData(int code,Song s, int index,MyListener<Song> myListener){
       if(code==1){
           this.song = s;
           this.myListener = myListener;
           stt.setText(Integer.toString(index));
           nameOfSong.setText(s.getName());
           art.setText(s.getArtist());
           duration.setText(s.getDuration());
       }
       else if(code==2){
           this.song = s;
           this.myListener = myListener;
           stt.setText(Integer.toString(index));
           nameOfSong.setText(s.getName());
           art.setText(s.getArtist());
           duration.setText(s.getDuration());
       }
       else {
           System.out.println("error");
       }

    }


}
