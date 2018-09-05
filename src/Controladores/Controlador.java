package Controladores;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Pasapalabras_DuocUC
 * <p>
 * Created by Eric Ramírez Santis on 28-08-2018.
 * Github Account: https://github.com/EricRamirezS
 */
public class Controlador {

	private final DoubleProperty tiempoProperty = new SimpleDoubleProperty(CONFIG.TIEMPO_MAXIMO * 1000);
	private Timeline animacionEntrada;
	private Character current = CONFIG.abc.get(0);
	@FXML
	private ImageView webcam;
	@FXML
	private ProgressIndicator barraTiempoRestante;
	@FXML
	private Label labelTiempo;
	@FXML
	private Label labelRespuestaCorrecta;

	@FXML
	private StackPane root;
	private HashMap<Character, CirculoLetra> circulos;
	private boolean isPlaying = false;
	private Timeline timeline;
	private Audio audio = new Audio();
	@FXML
	private Group g1;
	@FXML
	private Group g2;
	@FXML
	private Group g3;

	@FXML
	void inicializar(ActionEvent event) {
		timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 60), ev -> {
			if (current != null) {
				setTiempoProperty(tiempoProperty.subtract(1000 / 60).get());
			} else {
				isPlaying = false;
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);

		circulos.get(current).setCurrentStatus(CirculoLetra.STATUS.ACTIVA);
		Platform.runLater(() -> Main.stage.getScene().setOnKeyPressed(e -> {
			if (e.getCode() == CONFIG.ERROR && isPlaying) {
				incorrect(null);
			}
			if (e.getCode() == CONFIG.CORRECTA && isPlaying) {
				correct(null);
			}
			if (e.getCode() == CONFIG.PASAPALABRA && isPlaying) {
				skip(null);
			}
			if (e.getCode() == CONFIG.PAUSA) {
				pausar();
			}
		}));

		g1.setVisible(true);
		g2.setVisible(true);
		g3.setVisible(true);
		((Button) event.getSource()).setVisible(false);
		animacionEntrada.play();
	}

	@FXML
	void initialize() {
		Main.stage.setOnCloseRequest(e -> {
			try {
				WebCamManager.getWebCams().get(CONFIG.CAMERA_INDEX).close();
				Main.stage.setScene(null);
				audio.stopBGM();
			} catch (Exception ignore) {
			}
		});
		root.setStyle("-fx-background-color: " + CONFIG.BACKGROUND_COLOR + ";");
		barraTiempoRestante.progressProperty().bind(
				tiempoProperty.subtract(CONFIG.TIEMPO_MAXIMO * 1000).multiply(-1).divide(CONFIG.TIEMPO_MAXIMO * 1000D)
		);
		IntegerProperty intprop = new SimpleIntegerProperty();
		intprop.bind(tiempoProperty.divide(1000));
		labelTiempo.textProperty().bind(
				intprop.asString()
		);
		circulos = new HashMap<>();
		ArrayList<CirculoLetra> circuloLetras = new ArrayList<>();
		double r = Main.SCREEN.getHeight() / 2 * 0.8;
		int i = 0;

		for (char character : CONFIG.abc) {
			CirculoLetra circle = new CirculoLetra(character, this);
			root.getChildren().add(circle);
			circulos.put(character, circle);
			circuloLetras.add(circle);
			double x = calcularCircunferenciaX(r, i);
			double y = calcularCircunferenciaY(r, i);
			circle.setTranslateX(x);
			circle.setTranslateY(-y);
			i++;
			circle.setVisible(false);
		}
		Platform.runLater(() -> {
			webcam.setFitHeight(root.getHeight());
			webcam.setFitWidth(root.getWidth());
			webcam.prefHeight(root.getHeight());
			webcam.prefWidth(root.getWidth());
			webcam.setPreserveRatio(false);
		});
		new WebCamManager(CONFIG.CAMERA_INDEX, webcam);
		animacionEntrada = new AnimacionEntrada().getAnimacion(circuloLetras);
		if (CONFIG.TIC_TAC) audio.playBGM("tictac");
		audio.pauseBGM();
	}

	@FXML
	void correct(ActionEvent event) {
		if (isPlaying) {
			selectNextCircle(CirculoLetra.STATUS.CORRECTA);
			audio.playAudio("Correcta");
		}
	}

	@FXML
	void incorrect(ActionEvent event) {
		if (isPlaying) {
			selectNextCircle(CirculoLetra.STATUS.INCORRECTA);
			pausar();
			audio.playAudio("Error");
		}
	}

	@FXML
	void pause(ActionEvent event) {
		if (current != null) pausar();
	}

	@FXML
	void skip(ActionEvent event) {
		if (isPlaying) {
			selectNextCircle(CirculoLetra.STATUS.PENDIENTE);
			pausar();
			audio.playAudio("Pasapalabras");
		}
	}

	private void setTiempoProperty(double tiempoProperty) {
		Platform.runLater(() -> {
			if (tiempoProperty > 0) {
				this.tiempoProperty.set(tiempoProperty);
			} else {
				this.tiempoProperty.set(0);
				current = null;
				isPlaying = false;
				timeline.stop();
				audio.stopBGM();
				audio.playAudio("gameover");
			}
		});
	}

	private double calcularCircunferenciaX(double r, int i) {
		double x;
		x = r * Math.sin(2 * Math.PI / (CONFIG.abc.size()) * (i));
		return x;
	}

	private double calcularCircunferenciaY(double r, int i) {
		double y;
		y = r * Math.cos(2 * Math.PI / (CONFIG.abc.size()) * (i));
		return y;
	}

	private void updatePuntaje() {
		int correctas = 0;
		for (Map.Entry<Character, CirculoLetra> entry : circulos.entrySet()) {
			CirculoLetra circulo = entry.getValue();
			if (circulo.getStatus() == CirculoLetra.STATUS.CORRECTA) {
				correctas++;
			}
		}
		labelRespuestaCorrecta.setText(correctas + "");
	}

	private void selectNextCircle(CirculoLetra.STATUS newStatus) {
		if (current != null) {
			circulos.get(current).setCurrentStatus(newStatus);
			int index = CONFIG.abc.indexOf(current);
			index++;
			if (index == CONFIG.abc.size()) index = 0;
			current = CONFIG.abc.get(index);
			int laps = 0;
			while (circulos.get(current).getStatus() != CirculoLetra.STATUS.ACTIVA && laps < 10) {
				CirculoLetra.STATUS estado = circulos.get(current).getStatus();
				if (estado != CirculoLetra.STATUS.CORRECTA && estado != CirculoLetra.STATUS.INCORRECTA) {
					circulos.get(current).setCurrentStatus(CirculoLetra.STATUS.ACTIVA);
				} else {
					index++;
					if (index == CONFIG.abc.size()) {
						index = 0;
						laps++;
					}
					current = CONFIG.abc.get(index);
				}
			}
			if (laps >= 10) {
				current = null;
				isPlaying = false;
				timeline.stop();
				audio.stopBGM();
				audio.playAudio("aplausos");
			}
			updatePuntaje();
		}
	}

	private void pausar() {
		if (isPlaying) {
			timeline.pause();
			if (CONFIG.TIC_TAC) audio.pauseBGM();

		} else {
			timeline.play();
			if (CONFIG.TIC_TAC) audio.resumeBGM();

		}
		isPlaying = !isPlaying;
	}
}
