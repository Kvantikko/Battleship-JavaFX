package fi.utu.tech.gui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BattleController {

    private MainController mainController;
    private GameLogic gameLogic = GameLogic.getInstance();
    
    public void initialize() {
        // Controllerit alusteaan käänteisessä järjestyksessä (ensin battle ja lopuksi main), joten joitain alustuksia
		// tehdään tässsä kohtaa
		ShipPatternFactory.createImagePatterns();
		ObservableResourceFactory.setEnglish();
		showLabels(false);

		playerTurn.textProperty().bind(ObservableResourceFactory.getStringBinding("turnStatus"));
    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private VBox battle;

	@FXML
	private StackPane player1;

	@FXML
	private StackPane player2;

    @FXML
    private Pane info;
    
    @FXML
    private Button btnEndTurn;

	@FXML
	private Label p1Name;

	@FXML
	private Label p2Name;

	@FXML
	private Label playerTurn;

	@FXML
	private Label playingPlayerName;

	@FXML
	private Label hitStatus;

	private Rectangle highlight;

	private Rectangle highlight2;

	// Asettaa UI:n vuoroon perustuen
    @FXML
    private void onEndTurnButtonPress() {
    	AudioPlayer.playClickSfx();
		Player p1 = gameLogic.getP1();
		Player p2 = gameLogic.getP2();
		btnEndTurn.setVisible(false);
		showLabels(true);
		hitStatus.setVisible(false);
		p1.getGameBoard().getBoardPane().setVisible(true);
		p2.getGameBoard().getBoardPane().setVisible(true);

		p1.getShips().forEach(ship -> {
			if (!ship.isSunk()) ship.setVisible(false);
			ship.toFront();
		});
		p2.getShips().forEach(ship -> {
			if (!ship.isSunk()) ship.setVisible(false);
			ship.toFront();
		});

		gameLogic.getP1().getGameBoard().getBoardCells().forEach(c -> {
			if (!c.getFill().equals(gameLogic.getP1().getGameBoard().getDefaultCellColor())) {
				c.toFront();
			}
		});
		gameLogic.getP2().getGameBoard().getBoardCells().forEach(c -> {
			if (!c.getFill().equals(gameLogic.getP2().getGameBoard().getDefaultCellColor())) {
				c.toFront();
			}
		});

		if (p1.getTurn()) {
			System.out.println("Pelaajan: "+ p1.getName() + " vuoro");
			p1.setTurn(false);
			p2.setTurn(true);
			p1.getGameBoard().setNonClickable(true);
			p2.getGameBoard().setNonClickable(false);
			p2.getGameBoard().colorOccupiedCells(p2.getGameBoard().getDefaultCellColor());
			p1.getShips().forEach(ship -> ship.setVisible(true));
			p1.getShips().forEach(ship -> {
				if (ship.isSunk()) ship.toFront();
			});
			p2.getShips().forEach(ship -> {
				if (ship.isSunk()) ship.toFront();
			});
			highlight2.setOpacity(0.75);
		} else {
			System.out.println("Pelaajan: " + p2.getName() + " vuoro");
			p1.setTurn(true);
			p2.setTurn(false);
			p1.getGameBoard().setNonClickable(false);
			p2.getGameBoard().setNonClickable(true);
			p1.getGameBoard().colorOccupiedCells(p1.getGameBoard().getDefaultCellColor());
			p2.getShips().forEach(ship -> ship.setVisible(true));
			p1.getShips().forEach(ship -> {
				if (ship.isSunk()) ship.toFront();
			});
			p2.getShips().forEach(ship -> {
				if (ship.isSunk()) ship.toFront();
			});
			highlight.setOpacity(0.75);
		}
    }

	// Luo punaisen ääriviivan lautojen ympärille
	public void createHighlight(GameBoard board) {
		highlight = new Rectangle(board.getBoardWidth()+ 8, board.getBoardWidth() + 8);
		highlight.setFill(Color.TRANSPARENT);
		highlight.setStrokeWidth(8);
		highlight.setStroke(Color.web("#c61d1d"));
		highlight.setOpacity(0);
		highlight.setMouseTransparent(true);

		highlight2 = new Rectangle(board.getBoardWidth()+ 8, board.getBoardWidth() + 8);
		highlight2.setFill(Color.TRANSPARENT);
		highlight2.setStrokeWidth(8);
		highlight2.setStroke(Color.web("#c61d1d"));
		highlight2.setOpacity(0);
		highlight2.setMouseTransparent(true);

		player1.getChildren().add(highlight);
		player2.getChildren().add(highlight2);
	}

	public void hideP1Highlight() {
		this.highlight.setOpacity(0);
	}

	// Asettaa pelaajien nimet lautojen ylle
	public void setNamesAboveBoards() {
		p1Name.setText(gameLogic.getP1().getName());
		p2Name.setText(gameLogic.getP2().getName());
	}
	public void hideP2Highlight() {
		this.highlight2.setOpacity(0);
	}

    public Pane getInfoPane() {
        return info;
    }

    public VBox getBattle() {
        return battle;
    }

	public Button getBtnEndTurn() {
		return btnEndTurn;
	}

	// Asettaa pelaajien laudat sceneen
	public void setBoardsToScene(Pane p1, Pane p2) {
		player1.getChildren().add(p1);
		player2.getChildren().add(p2);
	}

	public void setPlayingPlayerName(String text) {
		playingPlayerName.setText(text);
	}

	public void showLabels(Boolean b) {
		p1Name.setVisible(b);
		p2Name.setVisible(b);
		hitStatus.setVisible(b);
	}

	// Alustaa vuorojen välissä olevan ruudun
	public void showBreakScreen() {
		btnEndTurn.setVisible(true);

		showLabels(false);
		if (gameLogic.getP1().getTurn()) {
			mainController.getBattleController().setPlayingPlayerName(gameLogic.getP1().getName());
		} else {
			mainController.getBattleController().setPlayingPlayerName(gameLogic.getP2().getName());
		}
		mainController.getBattleController().getBtnEndTurn().setVisible(true);
		gameLogic.getP1().getGameBoard().getBoardPane().setVisible(false);
		gameLogic.getP2().getGameBoard().getBoardPane().setVisible(false);
	}

	// Asettaa laukauksesta kertovan viestin
	public void setHitStatus(String key) {
		hitStatus.textProperty().bind(ObservableResourceFactory.getStringBinding(key));
		hitStatus.setVisible(true);
	}
}
