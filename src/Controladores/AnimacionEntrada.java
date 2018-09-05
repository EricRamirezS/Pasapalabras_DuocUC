package Controladores;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Pasapalabras_DuocUC
 * <p>
 * Created by Eric Ramírez Santis on 31-08-2018.
 * Github Account: https://github.com/EricRamirezS
 */
class AnimacionEntrada {

	private final double[][] puntos = new double[200][2];
	private Audio audio = new Audio();
	private final double r;

	AnimacionEntrada() {
		r = Main.SCREEN.getHeight() / 2 * 0.8;
		for (int i = 0; i < 200; i++) {
			puntos[i] = new double[]{-calcularCircunferenciaX(r, i), -calcularCircunferenciaY(r, i)};
		}
	}

	private double calcularCircunferenciaX(double r, int i) {
		double x;
		x = r * Math.sin(2 * Math.PI / (200) * (i));
		return x;
	}

	private double calcularCircunferenciaY(double r, int i) {
		double y;
		y = r * Math.cos(2 * Math.PI / (200) * (i));
		return y;
	}

	Timeline getAnimacion(ArrayList<CirculoLetra> circulos) {
		Timeline tl = new Timeline();
		tl.getKeyFrames().add(new KeyFrame(Duration.ZERO, e -> audio.playBGM("Entrada")));
		int i = circulos.size();
		for (CirculoLetra circulo : circulos) {
			for (int j = 199; j >= 0; j--) {
				tl.getKeyFrames().add(
						new KeyFrame(Duration.millis((200 - j) * 15 + (i) * 110 + 1000),
								new KeyValue(circulo.translateXProperty(), puntos[199 - j][0]),
								new KeyValue(circulo.translateYProperty(), puntos[199 - j][1])
						)
				);
			}
			tl.getKeyFrames().add(
					new KeyFrame(Duration.millis(201 * 15 + circulos.size() * 110 + 1000),
							new KeyValue(circulo.translateXProperty(), circulo.getTranslateX()),
							new KeyValue(circulo.translateYProperty(), circulo.getTranslateY())
					)
			);
			circulo.setTranslateY(-r - r);
			circulo.setTranslateX(r + r);
			circulo.setVisible(true);
			i--;
		}
		tl.setOnFinished(e -> audio.stopBGM());
		return tl;
	}
}
