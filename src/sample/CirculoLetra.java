package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Pasapalabras_DuocUC
 * <p>
 * Created by Eric Ramírez Santis on 31-08-2018.
 * Github Account: https://github.com/EricRamirezS
 */
class CirculoLetra extends Group {

	private final Property<STATUS> currentStatus = new SimpleObjectProperty<>();
	private final Circle circle;
	private byte odd = 0;
	private final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(333), event -> fillCircle()));

	CirculoLetra(char character, Controlador controlador) {
		String caracter = String.valueOf(character);
		StackPane root = new StackPane();
		Label text = new Label(caracter);
		circle = new Circle((35D * Main.SCREEN.getHeight()) / 900);
		Circle circleBorder = new Circle(36D * Main.SCREEN.getHeight() / 900);
		circleBorder.setFill(Color.WHITE);
		text.setStyle("-fx-text-fill: whitesmoke;" +
				"-fx-font-weight: bolder;" +
				"-fx-font-size: " + (32D * Main.SCREEN.getHeight() / 900) + "px;" +
				"-fx-font-family: Arial");
		setCurrentStatus(STATUS.INICIAL);

		root.getChildren().addAll(circleBorder, circle, text);
		getChildren().add(root);

		circle.setOnMouseClicked(e -> {
			switch (currentStatus.getValue()) {
				case PENDIENTE:
					setCurrentStatus(STATUS.INICIAL);
					break;
				case INICIAL:
					setCurrentStatus(STATUS.ACTIVA);
					break;
				case ACTIVA:
					setCurrentStatus(STATUS.CORRECTA);
					break;
				case CORRECTA:
					setCurrentStatus(STATUS.INCORRECTA);
					break;
				default:
					setCurrentStatus(STATUS.PENDIENTE);
			}
		});
	}

	STATUS getStatus() {
		return currentStatus.getValue();

	}

	void setCurrentStatus(STATUS status) {
		currentStatus.setValue(status);
		fillCircle();
	}

	private void fillCircle() {
		timeline.stop();
		if (currentStatus.getValue() == STATUS.ACTIVA) {
			if (odd % 2 == 0) {
				Stop[] stops = new Stop[]{new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.BLUE)};
				LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
				circle.setFill(lg);
				odd++;
			} else {
				Stop[] stops = new Stop[]{new Stop(0, Color.BLUE), new Stop(1, Color.DARKBLUE)};
				LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
				circle.setFill(lg);
				odd = 0;
			}
			timeline.play();
		} else if (currentStatus.getValue() == STATUS.INICIAL) {
			Stop[] stops = new Stop[]{new Stop(0, Color.BLUE), new Stop(1, Color.DARKBLUE)};
			LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
			circle.setFill(lg);
		} else if (currentStatus.getValue() == STATUS.PENDIENTE) {
			Stop[] stops = new Stop[]{new Stop(0, new Color(1, 139D / 255, 0, 1)), new Stop(1, new Color(130D / 255, 67D / 255, 0, 1))};
			LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
			circle.setFill(lg);
		} else if (currentStatus.getValue() == STATUS.CORRECTA) {
			Stop[] stops = new Stop[]{new Stop(0, new Color(0, 255D / 255, 0, 1)), new Stop(1, new Color(0, 130D / 255, 0, 1))};
			LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
			circle.setFill(lg);
		} else {
			Stop[] stops = new Stop[]{new Stop(0, Color.RED), new Stop(1, Color.DARKRED)};
			LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
			circle.setFill(lg);
		}
	}

	enum STATUS {PENDIENTE, CORRECTA, INCORRECTA, INICIAL, ACTIVA}
}
