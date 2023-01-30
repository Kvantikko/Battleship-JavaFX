package fi.utu.tech.gui.javafx;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Luokka on vastuussa hiukkas animaatioiden toistosta.
 */

public final class ParticlePlayer {

    private static final Random random = new Random();
    private static final ArrayList<Color> explosionColors =
            new ArrayList(List.of(Color.RED, Color.ORANGE, Color.GRAY, Color.ORANGERED));
    private static final ArrayList<Color> waterSplashColors =
            new ArrayList(List.of(Color.AQUA, Color.DEEPSKYBLUE, Color.WHITE, Color.MIDNIGHTBLUE));

    public static void playWaterSplash(Pane pane, double x, double y) {
        createParticleAnimation(pane, x, y, waterSplashColors);
    }

    public static void playExplosion(Pane pane, double x, double y) {
        createParticleAnimation(pane, x, y, explosionColors);

    }

    // Luo lyhen hiukkasanimaation annettuun paneen. Käytetään laivan osumassa ja huti laukauksessa.
    private static void createParticleAnimation(Pane pane, double x, double y, ArrayList<Color> colors) {
        for (int i=0; i<500; i++) {
            var r = random.nextInt(0,3);

           Circle c = new Circle(1, colors.get(r));

            pane.getChildren().add(c);

            TranslateTransition tran = new TranslateTransition(Duration.millis(1000), c);
            tran.setFromX(x);
            tran.setFromY(y);
            tran.setToX(tran.getFromX() + random.nextInt(-200, 200));
            tran.setToY(tran.getFromY() + random.nextInt(-200, 200));
            tran.setInterpolator(Interpolator.EASE_OUT);
            tran.play();

            FadeTransition ft = new FadeTransition(Duration.millis(1000), c);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();
        }
    }

    // Starttaa serpenttiinin heittämisen voittavan pelaajan pelilaudan takaa.
    public static void playWinAnimation(Player player) {
        final int size = 1200;
        final Rectangle[] rectangles = new Rectangle[size];
        final long[] delays = new long[size];
        final double[] angles = new double[size];
        final long duration = java.time.Duration.ofSeconds(4).toNanos();
        final Random random = new Random();

        for (int i = 0; i < size; i++) {
            rectangles[i] = new Rectangle(7, 7, Color.hsb(random.nextInt(360), 1, 1));
            delays[i] = (long) (Math.random()*duration);
            angles[i] = 2 * Math.PI * random.nextDouble();
        }

        player.getGameBoard().getBoardPane().getChildren().addAll(0, List.of(rectangles));

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                final double width = 0.5 * player.getGameBoard().getBoardWidth();;
                final double height = 0.5 * player.getGameBoard().getBoardWidth();;
                final double radius = Math.sqrt(2) * Math.max(width, height)*2.5;

                for (int i = 0; i < size; i++) {
                    Rectangle r = rectangles[i];
                    double angle = angles[i];
                    long t = (now - delays[i]) % duration;
                    double d = t*radius/duration;
                    r.setOpacity(1);
                    r.setOpacity((duration - t)/(double)duration);
                    r.setTranslateX(Math.cos(angle)*d + width);
                    r.setTranslateY(Math.sin(angle)*d + height);
                }
            }
        }.start();
    }
}
