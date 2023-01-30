package fi.utu.tech.gui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;


public class Setup3Controller {
    private MainController mainController;
    private Player playingPlayer;
    private int carriers = 0;
    private int battleships = 0;
    private int cruisers = 0;
    private int submarines = 0;
    private int destroyers = 0;
    private GameLogic gameLogic = GameLogic.getInstance();

    public void initialize() {
        warning.setVisible(false);
        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (warning.isVisible() && shipsArePlaced()) warning.setVisible(false);
        });
        hideShipCounters();
        bindTexts();
    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void setPlayingPlayer(Player player) {
    	this.playingPlayer = player;
    }

    // Asettaa laivatyyppien määrät luokan attribuutteihin
    public void setShipAmounts(ArrayList<Ship> ships) {
        ships.forEach(s -> {
            if (s.getType().equals("carrier")) { carriers += 1; }
            if (s.getType().equals("battleship")) { battleships += 1; }
            if (s.getType().equals("cruiser")) { cruisers += 1; }
            if (s.getType().equals("submarine")) { submarines += 1; }
            if (s.getType().equals("destroyer")) { destroyers += 1; }
        });
    }

    @FXML
    private VBox setup3;

    @FXML
    private Pane pane;

    @FXML
    private Label instructions;

    @FXML
    private Label instructionsName;

    @FXML
    private Label rotateInstructions;

    @FXML
    private Label warning;

    @FXML
    private Label carrierAmount;

    @FXML
    private Label battleshipAmount;

    @FXML
    private Label cruiserAmount;

    @FXML
    private Label submarineAmount;

    @FXML
    private Label destroyerAmount;

    @FXML
    private Rectangle carrierCounter;

    @FXML
    private Rectangle battleshipCounter;

    @FXML
    private Rectangle cruiserCounter;

    @FXML
    private Rectangle submarineCounter;

    @FXML
    private Rectangle destroyerCounter;
    
    @FXML
    private Button btnPrevious;

    @FXML
    private Button btnNext;

    @FXML
    private Button btnReset;

    @FXML // Vaihtaa UI:n pelaajaa 2 varten jos ollaan pelaajan 1 laivojen asettelun kohdalla. Muuten aloitta pelin.
    private void onNextButtonClick() {
        AudioPlayer.playClickSfx();
        if (playingPlayer.equals(gameLogic.getP1())) {  // vaihdetaan pelaajaa
            if (!shipsArePlaced()) return;

            btnNext.textProperty().bind(ObservableResourceFactory.getStringBinding("startGameButton"));
            warning.setVisible(false);

            pane.getChildren().remove(playingPlayer.getGameBoard().getBoardPane());

            playingPlayer = gameLogic.getP2();
            setName();
            pane.getChildren().add(playingPlayer.getGameBoard().getBoardPane());

            try {
                playingPlayer.getGameBoard().setShipsToPane(pane); // yrittää asettaa jo luotuja aluksia...
            } catch (Exception e) {
                pane.getChildren().addAll(playingPlayer.getGameBoard().generateShips()); // ...mutta jos ei ole niin generoidaan
            }
            calculateAvailableShips();
        } else {  // edetään seuraavaan sceneen
            if (!shipsArePlaced()) return;
            AudioPlayer.playBattleMusic();
            warning.setVisible(false);

            mainController.getRootPane().setCenter(mainController.getBattleController().getBattle()); //battlescene näkyviin
            //alustetaan vuoro
            gameLogic.getP1().setTurn(true);
            gameLogic.getP2().setTurn(false);
            gameLogic.getP1().getGameBoard().setNonClickable(true);
            gameLogic.getP2().getGameBoard().setNonClickable(false);

            gameLogic.getP1().getGameBoard().setShipsToPane(gameLogic.getP1().getGameBoard().getBoardPane());
            gameLogic.getP2().getGameBoard().setShipsToPane(gameLogic.getP2().getGameBoard().getBoardPane());

            gameLogic.getP1().getGameBoard().relocateAllShips(-400, 0); // koordinaatit ovat muutoksen määrä
            gameLogic.getP2().getGameBoard().relocateAllShips(-400, 0);
            mainController.getBattleController().setBoardsToScene(
                    gameLogic.getP1().getGameBoard().getBoardPane(),
                    gameLogic.getP2().getGameBoard().getBoardPane()
            );

            gameLogic.getP1().getShips().forEach(s -> {
                s.setMouseTransparent(true);
                s.setVisible(false);
                s.setStroke(Color.BLACK);
                s.setStrokeWidth(1);
            });
            gameLogic.getP2().getShips().forEach(s -> {
                s.setMouseTransparent(true);
                s.setVisible(false);
                s.setStroke(Color.BLACK);
                s.setStrokeWidth(1);
            });
            gameLogic.getP1().getGameBoard().getBoardCells().forEach(c-> {
                c.setFill(gameLogic.getP1().getGameBoard().getDefaultCellColor());
                c.setOpacity(1);
            });
            gameLogic.getP2().getGameBoard().getBoardCells().forEach(c-> {
                c.setFill(gameLogic.getP1().getGameBoard().getDefaultCellColor());
                c.setOpacity(1);
            });

            gameLogic.getP1().getGameBoard().getBoardPane().setVisible(false);
            gameLogic.getP2().getGameBoard().getBoardPane().setVisible(false);
            
            mainController.getBattleController().createHighlight(gameLogic.getP1().getGameBoard());

            mainController.getBattleController().setPlayingPlayerName(gameLogic.getP1().getName());
            mainController.getBattleController().setNamesAboveBoards();
        }
    }

    @FXML
    private void onPreviousButtonClick() {
    	AudioPlayer.playClickSfx();
        if (playingPlayer.equals(gameLogic.getP2())) {  // vaihdetaan pelaajaa
            changeP2ToP1();
        } else {  // edetään edelliseen sceneen
            warning.setVisible(false);
            mainController.getRootPane().setCenter(mainController.getSetup2Controller().getSetup2());
        }
    }

    @FXML
    private void onResetButtonClick() {
        AudioPlayer.playClickSfx();
        reset();
    }

    // Vaihtaa UI:n takaisin p1 varten jos mennään takaisinpäin.
    public void changeP2ToP1() {
        btnNext.textProperty().bind(ObservableResourceFactory.getStringBinding("nextButton3"));
        warning.setVisible(false);
        pane.getChildren().remove(playingPlayer.getGameBoard().getBoardPane());
        playingPlayer.getShips().forEach(s -> pane.getChildren().remove(s));

        playingPlayer = gameLogic.getP1();
        setName();
        pane.getChildren().add(playingPlayer.getGameBoard().getBoardPane());

        gameLogic.getP1().getGameBoard().removeShipsFromPane(pane);
        gameLogic.getP1().getGameBoard().setShipsToPane(pane);

        calculateAvailableShips();
    }

    // Kertoo ovatko kaikki pelaajan laivat asetettu laudalle.
    private boolean shipsArePlaced() {
        for (Ship s : playingPlayer.getShips()) {
            if (!playingPlayer.getGameBoard().getBoardBounds().contains(s.getBoundsInParent())) {
                warning.setVisible(true);
                return false;
            }
        }
        return true;
    }

    // Asettaa pelaajan nimen setup 3 ohjetekstiin.
    public void setName() {
        instructionsName.setText(playingPlayer.getName());
    }

    // Laskee laivatyypeittäin montako laivaa on vielä pistämättä pelilaudalle. Määrä näytetään jos tyypin määrä > 1.
    // Laskenta tapahtuu perustuen laivojen bounds arvoon, törmäävätkö ne niiden alla olevan laskijan boundsien kanssa.
    public void calculateAvailableShips() {
        carriers = 0;
        battleships = 0;
        cruisers = 0;
        submarines = 0;
        destroyers = 0;

        carrierAmount.setText("");
        battleshipAmount.setText("");
        cruiserAmount.setText("");
        submarineAmount.setText("");
        destroyerAmount.setText("");

        playingPlayer.getShips().forEach(ship -> {
            switch (ship.getType()) {
            case "carrier":
                if (ship.getBoundsInParent().intersects(carrierCounter.getBoundsInParent())) {
                    carriers += 1;
                    if (carriers > 1) carrierAmount.setText(String.valueOf(carriers));
                }
            case "battleship":
                if (ship.getBoundsInParent().intersects(battleshipCounter.getBoundsInParent())) {
                    battleships += 1;
                    if (battleships > 1) battleshipAmount.setText(String.valueOf(battleships));
                }
            case "cruiser":
                if (ship.getBoundsInParent().intersects(cruiserCounter.getBoundsInParent())) {
                    cruisers += 1;
                    if (cruisers > 1) cruiserAmount.setText(String.valueOf(cruisers));
                }
            case "submarine":
                if (ship.getBoundsInParent().intersects(submarineCounter.getBoundsInParent())) {
                    submarines += 1;
                    if (submarines > 1) submarineAmount.setText(String.valueOf(submarines));
                }
            case "destroyer":
                if (ship.getBoundsInParent().intersects(destroyerCounter.getBoundsInParent())) {
                    destroyers += 1;
                    if (destroyers > 1) destroyerAmount.setText(String.valueOf(destroyers));
                }
            }
        });
    }

    //Laivat alkupaikoilleen
    public void reset() {
        playingPlayer.getGameBoard().getBoardCells().forEach(cell -> {
            cell.setFill(playingPlayer.getGameBoard().getDefaultCellColor());
            cell.setOpacity(1);
        });
        for(Ship ship : playingPlayer.getShips()) {
            ship.setX(ship.getResetX());
            ship.setY(ship.getResetY());
            ship.setRotate(0);
            ship.setMouseTransparent(false);
            ship.setOpacity(1);
        }
        calculateAvailableShips();
    }

    // Piilottaa laivojen laskemiseen käyetyt nodet
    private void hideShipCounters() {
        carrierCounter.setVisible(false);
        battleshipCounter.setVisible(false);
        cruiserCounter.setVisible(false);
        submarineCounter.setVisible(false);
        destroyerCounter.setVisible(false);
    }

    // Bindaa scenen tekstit kileiasetukseen
    private void bindTexts() {
        instructions.textProperty().bind(ObservableResourceFactory.getStringBinding("instruction3"));
        rotateInstructions.textProperty().bind(ObservableResourceFactory.getStringBinding("rotateInstructions"));
        btnReset.textProperty().bind(ObservableResourceFactory.getStringBinding("resetButton"));
        btnPrevious.textProperty().bind(ObservableResourceFactory.getStringBinding("previousButton2"));
        btnNext.textProperty().bind(ObservableResourceFactory.getStringBinding("nextButton3"));
        warning.textProperty().bind(ObservableResourceFactory.getStringBinding("warningDragShips"));
    }
    
    public VBox getSetup3() {
        return this.setup3;
    }

    public Button getBtnNext() {
        return btnNext;
    }

    public Pane getPane() {
        return this.pane;
    }
}