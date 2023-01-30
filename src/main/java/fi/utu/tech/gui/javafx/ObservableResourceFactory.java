package fi.utu.tech.gui.javafx;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Luokka sisältää pelin tekstit suomeksi ja englanniksi, sekä tarjoaa StringBindingin niiden bindaamiseksi komponenttien
 * tesksteihin. Luokan avulla kielen vaihtaminen onnistuu ohjelman suorituksen aikana napin painaluksella.
 */

public final class ObservableResourceFactory {
    private static final String ENGLISH = ResourcesEng.class.getTypeName();
    private static final String FINNISH = ResourcesFin.class.getTypeName();
    private static ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();

    public static ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources ;
    }

    public static ResourceBundle getResources() {
        return resourcesProperty().get();
    }

    public static void setResources(ResourceBundle resources) {
        resourcesProperty().set(resources);
    }

    // Palauttaa uuden StringBindingin annetulle avaimelle.
    public static StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); }
            @Override
            public String computeValue() {
                return getResources().getString(key);
            }
        };
    }

    public static void setEnglish() {
        ObservableResourceFactory.setResources(ResourceBundle.getBundle(ENGLISH, Locale.ENGLISH));
    }

    public static void setFinnish() {
        ObservableResourceFactory.setResources(ResourceBundle.getBundle(FINNISH, Locale.forLanguageTag("fi")));
    }

    // Englannin kieliset teksit
    public static class ResourcesEng extends ListResourceBundle {
       private static final Locale locale = Locale.ENGLISH;

        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    // main controller
                    {"muteButton", "Mute music"},
                    {"unmuteButton", "Unmute music"},
                    {"instructionsButton", "Instructions"},
                    {"creditsButton", "Credits"},
                    {"quitButton", "Quit"},
                    {"mainMenuButton", "Main menu"},
                    {"instructionsWindow", "Main menu: \n"
                            + "- Enter player names for both players. Names cannot be the same. \n"
                            + "\n"
                            + "Adjust game settings: \n"
                            + "- Select size of the game board from 5x5 to 10x10, and select the amount of each ship type. \n"
                            + "- At least one ship must be selected. \n"
                            + "- Ships cannot take more space than what the board allows. \n"
                            + "\n"
                            + "Placing your ships: \n"
                            + "- Players take turns placing their ships. \n"
                            + "- Ships may touch each other only from the corners. \n"
                            + "- Press R key to rotate the ship. \n"
                            + "- Press the 'Restart' button on screen to remove all ships from board. \n"
                            + "- When both players are ready, the game will begin. \n"
                            + "\n"
                            + "Battle: \n"
                            + "- There is a screen in between the players turns where both players boards are hidden. \n"
                            + "- Player one starts. After their turn, player two will be asked to make their move. \n"
                            + "- Players may not see each others turns, but during their turn both players boards are visible, and the players own ships are visible. \n"
                            + "- After the winner has been decided, the game can be played again."},
                    {"creditsContent", "Credits: \n"
                            + "Oskari Savonen, Mikko Syrjälä, Jesse Vuontisjärvi, Teemu Kivimäki \n"
                            + "Background: https://pixabay.com/illustrations/war-ship-battle-ocean-water-3624522/ \n"
                            + "\n"
                            + "Music: Tommy Mutiu - Battle of the dragons https://pixabay.com/music/id-8037/ \n"
                            + "\n"
                            + "ZakharValaha - Rhythm of war https://pixabay.com/music/id-7233/"},
                    {"quitTitle", "Quitting Battleship"},
                    {"quitHeader", "Are you sure you want to quit?"},
                    {"mainMenuTitle", "Confirmation"},
                    {"mainMenuHeader", "Return to main menu?"},
                    {"cancelButton", "Cancel"},

                    // setup 1 controller
                    {"instruction1", "Welcome to JavaFX Battleship game! Please enter player names below."},
                    {"required", "Required"},
                    {"p1NameEnter", "Player 1 name"},
                    {"p2NameEnter", "Player 2 name"},
                    {"nextButton1", "Next ->"},
                    {"warningBothNames", "Both player names are required"},
                    {"warningUniqueNames", "Players must have unique names!"},
                    {"warningLongNames", "Max name length is 20 characters!"},

                    // setup 2 controller
                    {"instruction2", "Now please choose gameboard size and battleships."},
                    {"boardSize", "Board size"},
                    {"carriers", "Carrier"},
                    {"battleships", "Battleship"},
                    {"cruisers", "Cruiser"},
                    {"submarines", "Submarine"},
                    {"destroyers", "Destroyer"},
                    {"previousButton1", "<- Previous"},
                    {"nextButton2", "Next ->"},
                    {"warningTooManyShips", "Too many Ships"},
                    {"warningAtleastOne", "Select at least one ship"},

                    // setup 3 controller
                    {"instruction3", " please drag your ships to the board."},
                    {"rotateInstructions", "Press R to rotate ship while dragging"},
                    {"resetButton", "Reset"},
                    {"previousButton2", "<- Previous"},
                    {"nextButton3", "Next ->"},
                    {"startGameButton", "Start game!"},
                    {"warningDragShips", "You have to drag your ships to the gameboard before continuing!"},
                    {"tooltip", "Click and hold to drag"},

                    // battle controller
                    {"turnStatus", "It's your turn"},
                    {"hit", "Hit!"},
                    {"miss", "Miss!"},
                    {"sunk", "Ship sunk!"},
                    {"winAlertNo", "Quit"},
                    {"winAlertYes", "Play again"},
                    {"winAlertTitle", "Play again?"},
                    {"winAlertHeader", "Game over! Winner is:"},
            };
        }

        public Locale getLocale() {
            return locale;
        }
    }

    // Suomenkieliset tekstit
    public static class ResourcesFin extends ListResourceBundle {
        private static final Locale locale = Locale.forLanguageTag("fi");

        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    // main controller
                    {"muteButton", "Vaimenna"},
                    {"unmuteButton", "Musiikki päälle"},
                    {"instructionsButton", "Ohjeet"},
                    {"creditsButton", "Tekijät"},
                    {"quitButton", "Lopeta"},
                    {"mainMenuButton", "Päävalikko"},
                    {"instructionsWindow", "Päävalikko: \n"
                            + "- Aseta pelaajille eri nimet. \n"
                            + "\n"
                            + "Pelin asetukset: \n"
                            + "- Valitse pelilaudan koko 5x5 - 10x10 väliltä, ja valitse kunkin laivatyypin määrä.  \n"
                            + "- Vähintään yksi laiva tarvitaan pelin aloittamiseksi. \n"
                            + "- Laivat eivät voi viedä enemmän tilaa kuin pelilauta sallii. \n"
                            + "\n"
                            + "Laivojen asettelu: \n"
                            + "- Pelaajat laittavat laivat paikoilleen. Pelaaja 1 ensin. \n"
                            + "- Laivat saavat koskea toisiaan vain kulmista. \n"
                            + "- Paina R näppäintä kääntääksesi laivan vetämisen aikana. \n"
                            + "- Resetoi nappi poistaa laivat pelilaudalta. \n"
                            + "- Kun molemmat pelaajat ovat saanet laivansa paikoilleen, pelin voi aloittaa. \n"
                            + "\n"
                            + "Taistelu: \n"
                            + "- Pelaajien vuorojen välillä on ruutu, jolloin pelilaudat on piilotettu. \n"
                            + "- Pelaaja yksi aloittaa. Pelaajan 1 vuoron jälkeen, pelaajaa 2 pyydetään aloittamaan oma vuoronsa. \n"
                            + "- Pelaajan 1 vuoron aikana pelaaja 2 ei saa katsoa ruutua ja päinvastoin. \n"
                            + "- Vuorossa olevan pelaajan laivat näkyvät tämän laudalla, mutta vihollisen laivat ovat piilotettuja tämän laudalla. \n"
                            + "- Voittajan selvittyä peliä voidaan pelata uudelleen."},
                    {"creditsContent", "Tekijät: \n"
                            + "Oskari Savonen, Mikko Syrjälä, Jesse Vuontisjärvi, Teemu Kivimäki \n"
                            + "Background: https://pixabay.com/illustrations/war-ship-battle-ocean-water-3624522/ \n"
                            + "\n"
                            + "Music: Tommy Mutiu - Battle of the dragons https://pixabay.com/music/id-8037/ \n"
                            + "\n"
                            + "ZakharValaha - Rhythm of war https://pixabay.com/music/id-7233/"},
                    {"quitTitle", "Battleship Lopetus"},
                    {"quitHeader", "Haluatko varmasti lopettaa?"},
                    {"mainMenuTitle", "Varmistus"},
                    {"mainMenuHeader", "Palataanko päävalikkoon?"},
                    {"cancelButton", "Peruuta"},

                    // setup 1 controller
                    {"instruction1", "Tervetuloa JavaFX Battleship -peliin! Anna pelaajien nimet."},
                    {"required", "Vaaditaan"},
                    {"p1NameEnter", "Pelaajan 1 nimi"},
                    {"p2NameEnter", "Pelaajan 2 nimi"},
                    {"nextButton1", "Seuraava ->"},
                    {"warningBothNames", "Molempien pelaajien nimet vaaditaan!"},
                    {"warningUniqueNames", "Pelaajien nimien on oltava uniikit!"},
                    {"warningLongNames", "Maksimi nimen pituus on 20 merkkiä!"},

                    // setup 2 controller
                    {"instruction2", "Valitse pelilaudan koko ja laivat."},
                    {"boardSize", "Laudan koko"},
                    {"carriers", "Lentotukialus"},
                    {"battleships", "Taistelulaiva"},
                    {"cruisers", "Ristelijä"},
                    {"submarines", "Sukellusvene"},
                    {"destroyers", "Hävittäjä"},
                    {"previousButton1", "<- Edellinen"},
                    {"nextButton2", "Seuraava ->"},
                    {"warningTooManyShips", "Liian monta laivaa!"},
                    {"warningAtleastOne", "Valitse vähintään yksi laiva!"},

                    // setup 3 controller
                    {"instruction3", " vedä aluksesi pelilaudalle"},
                    {"rotateInstructions", "Paina R näppäintä kääntääksesi \n laivaa vetämisen aikana"},
                    {"resetButton", "Resetoi"},
                    {"previousButton2", "<- Edellinen"},
                    {"nextButton3", "Seuraava ->"},
                    {"startGameButton", "Aloita peli!"},
                    {"warningDragShips", "Sinun täytää vetää laivat pelilaudalle jatkaaksesi!"},
                    {"tooltip", "Klikkaa ja pidä pohjassa vetääksesi"},

                    // battle controller
                    {"turnStatus", "Sinun vuorosi"},
                    {"hit", "Osuma!"},
                    {"miss", "Ohi!"},
                    {"sunk", "Laiva upposi!"},
                    {"winAlertNo", "Lopeta"},
                    {"winAlertYes", "Pelaa uudelleen"},
                    {"winAlertTitle", "Pelataanko uudelleen?"},
                    {"winAlertHeader", "Peli päättyi! Voitaja on:"},
            };
        }

        public Locale getLocale() {
            return locale;
        }
    }

}
