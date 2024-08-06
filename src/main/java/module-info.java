module com.musicplayer.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
	requires javafx.graphics;


    opens com.musicplayer.musicplayer to javafx.fxml;
    exports com.musicplayer.musicplayer;
}