package sample;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Pasapalabras_DuocUC
 * <p>
 * Created by Eric Ramírez Santis on 31-08-2018.
 * Github Account: https://github.com/EricRamirezS
 */
class CustomDialog {

	static KeyCode code;

	static void showDialog() {
		code = null;
		Stage stage = new Stage();

		Parent root = new StackPane();
		((StackPane) root).setPrefSize(300, 100);
		((StackPane) root).getChildren().add(new Label("Presiona una tecla cualquiera\nPresione ESC para cancelar"));
		root.setStyle("" +
				"-fx-padding: 20;" +
				"  -fx-spacing: 10;" +
				"  -fx-alignment: center;" +
				"  -fx-background-color: linear-gradient(to bottom, derive(cadetblue, 20%), cadetblue);" +
				"  -fx-border-color: derive(cadetblue, -20%);" +
				"  -fx-border-width: 5;" +
				"  -fx-background-insets: 12;" +
				"  -fx-border-insets: 10;" +
				"  -fx-border-radius: 6;" +
				"  -fx-background-radius: 6;");
		root.setEffect(new DropShadow());
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(new Scene(root));
		stage.getScene().setFill(Color.TRANSPARENT);
		stage.getScene().setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			} else {
				code = e.getCode();
				stage.close();
			}
		});


		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

}
