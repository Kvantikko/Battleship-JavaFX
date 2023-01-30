package fi.utu.tech.gui.javafx;

import java.util.Locale;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import static fi.utu.tech.gui.javafx.ParticlePlayer.playWinAnimation;

//Pelin "logiikka". Sisältää metodeja ja attribuutteja joita tarvitaan useissa luokissa.
public final class GameLogic {
	
	private MainController mainController;
	private Player p1;
	private Player p2;
	
	private final static GameLogic INSTANCE = new GameLogic();
	
	private GameLogic() {}
	
	public static GameLogic getInstance() {
		return INSTANCE;
	}
	
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public MainController getMainController() {
		return this.mainController;
	}
	
	public void createP1(String p1Name) {
		this.p1 = new Player(p1Name);
	}

	public void createP2(String p2Name) {
		this.p2 = new Player(p2Name);
	}
	
	public Player getP1() {
		return p1;
	}
	
	public Player getP2() {
		return p2;
	}

	// Tarkastaa onko jompikumpi pelaajista voittotilanteessa
	public void checkWin() {
		if (p1.hasShipsLeft() && p2.hasShipsLeft()) {
			return;
		} else {
			Alert restart = new Alert(Alert.AlertType.CONFIRMATION);
			((Button) restart.getDialogPane().lookupButton(ButtonType.OK)).
					textProperty().bind(ObservableResourceFactory.getStringBinding("winAlertYes"));
			((Button) restart.getDialogPane().lookupButton(ButtonType.CANCEL)).
					textProperty().bind(ObservableResourceFactory.getStringBinding("winAlertNo"));
			restart.titleProperty().bind(ObservableResourceFactory.getStringBinding("winAlertTitle"));
			restart.setHeaderText(null);
			//Pelaaja 2 voitti
			if (!(p1.hasShipsLeft())) {
				System.out.print("Pelaaja 2 voitti!\n");
				AudioPlayer.playWinSfx();
				playWinAnimation(p2);
				getMainController().getBattleController().hideP1Highlight();
				if (ObservableResourceFactory.getResources().getLocale().equals(Locale.ENGLISH)) {
					restart.setContentText("Player "+  p2.getName() + " won. Play again?");
				} else {
					restart.setContentText("Pelaaja "+  p2.getName() + " voitti. Pelataanko uudelleen?");
				}
			//Pelaaja 1 voitti
			} else if (!(p2.hasShipsLeft())) {
				System.out.print("Pelaaja 1 voitti!\n");
				AudioPlayer.playWinSfx();
				playWinAnimation(p1);
				getMainController().getBattleController().hideP2Highlight();
				if (ObservableResourceFactory.getResources().getLocale().equals(Locale.ENGLISH)) {
					restart.setContentText("Player "+  p1.getName() + " won. Play again?");
				} else {
					restart.setContentText("Pelaaja "+  p1.getName() + " voitti. Pelataanko uudelleen?");
				}
			}
			Optional<ButtonType> choice = restart.showAndWait();
			//Jos halutaan pelata uudestaan heittää main menuun
			if (choice.get() == ButtonType.OK) {
				System.out.print("Game on\n");
				mainController.resetGame();
			}
			//Jos ei haluta pelata uudestaan -> heittää suoraan pihalle
			else if (choice.get() == ButtonType.CANCEL) {
				System.exit(0);
			}
		}
	
	}

	// Ajastettu väliruudun näyttö, jos pelaaja klikkaa peliruutua jossa ei ole laivaa
	public void startChangeTimer() {
		Runnable timer = () -> {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Platform.runLater(() -> mainController.getBattleController().showBreakScreen());
		};

		new Thread(timer).start();
	}
}
