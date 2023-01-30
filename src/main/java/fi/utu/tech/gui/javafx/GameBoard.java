package fi.utu.tech.gui.javafx;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class GameBoard {
	private Player player;
	
	private double x;
    private double y;

    private double failX; // jos laivan asetus epäonnistuu niin lähetetään takas näihin koordinaateihin
    private double failY;

    private int cells; // ruutumäärä, esim. 10x10 -> cells = 10
    private ArrayList<Rectangle> boardCells;
    private int cellSize = 40; // ruudun koko
    private int size; // laudan pituus ja leveys
    private BoundingBox boardBounds; // laudan rajat
    private Pane boardPane; // ruudut mäiskitään tähän paneen
    private ImagePattern blockedCell;
    private ImagePattern missedCell;
    private ImagePattern hitCell;
    private ImagePattern destroyedCell;
    private Color defaultCellColor = Color.web("#4882ab");
    private Color redColor = Color.web("#c61d1d");

    private ArrayList<Ship> ships; // saadaan pelaajalta
    private double rotation; // apuna laivan asettelussa jos käännetään
    private EventHandler<KeyEvent> rTypedHandler; // R näppäin handleri
    private Tooltip dragTip; // laivan tooltip
    
    private GameLogic gameLogic = GameLogic.getInstance();

    public GameBoard(Player player, int cells) {
    	this.player = player;
    	this.cells = cells;
    	this.size = this.cells * cellSize;
        boardPane = new Pane();
        boardPane.setMaxWidth(size);
        boardPane.setMinWidth(size);
        boardPane.setMaxHeight(size);
        boardPane.setMaxHeight(size);
        boardPane.setMouseTransparent(true);
        boardPane.setBackground(new Background(new BackgroundFill(defaultCellColor, CornerRadii.EMPTY, Insets.EMPTY)));
        boardCells = new ArrayList<>();
        Image blocked = new Image(getClass().getResource("blockedcell.png").toExternalForm());
        this.blockedCell = new ImagePattern(blocked);
        Image missed = new Image(getClass().getResource("splash.png").toExternalForm());
        this.missedCell = new ImagePattern(missed);
        Image hit = new Image(getClass().getResource("explosion.png").toExternalForm());
        this.hitCell = new ImagePattern(hit);
        Image destroyed = new Image(getClass().getResource("destroyedcell.png").toExternalForm());
        this.destroyedCell = new ImagePattern(destroyed);
        dragTip = new Tooltip();
        dragTip.textProperty().bind(ObservableResourceFactory.getStringBinding("tooltip"));
        drawBoard();
    }

    // Luo pelilaudan
    private void drawBoard() {
        for (int i = 0; i < size; i += cellSize) {
            for (int j = 0; j < size; j += cellSize) {
                Rectangle boardCell = new Rectangle(i, j, cellSize, cellSize);
                boardCell.setFill(defaultCellColor);
                boardCell.setStroke(Color.BLACK);

                boardCells.add(boardCell);
                boardPane.getChildren().add(boardCell);

                boardCell.setOnMouseClicked(e -> cellClicked(e, boardCell));
                boardCell.setOnMouseEntered(e-> cellEntered(e, boardCell));
                boardCell.setOnMouseExited(e -> cellExited(e, boardCell));
            }
        }
        setBoardCoordinates(400, 0);
        boardBounds = new BoundingBox(
                boardPane.getLayoutX() - cellSize / 2, boardPane.getLayoutY() - cellSize / 2,
                size + cellSize, size + cellSize
        );}

    public void setBoardCoordinates(double x, double y) {
        boardPane.setLayoutX(x);
        boardPane.setLayoutY(y);
    }

    // Lisää handlerit pelaajan laivoihin
    public ArrayList<Ship> generateShips() {
        ships = player.getShips();

        for (int i = 0; i < ships.size(); i++) {
            Ship s = ships.get(i);
            s.setOnMousePressed(event -> shipPressed(event, s));
            s.setOnMouseDragged(event -> shipDragged(event, s));
            s.setOnMouseReleased(event -> shipReleased(event, s));
            s.setOnMouseEntered(event -> shipEntered(event, s));
            s.setOnMouseExited(event -> {
                s.setCursor(Cursor.DEFAULT);
                s.setOpacity(1);
            });
            if (s.getType().equals("carrier")) {
                s.setX(30);
                s.setY(50);
            }
            if (s.getType().equals("battleship")) {
                s.setX(70);
                s.setY(100);
            }
            if (s.getType().equals("cruiser")) {
                s.setX(110);
                s.setY(150);
            }
            if (s.getType().equals("submarine")) {
                s.setX(110);
                s.setY(200);
            }
            if (s.getType().equals("destroyer")) {
                s.setX(150);
                s.setY(250);
            }
            s.setReset(s.getX(), s.getY());
        }
        return ships;
    }

    public void setShipsToPane(Pane pane) {
        ships.forEach(s -> pane.getChildren().add(s));
    }

    public void removeShipsFromPane(Pane pane) {
        ships.forEach(s -> pane.getChildren().remove(s));
    }

    public void relocateAllShips(double x, double y) {
        ships.forEach(s -> s.setX(s.getX() + x));
        ships.forEach(s -> s.setY(s.getY() + y));
    }

    public int getBoardSize() {
        return this.cells*cells;
    };

    public BoundingBox getBoardBounds() {
        return this.boardBounds;
    }

    public void removeShipsFromGameBoard() {
        this.ships = null;
    }

    // Kun pelilaudan ruutua klikataan
    private void cellClicked(MouseEvent event, Rectangle boardCell) {
        //Event joka kertoo onko ruudun kohdalla laiva, ja laittaa osuman laivalle/kertoo uppoamisesta
        boolean isOccupied = false;
        boolean sunk = false;

        for (Ship s : ships) {
            var shipBounds = s.localToScene(s.getBoundsInLocal());
            if (shipBounds.contains(event.getSceneX(), event.getSceneY())) {
                isOccupied = true;
                boardCell.setFill(hitCell);
                boardCell.setMouseTransparent(true);
                gameLogic.getMainController().getBattleController().setHitStatus("hit");
                AudioPlayer.playHitSfx();
                ParticlePlayer.playExplosion(boardPane, event.getX(), event.getY());
                s.hit();
                if (s.getHitPoints() <= 0) {
                    sunk = true;
                    System.out.println("Laiva upposi");
                    s.setSunk();
                    s.setVisible(true);
                    s.toFront();
                    s.setFill(s.getTransParentPattern());
                    gameLogic.getMainController().getBattleController().setHitStatus("sunk");
                    AudioPlayer.playSunkSfx();
                    boardCell.setStroke(Color.BLACK);
                    boardCell.setStrokeWidth(0);
                    colorOccupiedCells(defaultCellColor);
                    gameLogic.checkWin();
                }
                break;
            }
        }

        if (isOccupied) {
            if (!sunk) {
                boardCell.setStroke(Color.BLACK);
                boardCell.setStrokeWidth(1);
            }
            return;
        }

        if(this.player.equals(gameLogic.getP2())) {
            System.out.println("Pelaaja 1:n vuoro päättyy");
            gameLogic.getP2().getGameBoard().setNonClickable(true);
            gameLogic.getMainController().getBattleController().hideP2Highlight();
        } else if(this.player.equals(gameLogic.getP1())) {
            System.out.println("Pelaaja 2:n vuoro päättyy");
            gameLogic.getP1().getGameBoard().setNonClickable(true);
            gameLogic.getMainController().getBattleController().hideP1Highlight();
        }
        ParticlePlayer.playWaterSplash(boardPane, event.getX(), event.getY());
        boardCell.setFill(missedCell);
        boardCell.setMouseTransparent(true);
        boardCell.setStroke(Color.BLACK);
        boardCell.setStrokeWidth(1);
        AudioPlayer.playMissSfx();
        gameLogic.getMainController().getBattleController().setHitStatus("miss");
        gameLogic.startChangeTimer();
    }

    // Kursori saapuu ruudun päälle
    private void cellEntered(MouseEvent event, Rectangle boardCell) {
        if (boardCell.getFill().equals(defaultCellColor)) {
            boardCell.setCursor(Cursor.CROSSHAIR);
            boardCell.setFill(Color.web("#3abefc"));
            boardCell.setStroke(redColor);
            boardCell.setStrokeWidth(3);
            boardCell.toFront();
        }
    }

    // Kursori lähtee ruudusta
    private void cellExited(MouseEvent event, Rectangle boardCell) {
        if (boardCell.getFill().equals(Color.web("#3abefc"))) {
            boardCell.setFill(defaultCellColor);
            boardCell.setStroke(Color.BLACK);
            boardCell.setStrokeWidth(1);
        }
    }

    // Kursori laivan päällä
    private void shipEntered(MouseEvent event, Ship ship) {
        ship.setCursor(Cursor.OPEN_HAND);
        ship.setOpacity(0.75);
        Tooltip.install(ship, dragTip);
    }

    // Kun laivaa klikataan
    private void shipPressed(MouseEvent event, Ship ship) {
        rTypedHandler = createNewRHandler(ship);
        ship.addEventFilter(KeyEvent.KEY_TYPED, rTypedHandler);

        ship.setCursor(Cursor.MOVE);
        ship.requestFocus();
        ship.toFront();
        ship.setOpacity(0.5);

        x = event.getSceneX();
        y = event.getSceneY();

        failX = ship.getX();
        failY = ship.getY();
        rotation = ship.getRotate();

        colorBlockedCells(ship, defaultCellColor);
        ships.forEach(s -> {
            if (s == ship) return;
            colorBlockedCells(s, null);
        });
    }

    // Drag and dropin drag ominaisuus
    private void shipDragged(MouseEvent event, Ship ship) {
        ship.setCursor(Cursor.MOVE);
        ship.setOpacity(0.5);

        double deltaX = event.getSceneX() - x ;
        double deltaY = event.getSceneY() - y ;

        ship.setX(ship.getX() + deltaX);
        ship.setY(ship.getY() + deltaY);

        x = event.getSceneX() ;
        y = event.getSceneY() ;

        checkCollision(ship);
    }

    // Drag and dropin drop ominaisuus
    private void shipReleased(MouseEvent event, Ship ship) {
        ship.removeEventFilter(KeyEvent.KEY_TYPED, rTypedHandler);

        ship.setCursor(Cursor.OPEN_HAND);

        calculateShipsOutsideBoard();

        if (checkCollision(ship)) {
            sendShipBack(ship); return;
        }
        ship.setProtectiveBounds();

        // Kääntämättömät laivat
        if (ship.getRotate() == 0){
            ship.setX( (cellSize / 2) + (cellSize * ((int)(ship.getX()+20) / cellSize))-20 );
            ship.setY( (cellSize / 2) + (cellSize * ((int)(ship.getY()+20)  / cellSize))-20 );
        // Käännetyt laivat kokoa 3 tai 5
        } else if (ship.getSize() % 2 != 0){
            ship.setX( (cellSize / 2) + (cellSize * ((int)(ship.getX()+20) / cellSize))-20 );
            ship.setY( (cellSize / 2) + (cellSize * ((int)(ship.getY()+20)  / cellSize))-20 );
        // Käännetyt laivat kokoa 2 tai 4
        } else {
            ship.setX( cellSize / 2 + cellSize * ((int)(ship.getX()) / cellSize) );
            ship.setY( cellSize / 2 + cellSize * ((int)(ship.getY()) / cellSize ));
        }

        colorBlockedCells(ship, null);
        ship.setOpacity(1);
    }

    // Handleri R näppäimen painoa varten
    private EventHandler<KeyEvent> createNewRHandler(Ship ship) {
        return event -> {
            if (event.getCharacter().equals("r")) {
                if (ship.getRotate() == 0) {
                    ship.setRotate(90);
                } else {
                    ship.setRotate(0);
                }
                checkCollision(ship);
            }
        };
    }

    // Värittää laivan kyljissä, edessä ja takana olevat blokatut ruudut
    private void colorBlockedCells(Ship ship, Color color) {
        var bX = ship.getBoundsForBoardColoringX();
        var boundsX = new BoundingBox(bX.getMinX()-400, bX.getMinY(), bX.getWidth(), bX.getHeight());
        var bY = ship.getBoundsForBoardColoringY();
        var boundsY = new BoundingBox(bY.getMinX()-400, bY.getMinY(), bY.getWidth(), bY.getHeight());
        boardCells.forEach(c -> {
            if (c.getBoundsInParent().intersects(boundsX) || c.getBoundsInParent().intersects(boundsY)) {
                if (color == null) {
                    c.setFill(blockedCell);
                    c.setOpacity(0.5);
                } else {
                    c.setFill(color);
                    c.setOpacity(1);
                }
            }
        });
    }

    // Värittää laivan alla olevat ruudut
    public void colorOccupiedCells(Color color) {
        for (Ship s : ships) {
            boardCells.forEach(c -> {
                if (c.getFill().equals(hitCell) && !s.isSunk()) {
                    return;
                } else if (c.getBoundsInParent().intersects(s.getInnerBounds())) {
                    if (s.isSunk()) {
                        c.setFill(destroyedCell);
                        c.setStrokeWidth(0);
                    } else {
                        c.setFill(color);
                    }
                }
            });
        }
    }

    // Tarkistaa törmääkö alus toiseen laivaan tai onko laudan ulkopuolella
    private boolean checkCollision(Ship ship) {
        if (boardBounds.contains(ship.getBoundsInParent())) {
            ship.setFill(ship.getDefaultPattern());
            ship.setStrokeWidth(0);
        } else {
            ship.setFill(ship.getCollisionPattern());
        	return true;
        }
        for (Ship s : ships) {
            if (s == ship) continue;
            s.setProtectiveBounds();
            if (s.getProtectiveBoundX().intersects(ship.getInnerBounds())) {
                ship.setFill(ship.getCollisionPattern());
                ship.setStrokeWidth(4);
                return true;
            } else if (s.getProtectiveBoundY().intersects(ship.getInnerBounds())) {
                ship.setFill(ship.getCollisionPattern());
                ship.setStrokeWidth(4);
                return true;
            }
        }
        return false;
    }

    // Lähettää laivan viimeisimpään olipaikkaan, jos ei pystytty asettamaan.
    private void sendShipBack(Ship ship) {
        if (ship.getRotate() != rotation) {
            ship.setRotate(rotation);
        }
        ship.setX(failX);
        ship.setY( failY );
        ship.setOpacity(1);
        ship.setFill(ship.getDefaultPattern());
        ship.setStrokeWidth(0);
        colorBlockedCells(ship, null);
    }

    public Pane getBoardPane() {
    	return this.boardPane;
    }
    
    public void setNonClickable(Boolean b) {
    	this.boardPane.setMouseTransparent(b);
    }

    public ArrayList<Rectangle> getBoardCells() {
        return this.boardCells;
    }

    public int getBoardWidth() {
        return size;
    }

    public Color getDefaultCellColor() {
        return defaultCellColor;
    }

    // Ajastin laivojen laskemiseen setup3:ssa. Tarvitaan pieni viive, että laskenta onnistuisi oikein
    public void calculateShipsOutsideBoard() {
        Runnable timer = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> gameLogic.getMainController().getSetup3Controller().calculateAvailableShips());
        };

        new Thread(timer).start();
    }
}
