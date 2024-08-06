package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Song;
import com.musicplayer.musicplayer.playMusicController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListSongController extends playMusicController implements Initializable {
    public ListSongController() throws MalformedURLException {
		super();
		// TODO Auto-generated constructor stub
	}



	@FXML
    private GridPane gridSong;

    @FXML
    private ScrollPane scrollSong;

    private List<Song> songs = new ArrayList<>();
    private DBConnection dBConnection;
    private MyListener<Song> myListener;
    private List<Song> songPassToPlay = new ArrayList<>();
    private AnchorPane pane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dBConnection  = new DBConnection();
        listSongs();
    }
    @FXML
    void PlaySongOfYourSong(MouseEvent event) throws Exception {
        playMusicController play = new playMusicController();
        play.setSongToPlay(songs);
    }
    public List<Song> getSongs(){
        List<Song> listSong = new ArrayList<>();
        Connection conn = dBConnection.getConnection();
        String sql = "select * from songs";
        PreparedStatement pst =null;
        ResultSet rs  = null;
        try{
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()){
                Song s = new Song();
                s.setId(rs.getInt(1));
                s.setName(rs.getString(2));
                s.setArtist(rs.getString(3));
                s.setDuration(rs.getString(4));
                s.setFileSong(rs.getString(5));
                listSong.add(s);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return listSong;
    }
    
    
    
    public void listSongs() {
    	
        songs.addAll(getSongs());
        
        if(songs.size() > 0){
            myListener = new MyListener<Song>() {
                @Override
                public void onClickListener(Song p) {
                	try{
                    songPassToPlay.add(p);
                    Connection con = dBConnection.getConnection();
                    String deleteQuery = "TRUNCATE playsong";
                    PreparedStatement deleteStatement = con.prepareStatement(deleteQuery);
                    System.out.println(deleteStatement.executeUpdate());
                    
                    String insertQuery = "INSERT INTO playsong (name, artist, duration, song) VALUES (?, ?, ?, ?)";
                    
                    System.out.println("listSongs()"+songPassToPlay); // Once the password is complete, delete it
                    try (PreparedStatement insertStatement = con.prepareStatement(insertQuery)) {
                        for (Song song : songPassToPlay) {
                            insertStatement.setString(1, song.getName());
                            insertStatement.setString(2, song.getArtist());
                            insertStatement.setString(3, song.getDuration());
                            insertStatement.setString(4, song.getFileSong());
                            insertStatement.addBatch();
                        }
                        insertStatement.executeUpdate();
                    }

                    System.out.println("Data replaced successfully.");
                    
                    LoginController.songChanged = true;
                    
                }catch(Exception e) {
                	System.out.println(e);
                }
                	}
            };
        }
        int column=0;
        int row=1;
        int  index = 1;
        try{
            for(Song s : songs) {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ListSongs.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                SpecificSong specificSong = fxmlLoader.getController();
                specificSong.setData(1,s,index,myListener); // code ==1 is you're in song

                gridSong.add(anchorPane,column,row++);

                gridSong.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridSong.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridSong.setMaxWidth(Region.USE_COMPUTED_SIZE);

                gridSong.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridSong.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridSong.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(anchorPane,new Insets(5));  // insert is margin top and bottom
                index++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
