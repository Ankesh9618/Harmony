package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Playlist;
import com.musicplayer.musicplayer.model.Song;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private AnchorPane panePlayMusic;
    @FXML
    private AnchorPane MainPane;
    @FXML
    private AnchorPane PlaylistSideBarPane;
    @FXML
    private GridPane gridPlaylistSidebar;
    @FXML
    private ScrollPane scrollPlaylistSidebar;
    private AnchorPane Pane;

    private Connection conn;
    private DBConnection dbConnection;
//    private PreparedStatement pst;

    private List<Playlist> playlists = new ArrayList<>();
    @FXML
    private Button yourSongsbtn;
    @FXML
    private Button createPlaylistbtn;
    @FXML
    private Button homeBtn;

    @FXML
    private Button btnRefresh;
    private MyListener<Playlist> myListener;

    private ListSongOfPlaylist listSongOfPlaylist;
    private final ImageView refresh = new ImageView(Paths.get("src/main/java/images/refresh.png").toUri().toURL().toExternalForm());

    private List<Song> passSongs = new ArrayList<>();
    public HomeController() throws MalformedURLException {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConnection = new DBConnection();
        refresh.setFitWidth(20);
        refresh.setFitHeight(20);
        btnRefresh.setGraphic(refresh);
        Home();
        ListPlaylist();
        setMusicFXML();
    }
    public void Home(){
       try{
           homeBtn.setStyle("-fx-background-color: rgba(16, 14, 16, 0.8); -fx-text-fill: #d5d0d0");
           createPlaylistbtn.setStyle("-fx-background-color: none; ");

           //set node
           Pane = FXMLLoader.load(getClass().getResource("HomePane.fxml"));
           setNode(Pane);

           yourSongsbtn.setStyle("-fx-background-color:none");
       }
       catch (IOException e){
           e.printStackTrace();
       }

    }
    @FXML
    void createPlaylistPane(ActionEvent event) {
        try {
            homeBtn.setStyle("-fx-background-color:none; ");
            createPlaylistbtn.setStyle("-fx-background-color: rgba(16, 14, 16, 0.8); -fx-text-fill: #d5d0d0");
            //set node
            Pane = FXMLLoader.load(getClass().getResource("CreatePlaylistPane.fxml"));
            setNode(Pane);

            yourSongsbtn.setStyle("-fx-background-color:none;");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void createSong(ActionEvent event) {
        try {

            homeBtn.setStyle("-fx-background-color:none; ");
            createPlaylistbtn.setStyle("-fx-background-color: none;");
            //set node
            Pane = FXMLLoader.load(getClass().getResource("ListSongsPane.fxml"));
            setNode(Pane);

            yourSongsbtn.setStyle("-fx-background-color: rgba(16, 14, 16, 0.8); -fx-text-fill: #d5d0d0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNode(Node node) {
        MainPane.getChildren().clear();
        MainPane.getChildren().add((Node) node);
        FadeTransition ft = new FadeTransition(Duration.millis(500));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();

    } //add main pain set node
    void setMusicFXML(){

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("playMusic.fxml"));
        try {
            Pane = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        panePlayMusic.getChildren().clear();
        panePlayMusic.getChildren().add((Node) Pane);
        FadeTransition ft = new FadeTransition();
        ft.setNode(Pane);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    @FXML
    void RefreshPlaylist(ActionEvent event) {
        playlists.clear();
        ListPlaylist();
    }


    void ListPlaylist(){
        playlists.addAll(getPlaylists());
        if(playlists.size() > 0){
            myListener = new MyListener<Playlist>() {

                @Override
                public void onClickListener(Playlist p) {


                    homeBtn.setStyle("-fx-background-color:none; ");
                    createPlaylistbtn.setStyle("-fx-background-color: none;");
                    yourSongsbtn.setStyle("-fx-background-color: none;");


                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("ListSongOfPlaylistsPane.fxml"));
                        Pane = fxmlLoader.load();
                        //set node
//                        Pane = FXMLLoader.load(getClass().getResource("ListSongOfPlaylistsPane.fxml"));
                        ListSongOfPlaylist listSongOfPlaylist = fxmlLoader.getController();
                        listSongOfPlaylist.setIdplayList(p);
                        listSongOfPlaylist.listSongs();
                        setNode(Pane);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        int column=0;
        int row=1;
        try{
            for(Playlist p : playlists) {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ItemPlaylistSideBar.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemPlaylistSideBarController itemPlaylistSideBarController = fxmlLoader.getController();
                itemPlaylistSideBarController.setData(p,myListener);
                if(this.gridPlaylistSidebar == null){
                    this.gridPlaylistSidebar = new GridPane();
                }
                gridPlaylistSidebar.add(anchorPane,column,row++);

                gridPlaylistSidebar.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridPlaylistSidebar.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridPlaylistSidebar.setMaxWidth(Region.USE_COMPUTED_SIZE);

                gridPlaylistSidebar.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridPlaylistSidebar.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridPlaylistSidebar.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(anchorPane,new Insets(5));  // insert is margin top and bottom
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private List<Playlist> getPlaylists(){
        if(this.dbConnection == null){
            this.dbConnection = new DBConnection();
        }
        List<Playlist> playlists1 = new ArrayList<>();
        conn = dbConnection.getConnection();
        String sql = "select * from playlist";
        PreparedStatement pst =null;
        ResultSet rs =null;
        try{
            pst  = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()){
                Playlist p = new Playlist();
                p.setId(rs.getInt(1));
                p.setName(rs.getString(2));
                playlists1.add(p);
            }
        }catch (SQLException e){
            return null;
        }
        if(conn!= null){
            try{
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();

            }
        }
        if(pst!= null){
            try{
                pst.close();
            }catch (SQLException e){
                e.printStackTrace();

            }
        }if(rs!= null){
            try{
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();

            }
        }
        return playlists1;
    }
}
