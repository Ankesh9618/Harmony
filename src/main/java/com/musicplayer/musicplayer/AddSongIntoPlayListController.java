package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddSongIntoPlayListController implements Initializable {
    @FXML
    private GridPane gridPane;
    private DBConnection db;
    @FXML
    private Label labelError;
    @FXML
    private Label  songExist;
    @FXML
    private Label labelSuccess;
    @FXML
    private Label title;
    private List<Song> songs = new ArrayList<>();
    private MyListener<Song> myListener;
    private List<Song> allSongs = new ArrayList<>();
    private int idOfPlaylist;
    private String nameOfPlaylist;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = new DBConnection();

//        listSongs();
    }
    void setIdForPlaylist(int id,String name){
        this.idOfPlaylist = id;
        title.setText("Form add songs to "+"' "+ name + " ' " + "playlist");
        this.nameOfPlaylist = name;
    }
    @FXML
    void addSongsIntoPlaylist(ActionEvent event) throws SQLException {
        if(allSongs.size() > 0){
            clearLabel();
            for(Song s:allSongs){
                clearLabel();
                if(checkSongHasExistOrNot(s)){
                    if(insertDetailSong(idOfPlaylist,s)){
                        clearLabel();
                        writeLabel(1,"Add Success!!");
                    }
                    else{
                        clearLabel();
                        writeLabel(2,"Fail Add");
                    }
                }
                else{
                    clearLabel();
                    writeLabel(s.getName(), "has exist in playlist!!");
                }
            }
        }
        else{
            clearLabel();
            writeLabel(2,"click any song");

        }
    }
    public boolean insertDetailSong(int idOfPlaylist,Song s) throws SQLException {
        String sql = "insert into detailsong(id_Song,id_playlist) values(?,?)";
        Connection conn = db.getConnection();
        Boolean check = false;
        PreparedStatement stm = null;
        try{
            conn.setAutoCommit(false);
            stm = conn.prepareStatement(sql);
            stm.setInt(1,s.getId());
            stm.setInt(2,idOfPlaylist);
            stm.executeUpdate();
            conn.commit();
            check =  true;
        }catch (SQLException e){
            e.printStackTrace();
            conn.rollback();
            check = false;
        }

        return check;
    }
    //clear lable
    void clearLabel(){
        labelError.setText("");
        labelSuccess.setText("");
        songExist.setText("");
        songExist.setStyle("-fx-background-color: none;");
        labelSuccess.setStyle("-fx-background-color: none;");
        labelError.setStyle("-fx-background-color:  none;");
    }
    //check xem co trung or not
    boolean checkSongHasExistOrNot(Song s){
        Connection conn = db.getConnection();
        String sql = "select * from detailsong where id_playlist = ? and id_Song = ?";
        PreparedStatement stm = null;
        ResultSet rs = null;
        try{
            stm = conn.prepareStatement(sql);
            stm.setInt(1,idOfPlaylist);
            stm.setInt(2,s.getId());
            rs = stm.executeQuery();
            if(rs.next()){
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    void writeLabel(int index , String text){
        if(index==1){
            labelSuccess.setStyle("-fx-background-color:  green;");
            labelSuccess.setText(text);
        }else if(index ==2){
            labelError.setStyle("-fx-background-color:  #e44444;");
            labelError.setText(text);
        }
    }
    void writeLabel(Object s ,String text){

        songExist.setStyle("-fx-background-color:  #e44444;");
        songExist.setText((String) s + " " + text);

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
    public List<Song> getSongs(){
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
