package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Playlist;
import com.musicplayer.musicplayer.model.Song;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

public class ListSongOfPlaylist implements Initializable {
    @FXML
    private GridPane gridSong;

    @FXML
    private ScrollPane scrollSong;
    @FXML
    private Label nameOfPlaylist;
    private List<Song> songs = new ArrayList<>();
    private DBConnection dBConnection;
    private int IdOfPlaylist;
    private String name;
    @FXML
    private ImageView remove;
    @FXML
    private ImageView addSong;
    @FXML
    private ImageView refresh;
    @FXML
    private Label alert;
    private MyListener<Song> myListener;
    private List<Song> songPassToPlay = new ArrayList<>();
    public ListSongOfPlaylist() {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dBConnection  = new DBConnection();
//        listSongs();

    }
    @FXML
    public void handleRefresh(MouseEvent mouseEvent) {
        songs.clear();
        listSongs();
    }


    //play all music in the playlist.
    @FXML
    void clickToPlayPlaylist(MouseEvent event) throws Exception {
        playMusicController play = new playMusicController();
        play.setSongToPlay(songs);

    }

    //play all below music that CLick in the playlist.
    public void passSongToPlay(List<Song> allSongClicked)throws Exception{
        System.out.println("passSongToPlay(List<Song> allSongClicked)"+allSongClicked);
        
        
        
        Connection con = dBConnection.getConnection();
        String deleteQuery = "TRUNCATE playsong";
        PreparedStatement deleteStatement = con.prepareStatement(deleteQuery);
        System.out.println(deleteStatement.executeUpdate());
        
        String insertQuery = "INSERT INTO playsong (name, artist, duration, song) VALUES (?, ?, ?, ?)";
        
         // Once the password is complete, delete it
        try (PreparedStatement insertStatement = con.prepareStatement(insertQuery)) {
            for (Song song : allSongClicked) {
                insertStatement.setString(1, song.getName());
                insertStatement.setString(2, song.getArtist());
                insertStatement.setString(3, song.getDuration());
                insertStatement.setString(4, song.getFileSong());
                insertStatement.executeUpdate();
                System.out.println("added song "+song);
            }
            
        }

        System.out.println("Data replaced successfully.");
        
        LoginController.songChanged = true;
    }


    @FXML
    public void handleAddSong(MouseEvent mouseEvent) {
        FXMLLoader load = new FXMLLoader();
        load.setLocation(getClass().getResource("AddSongScene.fxml"));
        try {
            load.load();
        }catch (IOException e){
            e.printStackTrace();
        }

        AddSongIntoPlayListController addSongIntoPlayListController = load.getController();
        addSongIntoPlayListController.setIdForPlaylist(IdOfPlaylist,name);
        addSongIntoPlayListController.listSongs();


        Parent parent = load.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UTILITY);
        stage.show();

    }
    @FXML
    public void handleRemove(MouseEvent mouseEvent) {
        Connection conn = dBConnection.getConnection();
        String sql = "delete from detailsong where id_playlist = ?";
        PreparedStatement stm = null;
        try{

            stm =conn.prepareStatement(sql);
            stm.setInt(1,IdOfPlaylist);
            stm.executeUpdate();
            alert.setStyle("-fx-background-color: green;");
            alert.setText("Remove Successful");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        removePlaylist();
        try{
            conn.close();
            stm.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    void removePlaylist(){
        Connection conn = dBConnection.getConnection();
        String sql = " delete from playlist where id = ?";
        PreparedStatement stm = null;
        try{
            stm =conn.prepareStatement(sql);
            stm.setInt(1,IdOfPlaylist);
            stm.executeUpdate();
        } catch (SQLException  e) {
            throw new RuntimeException(e);
        }
        try{
            conn.close();
            stm.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void setIdplayList(Playlist p){
        this.IdOfPlaylist = p.getId();
        this.name = p.getName();
    }


    public List<Song> getSongs(){

        nameOfPlaylist.setText(this.name);
        List<Song> listSong = new ArrayList<>();
        Connection conn = dBConnection.getConnection();
        String sql = "select songs.*  from detailsong ,songs  where detailsong.id_playlist = ? and songs.id = id_Song";
        PreparedStatement pst =null;
        ResultSet rs  = null;
        try{
            pst = conn.prepareStatement(sql);
            pst.setInt(1,this.IdOfPlaylist);
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

    public void listSongs(){

        songs.addAll(getSongs());


        //khi click 1 bài nhạc nào đó trong playlist thì sẽ lưu từ bài click tới cuối bài
        if(songs.size() > 0){
//            List<Song> songInthere = new ArrayList<>();
            myListener = new MyListener<Song>() {
                @Override
                public void onClickListener(Song p)  {
                    int id= p.getId();
                    for(int i = 0;i< songs.size();i++){
                        if(songs.get(i).getId() == id){
                            for(int j = i;j<songs.size();j++){
                                songPassToPlay.add(songs.get(j));
                            }
                            break;
                        }

                    }
                    try{
                    	passSongToPlay(songPassToPlay);
                    songPassToPlay.clear();
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
                specificSong.setData(2,s,index,myListener);

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
