package fi.utu.tech.gui.javafx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

/**
 * Luokka lataa laivojen kuvat ja luo niistä erilaisia ImagePatterneita, joita käytetään laivan eri tilan näyttämiseen.
 * Default pattern = musta kuva laivasta, jonka tausta on transparent.
 * Destroyed pattern = punainen kuva laivasta, jonka tausta on transparent.
 * Transparent pattern = laivan kuva on transparent, ja tausta on pelilaudan ruudun värinen.
 */

public final class ShipPatternFactory {
    private static final Image imgBattleship = new Image(ShipPatternFactory.class.getResource("battleship.png").toExternalForm());
    private static final Image imgSubmarine = new Image(ShipPatternFactory.class.getResource("submarine.png").toExternalForm());
    private static final Image imgCarrier = new Image(ShipPatternFactory.class.getResource("carrier.png").toExternalForm());
    private static final Image imgDestroyer = new Image(ShipPatternFactory.class.getResource("destroyer.png").toExternalForm());
    private static final Image imgCruiser = new Image(ShipPatternFactory.class.getResource("cruiser.png").toExternalForm());

    private static final Image destroyedCarrier = createColoredShip(imgCarrier, Color.RED);
    private static final Image destroyedBattleship = createColoredShip(imgBattleship, Color.RED);
    private static final Image destroyedSubmarine = createColoredShip(imgSubmarine, Color.RED);
    private static final Image destroyedCruiser = createColoredShip(imgCruiser, Color.RED);
    private static final Image destroyedDestroyer = createColoredShip(imgDestroyer, Color.RED);

    private static final Image transparentCarrier = createTransparentShip(imgCarrier, 5);
    private static final Image transparentBattleship = createTransparentShip(imgBattleship, 4);
    private static final Image transparentSubmarine = createTransparentShip(imgSubmarine, 3);
    private static final Image transparentCruiser = createTransparentShip(imgCruiser, 3);
    private static final Image transparentDestroyer = createTransparentShip(imgDestroyer, 2);

    private static ImagePattern defaultCarrierPattern;
    private static ImagePattern defaultBattleshipPattern;
    private static ImagePattern defaultSubmarinePattern;
    private static ImagePattern defaultCruiserPattern;
    private static ImagePattern defaultDestroyerPattern;

    private static ImagePattern destroyedCarrierPattern;
    private static ImagePattern destroyedBattleshipPattern;
    private static ImagePattern destroyedSubmarinePattern;
    private static ImagePattern destroyedCruiserPattern;
    private static ImagePattern destroyedDestroyerPattern;

    private static ImagePattern transparentCarrierPattern;
    private static ImagePattern transparentBattleshipPattern;
    private static ImagePattern transparentSubmarinePattern;
    private static ImagePattern transparentCruiserPattern;
    private static ImagePattern transparentDestroyerPattern;

    public static void createImagePatterns() {
        defaultCarrierPattern = new ImagePattern(imgCarrier);
        defaultBattleshipPattern = new ImagePattern(imgBattleship);
        defaultSubmarinePattern = new ImagePattern(imgSubmarine);
        defaultCruiserPattern = new ImagePattern(imgCruiser);
        defaultDestroyerPattern = new ImagePattern(imgDestroyer);

        destroyedCarrierPattern = new ImagePattern(destroyedCarrier);
        destroyedBattleshipPattern = new ImagePattern(destroyedBattleship);
        destroyedSubmarinePattern = new ImagePattern(destroyedSubmarine);
        destroyedCruiserPattern = new ImagePattern(destroyedCruiser);
        destroyedDestroyerPattern = new ImagePattern(destroyedDestroyer);

        transparentCarrierPattern = new ImagePattern(transparentCarrier);
        transparentBattleshipPattern = new ImagePattern(transparentBattleship);
        transparentSubmarinePattern = new ImagePattern(transparentSubmarine);
        transparentCruiserPattern = new ImagePattern(transparentCruiser);
        transparentDestroyerPattern = new ImagePattern(transparentDestroyer);
    }

    public static ImagePattern getCarrierPattern() {
        return defaultCarrierPattern;
    }

    public static ImagePattern getBattleshipPattern() {
        return defaultBattleshipPattern;
    }

    public static ImagePattern getSubmarinePattern() {
        return defaultSubmarinePattern;
    }

    public static ImagePattern getCruiserPattern() {
        return defaultCruiserPattern;
    }

    public static ImagePattern getDestroyerPattern() {
        return defaultDestroyerPattern;
    }

    // ___________________________________________________________

    public static ImagePattern getCarrierPatternDestroyed() {
        return destroyedCarrierPattern;
    }

    public static ImagePattern getBattleshipPatternDestroyed() {
        return destroyedBattleshipPattern;
    }

    public static ImagePattern getSubmarinePatternDestroyed() {
        return destroyedSubmarinePattern;
    }

    public static ImagePattern getCruiserPatternDestroyed() {
        return destroyedCruiserPattern;
    }

    public static ImagePattern getDestroyerPatternDestroyed() {
        return destroyedDestroyerPattern;
    }

    //____________________________________________________________

    public static ImagePattern getCarrierPatternTransparent() {
        return transparentCarrierPattern;
    }

    public static ImagePattern getBattleshipPatternTransparent() {
        return transparentBattleshipPattern;
    }

    public static ImagePattern getSubmarinePatternTransparent() {
        return transparentSubmarinePattern;
    }

    public static ImagePattern getCruiserPatternTransparent() {
        return transparentCruiserPattern;
    }

    public static ImagePattern getDestroyerPatternTransparent() {
        return transparentDestroyerPattern;
    }

    // Luo laivasta värikkään (pelissä punainen) patternin.
    private static Image createColoredShip(final Image sourceImage, final Color blendColor) {
        final double r = blendColor.getRed();
        final double g = blendColor.getGreen();
        final double b = blendColor.getBlue();
        final int w = (int) sourceImage.getWidth();
        final int h = (int) sourceImage.getHeight();
        final WritableImage outputImage = new WritableImage(w, h);
        final PixelWriter writer = outputImage.getPixelWriter();
        final PixelReader reader = sourceImage.getPixelReader();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
               writer.setColor(x, y, new Color(r, g, b, reader.getColor(x, y).getOpacity()));
            }
        }
        return outputImage;
    }

    // Luo laivasta transparent patternin
    private static Image createTransparentShip(final Image sourceImage, int shipSize) {
        final int w = (int) sourceImage.getWidth();
        final int h = (int) sourceImage.getHeight();
        final WritableImage outputImage = new WritableImage(w, h);
        final PixelWriter writer = outputImage.getPixelWriter();
        final PixelReader reader = sourceImage.getPixelReader();

        int partSize = w/shipSize;
        int helper = 0;

        if (shipSize == 2) {
            helper = 20;
        } else if (shipSize == 3) {
            if (w == 800) helper = 8;
            if (w == 2289) helper = 16;
        } else if (shipSize == 4) {
            helper = 4;
        } else if (shipSize == 5) {
            helper = 2;
        }

        for (int y = 0; y < h; y++) {
            int nextLine = partSize;
            for (int x = 0; x < w; x++) {
                if (reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    writer.setColor(x, y, Color.web("#4882ab"));
                } else {
                    writer.setColor(x, y, Color.TRANSPARENT);
                }
                if (x > nextLine-helper && x < nextLine+helper ) {
                    if (reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                        writer.setColor(x, y, Color.BLACK);
                    }
                }
                if (x > nextLine+helper ) {
                    nextLine = nextLine + partSize;
                }
            }
        }
        return outputImage;
    }
}
