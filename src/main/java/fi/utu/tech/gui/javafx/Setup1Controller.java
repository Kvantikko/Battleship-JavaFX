package fi.utu.tech.gui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

//Main menu. Ensimmäinen ruutu, jossa pelaajat asettavat nimensä.
public class Setup1Controller {

    public MainController mainController;
    private GameLogic gameLogic = GameLogic.getInstance();

    // bindaa tekstit kieli asetukseen
    public void initialize() {
        p1.textProperty().bind(ObservableResourceFactory.getStringBinding("p1NameEnter"));
        p2.textProperty().bind(ObservableResourceFactory.getStringBinding("p2NameEnter"));
        txtFieldP1.promptTextProperty().bind(ObservableResourceFactory.getStringBinding("required"));
        txtFieldP2.promptTextProperty().bind(ObservableResourceFactory.getStringBinding("required"));
        instructions.textProperty().bind(ObservableResourceFactory.getStringBinding("instruction1"));
        btnNext.textProperty().bind(ObservableResourceFactory.getStringBinding("nextButton1"));
    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private VBox setup1;

    @FXML
    private Label instructions;

    @FXML
    private Label p1;

    @FXML
    private Label p2;

    @FXML
    private Label warning;

    @FXML
    private TextField txtFieldP1, txtFieldP2;


    @FXML
    private Button btnNext;

    //Asettaa mainControllerin rootin keskiosaan seuraavan näkymän, kun painetaan next painiketta. Luo pelaajat jos
    // ei olla vielä luotu
    @FXML
    private void onNextButtonClick() {
    	AudioPlayer.playClickSfx();
        if (!isValidFields()) return;
        warning.setVisible(false);

        if (gameLogic.getP1() == null || gameLogic.getP2() == null) {
            gameLogic.createP1(txtFieldP1.getText());
            gameLogic.createP2(txtFieldP2.getText());
        }

        gameLogic.getP1().setName(txtFieldP1.getText());
        gameLogic.getP2().setName(txtFieldP2.getText());

        mainController.getUpperButtons().getChildren().remove(mainController.getBtnMainMenu());
        mainController.getUpperButtons().getChildren().add(3, mainController.getBtnMainMenu());
        mainController.getRootPane().setCenter(mainController.getSetup2Controller().getSetup2());
    };

    // jos pelaaja palaa takaisin nimi setuppiin ja vaihtaa nimeä -> palauttaa true
    private boolean nameChanged() {
        System.out.println("here");
        System.out.println((txtFieldP1.getText() == gameLogic.getP1().getName()) ||
                (txtFieldP2.getText() == gameLogic.getP2().getName()));
        return  (txtFieldP1.getText() == gameLogic.getP1().getName()) ||
                (txtFieldP2.getText() == gameLogic.getP2().getName());
    }

    //Tarkistaa onko pelaajien nimet valideja. Nimet eivät saa olla samat tai tyhjiä eivätkä liian pitkiä.
    private boolean isValidFields() {
        if (txtFieldP1.getText() == null || txtFieldP1.getText().trim().isEmpty() ||
                txtFieldP2.getText() == null || txtFieldP2.getText().trim().isEmpty()) {
            warning.textProperty().bind(ObservableResourceFactory.getStringBinding("warningBothNames"));
            warning.setVisible(true);
            return false;
        } else if (txtFieldP1.getText().equals(txtFieldP2.getText())) {
            warning.textProperty().bind(ObservableResourceFactory.getStringBinding("warningUniqueNames"));
            warning.setVisible(true);
            return false;
        } else if (txtFieldP1.getText().length() > 20 || txtFieldP2.getText().length() > 20) {
            warning.textProperty().bind(ObservableResourceFactory.getStringBinding("warningLongNames"));
            warning.setVisible(true);
            return false;
        }
        return true;
    }

    //Palauttaa setup1:sen root noden.
    public VBox getSetup1() {
        return this.setup1;
    }
}
