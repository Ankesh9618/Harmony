package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Playlist;
import com.musicplayer.musicplayer.model.Song;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlaylistAddPane implements Initializable {
    @FXML
    private TextField txtName;
    private DBConnection db;
    @FXML
    private GridPane gridPane;
    private ImageView img = new ImageView(Paths.get("src/main/java/images/addPlaylist.png").toUri().toURL().toExternalForm());
    @FXML
    private Label error;
    @FXML
    private Label success;
    @FXML
    private AnchorPane scenePlaylistAdd;
    @FXML
    private Button btnAdd;
    private List<Song> songs = new ArrayList<>();
    private MyListener<Song> myListener;
    private List<Song> allSongs = new ArrayList<>();
    public PlaylistAddPane() throws MalformedURLException {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db =  new DBConnection();
        img.setFitHeight(40);
        img.setFitWidth(40);
        btnAdd.setGraphic(img);
        listSongs();


    }

    @FXML
    void add(ActionEvent event) throws SQLException, IOException {
        String nameOfPlaylist = txtName.getText();
        writeSuccess(false);
        if(nameOfPlaylist == null || nameOfPlaylist.equals("")){
            writeSuccess(false);
            writeError("Fill name of playlist");
        }
        else{
            if(checkPlaylistExist(nameOfPlaylist)){
                if(allSongs.size() > 0){
                    writeError("");
                    int idOfPlaylistThathasJustInsert = insertPlaylist(nameOfPlaylist);
                    if(idOfPlaylistThathasJustInsert != 0){

                        writeError("");
                        Boolean check  = insertDetailSong(idOfPlaylistThathasJustInsert,allSongs);
                        if(check){
                            writeError("");
                            writeSuccess(check);
                        }
                        else{
                            writeError("insert fail");
                            writeSuccess(check);
                        }
                    }
                    else{
                        writeSuccess(false);
                        writeError("insert fail");
                    }
                }
                else{
                    writeSuccess(false);
                    writeError("Add at least a song");
                }
            }
            else{
                writeSuccess(false);
                writeError("Exist in your playlist");
            }
        }

    }
    void writeSuccess(boolean check){
        if(check){
            success.setText("Add Success");
            success.setStyle("-fx-background-color: green;");
        }
        else{
            success.setText("");
            success.setStyle("-fx-background-color: none;");
        }
    }
    public boolean checkPlaylistExist(String namePlaylist){
        String sql = "select * from playlist where name like ?";
        Connection conn = db.getConnection();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try{
            stm = conn.prepareStatement(sql);
            stm.setString(1,namePlaylist);
            rs = stm.executeQuery();
            if(rs.next()){
                return false;
            }
            else{
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public void writeError(String textError){
        error.setText(textError);
    }
    public boolean insertDetailSong(int idOfPlaylist,List<Song> songssss) throws SQLException {
        String sql = "insert into detailsong(id_Song,id_playlist) values(?,?)";
        Connection conn = db.getConnection();
        Boolean check = false;
        for(Song s1 : songssss){

            PreparedStatement stm = null;
            try{
                conn.setAutoCommit(false);
                stm = conn.prepareStatement(sql);
                stm.setInt(1,s1.getId());
                stm.setInt(2,idOfPlaylist);
                stm.executeUpdate();
                conn.commit();
                check =  true;
            }catch (SQLException e){
                e.printStackTrace();
                conn.rollback();
                check = false;
            }
        }

        return check;
    }
    public int insertPlaylist(String name){
        int idOfPlaylist = 0;
        String sql = "insert into playlist(name) values(?)";

        try(
                Connection conn = db.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ){

            stm.setString(1,name);
            int affectedRows = stm.executeUpdate();
            if(affectedRows ==0){
                return 0;
            }
            try(ResultSet generatedKeys = stm.getGeneratedKeys()){
                if (generatedKeys.next()) {
                    idOfPlaylist = (int) generatedKeys.getLong(1);
                }
                else {
                    writeError("fail insertion");
                }
            }
            return idOfPlaylist;
        }catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
    public void listSongs(){
        songs.addAll(getSongs());
        if(songs.size()> 0){
            myListener = new MyListener<Song>() {
                @Override
                public void onClickListener(Song p) {


                    if(!allSongs.contains(p)){
                        allSongs.add(p);
                    }
                    else{
                        allSongs.remove(p);
                    }

                }
            };
        }
        int column=0;
        int row=1;
        try{
            for(Song s : songs) {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("SongCheckbox.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                SongCheckboxController songCheckboxController = fxmlLoader.getController();
                songCheckboxController.setData(s,myListener);
//                songCheckboxController.setData(s);

                gridPane.add(anchorPane,column,row++);

                gridPane.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxWidth(Region.USE_COMPUTED_SIZE);

                gridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(anchorPane,new Insets(5));  // insert is margin top and bottom

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public  List<Song> getSongs(){
        List<Song> lstSongs = new ArrayList<>();
        Connection conn = db.getConnection();
        String sql = "select * from songs";
        PreparedStatement pst =null;
        ResultSet rs  = null;
        List<String> name = new ArrayList<>();
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
                lstSongs.add(s);
            }
            return lstSongs;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }



}
