package fi.utu.tech.gui.javafx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

//Scene 2. Laudan koko ja alusten määrä säädetään tässä.
public class Setup2Controller {

    private MainController mainController;
    private double boardSize = 100;
    private double shipAmount = 0;
    private ArrayList<Spinner> shipSpinners = new ArrayList<>();
    private boolean shipSetupsChanged = false;
    private GameLogic gameLogic = GameLogic.getInstance();

    public void initialize() {
        shipSpinners.add(destroyer);
        shipSpinners.add(submarine);
        shipSpinners.add(cruiser);
        shipSpinners.add(battleship);
        shipSpinners.add(carrier);

        choiceBox.setItems(FXCollections.observableArrayList("10x10","9x9","8x8","7x7","6x6","5x5"));
        choiceBox.setValue("10x10");
        shipSpinners.forEach(s -> {
            if (s.getValueFactory() == null) {
                s.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
            }
        });

        warning.setVisible(false);
        bindTexts();

        //Kuvakkeiden asettaminen esimerkkilaivoihin
        carrierR.setFill(ShipPatternFactory.getCarrierPattern());
        destroyerR.setFill(ShipPatternFactory.getDestroyerPattern());
        cruiserR.setFill(ShipPatternFactory.getCruiserPattern());
        submarineR.setFill(ShipPatternFactory.getSubmarinePattern());
        battleshipR.setFill(ShipPatternFactory.getBattleshipPattern());
    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private VBox setup2;

    @FXML
    private Label warning;

    @FXML
    private Label instructions;

    @FXML
    private Label boardLabel;

    @FXML
    private Label carrierLabel;

    @FXML
    private Label battleshipLabel;

    @FXML
    private Label cruiserLabel;

    @FXML
    private Label submarineLabel;

    @FXML
    private Label destroyerLabel;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Spinner<Integer> carrier,destroyer,battleship,submarine,cruiser;

    @FXML
    private ProgressBar validityBar;
    
    @FXML
    private Button btnNext, btnPrevious;

    @FXML
    private Rectangle carrierR,destroyerR,battleshipR,submarineR,cruiserR;

    //Valitaan laudan koko.
    @FXML
    private void onChoiceboxAction() {
        if (carrier.getValue() == null || choiceBox.getValue() == null) return;
        var selectedOption = choiceBox.getValue();
        var x = Integer.valueOf(selectedOption.substring(0,1));
        if (x == 1) x = x + 9; // jos valitaan 10x10 niin lisätään 9, koska edellisen rivin koodi antaa vaan ekan luvun
        boardSize = x*x;
        updateValidity();
    }

    //Vaihtaa kyseisen alustyypin määrää.
    @FXML
    private void onSpinnerAction() {
        shipSetupsChanged = true;
        updateValidity();
    }

    //Takaisin aiempaan näkymään.
    @FXML
    private void onPreviousButtonClick() {
    	AudioPlayer.playClickSfx();
        mainController.getRootPane().setCenter(mainController.getSetup1Controller().getSetup1());
        mainController.getUpperButtons().getChildren().remove(mainController.getBtnMainMenu());
    }
    
    //Luo laivat ja seuraavaan sceneen.
    @FXML
    private void onNextButtonClick() {
    	AudioPlayer.playClickSfx();
        if (!isValidFields()) return;
        warning.setVisible(false);

        // laivojen luonti spinnereiden arvoista
        ArrayList<Ship> p1Ships = new ArrayList<>();
        ArrayList<Ship> p2Ships = new ArrayList<>();
        int size = 2;
        ImagePattern defaultPattern = null;
        ImagePattern redPattern = null;
        ImagePattern transparentPattern = null;
        String type = null;
        for (int i = 0; i  < 5; i ++ ) {
            if (i == 2) size = size -1;
            Spinner<Integer> s = shipSpinners.get(i);
            for (int j = 0; j < s.getValue(); j++) {
            	//Etsii oikean kuvakkeen laivalle
                if (s.getId().equals("submarine")) {
                    defaultPattern = ShipPatternFactory.getSubmarinePattern();
                    redPattern = ShipPatternFactory.getSubmarinePatternDestroyed();
                    transparentPattern = ShipPatternFactory.getSubmarinePatternTransparent();
                    type = s.getId();
                };
                if (s.getId().equals("destroyer")) {
                    defaultPattern = ShipPatternFactory.getDestroyerPattern();
                    redPattern = ShipPatternFactory.getDestroyerPatternDestroyed();
                    transparentPattern = ShipPatternFactory.getDestroyerPatternTransparent();
                    type = s.getId();
                };
                if (s.getId().equals("carrier")) {
                    defaultPattern = ShipPatternFactory.getCarrierPattern();
                    redPattern = ShipPatternFactory.getCarrierPatternDestroyed();
                    transparentPattern = ShipPatternFactory.getCarrierPatternTransparent();
                    type = s.getId();
                };
                if (s.getId().equals("battleship")) {
                    defaultPattern = ShipPatternFactory.getBattleshipPattern();
                    redPattern = ShipPatternFactory.getBattleshipPatternDestroyed();
                    transparentPattern = ShipPatternFactory.getBattleshipPatternTransparent();
                    type = s.getId();
                };
                if (s.getId().equals("cruiser")) {
                    defaultPattern = ShipPatternFactory.getCruiserPattern();
                    redPattern = ShipPatternFactory.getCruiserPatternDestroyed();
                    transparentPattern = ShipPatternFactory.getCruiserPatternTransparent();
                    type = s.getId();
                };

                p1Ships.add(new Ship(size, type, defaultPattern, redPattern, transparentPattern));
                p2Ships.add(new Ship(size, type, defaultPattern, redPattern, transparentPattern));
            }
            size = size + 1;
        }

        var setup3Pane = mainController.getSetup3Controller().getPane();

        // jos eka kertaa
        if (gameLogic.getP1().getGameBoard() == null || gameLogic.getP1().getShips().isEmpty()) {
            prepareNextScene(setup3Pane, p1Ships, p2Ships);
            shipSetupsChanged = false;
            mainController.getRootPane().setCenter(mainController.getSetup3Controller().getSetup3());
        // jos asetukset muuttuivat
        } else if (gameLogic.getP1().getGameBoard().getBoardSize() != boardSize || shipSetupsChanged) {
            resetNextScene(setup3Pane);
            prepareNextScene(setup3Pane, p1Ships, p2Ships);
            shipSetupsChanged = false;
            mainController.getRootPane().setCenter(mainController.getSetup3Controller().getSetup3());
        } else {
            mainController.getSetup3Controller().setName();
            shipSetupsChanged = false;
            mainController.getRootPane().setCenter(mainController.getSetup3Controller().getSetup3());
        }
    }

    // Resetoi seuraavan scenen jos asetuksia on muutettu.
    private void resetNextScene(Pane nextScenePane) {
        nextScenePane.getChildren().remove(gameLogic.getP1().getGameBoard().getBoardPane());
        nextScenePane.getChildren().removeAll(gameLogic.getP1().getShips());
        gameLogic.getP1().removeShips();
        gameLogic.getP2().removeShips();
    }

    // Valmistelee seuraavan scenen siirtymistä varten.
    private void prepareNextScene(Pane nextScenePane, ArrayList<Ship> p1Ships, ArrayList<Ship> p2Ships) {
        int cells = (int)Math.sqrt(boardSize);
        GameBoard p1Board = new GameBoard(gameLogic.getP1(),cells);
        GameBoard p2Board = new GameBoard(gameLogic.getP2(),cells);

        gameLogic.getP1().setGameBoard(p1Board);
        gameLogic.getP2().setGameBoard(p2Board);
        nextScenePane.getChildren().add(gameLogic.getP1().getGameBoard().getBoardPane());

        gameLogic.getP1().addShips(p1Ships);
        gameLogic.getP2().addShips(p2Ships);
        nextScenePane.getChildren().addAll(gameLogic.getP1().getGameBoard().generateShips());

        mainController.getSetup3Controller().setPlayingPlayer(gameLogic.getP1());
        mainController.getSetup3Controller().setShipAmounts(p1Ships);
        mainController.getSetup3Controller().calculateAvailableShips();
        mainController.getSetup3Controller().setName();
    }

    // Varmistaa että pelaaja on valinnut vähintään yhden laivan, tai että laivat mahtuvat pelilaudalle
    private boolean isValidFields() {
        if (shipAmount == 0) {
            warning.textProperty().bind(ObservableResourceFactory.getStringBinding("warningAtleastOne"));
            warning.setVisible(true);
            return false;
        }
        if (shipAmount / (boardSize/2) > 1) {
            return false;
        }
        return true;
    }

    // Päivittää progress barin sen mukaan kuinka paljon laivoja mahtuu vielä pelilaudalle
    private void updateValidity() {
        var carriers = carrier.getValue();
        var battleships = battleship.getValue();
        var cruisers = cruiser.getValue();
        var submarines = submarine.getValue();
        var destroyers = destroyer.getValue();

        shipAmount = carriers*5 + battleships*4 + cruisers*3 + submarines*3 + destroyers*2;

        var x = shipAmount / (boardSize/2);
        if (x > 1) {
            warning.textProperty().bind(ObservableResourceFactory.getStringBinding("warningTooManyShips"));
            warning.setVisible(true);
            validityBar.setStyle("-fx-accent: #A52A2A");
        } else {
            warning.setVisible(false);
            validityBar.setStyle("-fx-accent: #5F9EA0");
        }
        validityBar.setProgress(x);
    }

    // Bindaa scenen textit kieliasetukseen
    private void bindTexts() {
        instructions.textProperty().bind(ObservableResourceFactory.getStringBinding("instruction2"));
        btnNext.textProperty().bind(ObservableResourceFactory.getStringBinding("nextButton2"));
        btnPrevious.textProperty().bind(ObservableResourceFactory.getStringBinding("previousButton1"));
        boardLabel.textProperty().bind(ObservableResourceFactory.getStringBinding("boardSize"));
        carrierLabel.textProperty().bind(ObservableResourceFactory.getStringBinding("carriers"));
        battleshipLabel.textProperty().bind(ObservableResourceFactory.getStringBinding("battleships"));
        cruiserLabel.textProperty().bind(ObservableResourceFactory.getStringBinding("cruisers"));
        submarineLabel.textProperty().bind(ObservableResourceFactory.getStringBinding("submarines"));
        destroyerLabel.textProperty().bind(ObservableResourceFactory.getStringBinding("destroyers"));
    }

    public VBox getSetup2() {
        return this.setup2;
    }
    
}
