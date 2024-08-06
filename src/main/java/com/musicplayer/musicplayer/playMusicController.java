package com.musicplayer.musicplayer;

import com.musicplayer.musicplayer.model.Song;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;


//public class playMusicController implements Initializable {
public class playMusicController {
    @FXML
    private Label art;

    @FXML
    private Button nextBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button playBtn;

    @FXML
    private Button previousBtn;

    @FXML
    private Label songName;

    @FXML
    private ProgressBar songProgressBar;

    @FXML
    private Slider volumeSlider;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;
    private ArrayList<File> songs;

    private int songNumber=0;

    private Timer timer;
    private TimerTask task;

    private boolean running;

    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;

    @FXML
    private Slider songSlider;


    @FXML
    private ImageView SongImage;//avt rotation
    private RotateTransition rotate;//avt rotation

    //play next previous, repeat random
    private int checkPlay=1;

    private int checkRandom = 0;
 
    private int checkRepeat = 0;
    @FXML
    private Button repeatBtn;
    @FXML
    private Button randomBtn;
    private ImageView play = new ImageView(Paths.get("src/main/java/images/playButton.png").toUri().toURL().toExternalForm());
    private ImageView pause = new ImageView(Paths.get("src/main/java/images/pauseButton.png").toUri().toURL().toExternalForm());
    private ImageView next = new ImageView(Paths.get("src/main/java/images/nextButton.png").toUri().toURL().toExternalForm());
    private ImageView previous = new ImageView(Paths.get("src/main/java/images/previousButton.png").toUri().toURL().toExternalForm());
    private ImageView randomUnactive = new ImageView(Paths.get("src/main/java/images/randomButtonWhite.png").toUri().toURL().toExternalForm());
    private ImageView randomActive = new ImageView(Paths.get("src/main/java/images/randomButtonGreen.png").toUri().toURL().toExternalForm());
    private ImageView repeatUnactive = new ImageView(Paths.get("src/main/java/images/repeatButtonUnactive.png").toUri().toURL().toExternalForm());
    private ImageView repeatActive = new ImageView(Paths.get("src/main/java/images/repeatButtonActive.png").toUri().toURL().toExternalForm());
    private ImageView repeatAlways = new ImageView(Paths.get("src/main/java/images/repeatButtonAlways.png").toUri().toURL().toExternalForm());

//    private List<Song> songPass = new ArrayList<>();
    List<Song> playSong = new ArrayList<>();
    private DBConnection dbConnection;

    public playMusicController() throws MalformedURLException {
    	
    }
    
    @FXML
    public void initialize() {
        dbConnection = new DBConnection();

        //design for buttons
        setSongImageforBtn(play,playBtn,40,40);//play and pause btn
        setSongImageforBtn(next,nextBtn,20,20); //next btn
        setSongImageforBtn(previous,previousBtn,20,20);//previous btn
        setSongImageforBtn(repeatUnactive,repeatBtn,20,20);//repeat btn
        setSongImageforBtn(randomUnactive,randomBtn,20,20);//random btn

//        artist: calculate later

        List<File> temp = new ArrayList<>();
        songs = new ArrayList<>();
        directory = new File("src/main/java/com/musicplayer/musicplayer/" +"Music");
        files= directory.listFiles();
        if(files!=null){
            temp.addAll(List.of(files));
        }
        playSong.addAll(listAllSongsInDB());


        for(Song n: playSong){
            for(File f : temp){
                String s = f.toString();
                String[] splitFile = s.split("\\\\");
                if(n.getFileSong().equals(splitFile[splitFile.length-1])){
                    songs.add(f);
                    break;
                }
            }
        }
        //assign song to media
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songName.setText(songs.get(songNumber).getName());

        //change volume
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
        songProgressBar.setStyle("-fx-accent:  rgba(16, 1, 9, 0.8)"); //change color to progress bar

        rotate = new RotateTransition();

    }
    void setSongToPlay(List<Song> s1) throws Exception{
    	dbConnection = new DBConnection();
        System.out.println("setSongToPlay(List<Song> s1) "+s1);
        Connection con = dbConnection.getConnection();
        String deleteQuery = "TRUNCATE playsong";
        PreparedStatement deleteStatement = con.prepareStatement(deleteQuery);
        System.out.println(deleteStatement.executeUpdate());
        
        String insertQuery = "INSERT INTO playsong (name, artist, duration, song) VALUES (?, ?, ?, ?)";
        
         // Once the password is complete, delete it
        try (PreparedStatement insertStatement = con.prepareStatement(insertQuery)) {
            for (Song song : s1) {
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
    List<Song> listAllSongsInDB(){
        List<Song> lst= new ArrayList<>();
        Connection conn = dbConnection.getConnection();
        String sql = "select * from playsong";
        PreparedStatement stm  = null;
        ResultSet rs = null;
        try{
            stm = conn.prepareStatement(sql);
            rs = stm.executeQuery();
            while(rs.next()){
                Song o = new Song();
                o.setId(rs.getInt(1));
                o.setName(rs.getString(2));
                o.setArtist(rs.getString(3));
                o.setDuration(rs.getString(4));
                o.setFileSong(rs.getString(5));
                lst.add(o);
            }
            return lst;
        }catch (Exception e){
            e.printStackTrace();
        }
        if(conn != null && stm != null && rs != null){
            try{
                conn.close();
                stm.close();
                rs.close();
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }
    
    void setSongImageforBtn(ImageView view,Button btn,int height,int width){
        view.setFitHeight(height);
        view.setFitWidth(width);
        btn.setGraphic(view);
    }  
    
    // set image for initialize
    void startRotationImage(){
        rotate.setNode(SongImage);
        rotate.setDuration(Duration.millis(4000));
        rotate.setCycleCount(TranslateTransition.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setByAngle(360);
        rotate.play();
    }  // set avatar to rotation

    @FXML
    void onPlayButtonClick(){
        if(checkPlay==1){
            playSong();
            setSongImageforBtn(pause,playBtn,40,40);

            checkPlay=0;
        }
        else{
            pauseSong();
            setSongImageforBtn(play,playBtn,40,40);
            checkPlay=1;
        }
    }  //to set icon play and pause

    void pauseSong() {
        cancelTimer();
        mediaPlayer.pause();
    }
    @FXML
    void randomSong(){
        if (checkRandom==1){
            setSongImageforBtn(randomUnactive,randomBtn,20,20);

            checkRandom=0;
        }
        else{
            setSongImageforBtn(randomActive,randomBtn,20,20);

            checkRandom=1;
        }
    } //to set icon random
    @FXML
    void repeat() { //to set icon repeat
        if (checkRepeat==0){
            setSongImageforBtn(repeatActive,repeatBtn,20,20);
            checkRepeat=1;
        }
        else if (checkRepeat==1){
            setSongImageforBtn(repeatAlways,repeatBtn,20,20);
            checkRepeat=2;
        }
        else{
            setSongImageforBtn(repeatUnactive,repeatBtn,20,20);
            checkRepeat=0;
        }
    }

    void playSong() {
        startTimer();
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    } //play music

    @FXML
    void nextSong() {
        if (checkRandom==1) {
            int randomSongs = (int) Math.round(Math.random() * (songs.size()));
            while (randomSongs == songNumber || randomSongs>=songs.size()){
                randomSongs = (int) Math.round(Math.random() * (songs.size()));
            }
            songNumber=randomSongs-1;

        }
        else if(songNumber == songs.size() -1){
            songNumber = -1;
        }
        songNumber++;
        mediaPlayer.stop();
        if(running){
            cancelTimer();
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songName.setText(songs.get(songNumber).getName());
        System.out.println(songNumber+" "+songs);
        SongImage.setRotate(0);
        checkPlay=1;
        onPlayButtonClick();
        /*
        if(songNumber < songs.size() -1){
            songNumber++;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songName.setText(songs.get(songNumber).getName());

        }
        else{
            songNumber=0;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songName.setText(songs.get(songNumber).getName());

        }
        SongImage.setRotate(0);
        checkPlay=1;
        onPlayButtonClick();*/
    } //next music
   

    @FXML
    void previousSong() {
        if(songNumber > 0){
            songNumber--;


        }
        else{
            songNumber=songs.size() -1;


        }
        mediaPlayer.stop();
        if(running){
            cancelTimer();
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songName.setText(songs.get(songNumber).getName());
        SongImage.setRotate(0);
        checkPlay=1;
        onPlayButtonClick();
    } // back music
    @FXML
    void onAway(){
        ImageView view;
        if (checkPlay==1){
            view = play;
        }
        else  view = pause;
        view.setFitHeight(40);
        view.setFitWidth(40);
        playBtn.setGraphic(view);
    } //tao effect
    @FXML
    void onHover(){
        ImageView view;
        if (checkPlay==1){
            view = play;
        }
        else  view = pause;
        view.setFitHeight(42);
        view.setFitWidth(42);
        playBtn.setGraphic(view);
    }  //tao effect
    public void startTimer(){  //time of music
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                startRotationImage();
                running=true;
                int current = (int)Math.round(mediaPlayer.getCurrentTime().toSeconds());
                int end = (int)Math.round(media.getDuration().toSeconds());

                songSlider.setMin(0);
                if (end!=0) songSlider.setMax(end);
                else songSlider.setMax(1);

                String minuteE = String.format("%02d",end/60);
                String secondE = String.format("%02d",end%60);
                Platform.runLater(()->endLabel.setText(minuteE+":"+secondE));

                String minuteS = String.format("%02d",current/60);
                String secondS = String.format("%02d",current%60);
                Platform.runLater(()->startLabel.setText(minuteS+":"+secondS));


                int finalEnd = end;

                songSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        mediaPlayer.seek(Duration.seconds(songSlider.getValue()));
                        songProgressBar.setProgress((double)songSlider.getValue()/ finalEnd);
                    }
                });
                if (end==0) end++;
                songProgressBar.setProgress((double)current/end);
                if(current/end==1){
                    cancelTimer();
                    if (songNumber==songs.size()-1 && checkRepeat==0)  // in the last song, it will be stop when not active repeat
                        Platform.runLater(()->onPlayButtonClick());
                    else if(checkRepeat==2) { // repeat again a song.
                        songNumber--;
                        Platform.runLater(()->nextSong());
                    }
                    else Platform.runLater(()->nextSong()); // neu co repeat thi cu auto next nhu bth
                    /*
                    if (songNumber==songs.size()-1) return;
                    else songNumber++;
                    mediaPlayer.stop();

                    media = new Media(songs.get(songNumber).toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    Platform.runLater(()->songName.setText(songs.get(songNumber).getName()));
                    Platform.runLater(()->{SongImage.setRotate(0);});
                    playSong();*/
                }
            }
        };
        timer.scheduleAtFixedRate(task,0,1000);
    }
    public void cancelTimer(){
        running = false;
        timer.cancel();
        rotate.stop();
    }

}
