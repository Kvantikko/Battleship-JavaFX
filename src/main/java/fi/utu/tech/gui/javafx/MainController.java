package fi.utu.tech.gui.javafx;

import javafx.beans.binding.When;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

/**
 * Kontrolleri BorderBane juurelle.
 * MainController asennetaan sen alikontrollereihin.
 * Alikontrollerit voivat manipuloida muita controllereita pääkontrollerin kautta.
 */

public class MainController {

    @FXML private Setup1Controller setup1Controller;
    @FXML private Setup2Controller setup2Controller;
    @FXML private Setup3Controller setup3Controller;
    @FXML private BattleController battleController;
    private ImageView finFlag = new ImageView(new Image(getClass().getResource("finflag.png").toExternalForm()));
    private ImageView engFlag = new ImageView(new Image(getClass().getResource("engflag.png").toExternalForm()));
    private GameLogic gameLogic = GameLogic.getInstance();

    // Asentaa mainControllerin muihin controllereihin ja pelin logiikka luokkaan, sekä alustaa UI:ta.
    @FXML private void initialize() {
        setup1Controller.injectMainController(this);
        setup2Controller.injectMainController(this);
        setup3Controller.injectMainController(this);
        battleController.injectMainController(this);
        gameLogic.setMainController(this);
        setSecondBackground();
        AudioPlayer.playMusic();
        upperButtons.getChildren().remove(btnMainMenu);
        bindButtons();
        btnLanguage.setGraphic(finFlag);
    }

    @FXML
    private BorderPane mainPane;

    @FXML
    private HBox upperButtons;

    @FXML
    private Button btnMute;

    @FXML
    private Button btnInstructions;

    @FXML
    private Button btnCredits;

    @FXML
    private Button btnMainMenu;

    @FXML
    private Button btnQuit;

    @FXML
    private Button btnLanguage;

    // Vaihtaa ohjelman kieltä.
    @FXML
    private void onLanguageButtonClick() {
        if (ObservableResourceFactory.getResources().getLocale().equals(Locale.ENGLISH)) {
            btnLanguage.setGraphic(engFlag);
            ObservableResourceFactory.setFinnish();
        } else {
            ObservableResourceFactory.setEnglish();
            btnLanguage.setGraphic(finFlag);
        }
    }

    @FXML
    private void onMuteButtonClick() {
    	AudioPlayer.playClickSfx();
        AudioPlayer.muteMusic();
    }

    // Pop up ikkuna tekijöihin. Bindaa tekstit Kieliresursseihin.
    @FXML
    private void onCreditsButtonClick() {
    	AudioPlayer.playClickSfx();
        Alert credits = new Alert(Alert.AlertType.INFORMATION);
        credits.titleProperty().bind(ObservableResourceFactory.getStringBinding("creditsButton"));
        credits.headerTextProperty().bind(ObservableResourceFactory.getStringBinding("creditsButton"));
        credits.contentTextProperty().bind(ObservableResourceFactory.getStringBinding("creditsContent"));
        credits.showAndWait();
    }

    // Pop up ikkuna pelin ohjeisiin. Bindaa tekstit Kieliresursseihin.
    @FXML
    private void onInstructionsButtonClick() {
        AudioPlayer.playClickSfx();
    	Alert instructions = new Alert(Alert.AlertType.INFORMATION);
        instructions.titleProperty().bind(ObservableResourceFactory.getStringBinding("instructionsButton"));
        instructions.headerTextProperty().bind(ObservableResourceFactory.getStringBinding("instructionsButton"));
        instructions.contentTextProperty().bind(ObservableResourceFactory.getStringBinding("instructionsWindow"));
        instructions.showAndWait();
    }

    // Palaa main menuun. Bindaa tekstit Kieliresursseihin.
    @FXML
    private void onMainMenuButtonClick() {
    	AudioPlayer.playClickSfx();
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.titleProperty().bind(ObservableResourceFactory.getStringBinding("mainMenuTitle"));
        alert.headerTextProperty().bind(ObservableResourceFactory.getStringBinding("mainMenuHeader"));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL))
            .textProperty().bind(ObservableResourceFactory.getStringBinding("cancelButton"));
    	Optional<ButtonType> choice = alert.showAndWait();
    	if (choice.get() == ButtonType.OK) {
        	System.out.println("Returned to main menu");
            resetGame();

        }
        else if (choice.get() == ButtonType.CANCEL) {
        	alert.close();
    	}
    }

    // Pop up ikkuna pelin lopettamiseksi. Bindaa tekstit Kieliresursseihin.
    @FXML
    private void onQuitButtonClick() {
    	AudioPlayer.playClickSfx();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.titleProperty().bind(ObservableResourceFactory.getStringBinding("quitTitle"));
        alert.headerTextProperty().bind(ObservableResourceFactory.getStringBinding("quitHeader"));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL))
                .textProperty().bind(ObservableResourceFactory.getStringBinding("cancelButton"));
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    // Resetoi pelin riippuen missä scenessä ollaan. Battlescenessö oltaessa tehdään hard reset ja käynnistetään
    // MainApp uudestaan ja palataan eknsimmäiseen ruutuun (nimi asetus).
    public void resetGame() {
        if (getRootPane().getCenter().equals(battleController.getBattle())) {
            try {
                new MainApp().start((Stage) mainPane.getScene().getWindow());
            } catch (IOException e) {
                System.out.println("failed to restart");
            }
        } else {
            if (getRootPane().getCenter().equals(setup3Controller.getSetup3())) {
                if (setup3Controller.getBtnNext().getText().equals("Start game!")) {
                    setup3Controller.changeP2ToP1();
                }
            }
            getRootPane().setCenter(setup1Controller.getSetup1());
        }
    }

    // Bindaa yläpalkin nappuloiden tekstit kieliasetukseen
    private void bindButtons() {
        btnMute.textProperty().bind(
                new When(AudioPlayer.getMusicMuteProperty())
                        .then(ObservableResourceFactory.getStringBinding("unmuteButton"))
                        .otherwise(ObservableResourceFactory.getStringBinding("muteButton")));
        btnInstructions.textProperty().bind(ObservableResourceFactory.getStringBinding("instructionsButton"));
        btnCredits.textProperty().bind(ObservableResourceFactory.getStringBinding("creditsButton"));
        btnQuit.textProperty().bind(ObservableResourceFactory.getStringBinding("quitButton"));
        btnMainMenu.textProperty().bind(ObservableResourceFactory.getStringBinding("mainMenuButton"));
    }

    // Lataa ja asentaa setuppien paperisen taustakuvan (juuren taustakuva ladataan MainApp-luokassa).
    private void setSecondBackground() {
        Image img = new Image(getClass().getResource("paperbg.png").toExternalForm());
        BackgroundImage bgImg = new BackgroundImage(
                img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        setup1Controller.getSetup1().setBackground(new Background(bgImg));
        setup2Controller.getSetup2().setBackground(new Background(bgImg));
        setup3Controller.getSetup3().setBackground(new Background(bgImg));
    }

    public Button getMainMenuButton() {
    	return btnMainMenu;
    }

    public BorderPane getRootPane() {
        return mainPane;
    }

    public Setup1Controller getSetup1Controller() {
        return setup1Controller;
    }

    public Setup2Controller getSetup2Controller() {
        return setup2Controller;
    }

    public Setup3Controller getSetup3Controller() {
        return setup3Controller;
    }

    public BattleController getBattleController() {
        return battleController;
    }

    public HBox getUpperButtons() {
        return upperButtons;
    }

    public Button getBtnMainMenu() {
        return btnMainMenu;
    }
}