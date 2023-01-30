package fi.utu.tech.gui.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        BorderPane root = loader.load();
        Scene main = new Scene(root, 1280, 720);

        // Juuren taustakuva
        Image img = new Image(getClass().getResource("backGround.jpg").toExternalForm());
        BackgroundImage bgImg = new BackgroundImage(
                img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        root.setBackground(new Background(bgImg));

        // Css tiedosto
        String css = getClass().getResource("styles.css").toExternalForm();
        main.getStylesheets().add(css);

        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setTitle("Battleship JavaFX");
        stage.setScene(main);
        stage.show();
    }
}
