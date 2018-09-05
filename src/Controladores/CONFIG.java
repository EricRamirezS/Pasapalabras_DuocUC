package Controladores;

import com.github.sarxos.webcam.Webcam;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Controladores.Main.SCREEN;

/**
 * Pasapalabras_DuocUC
 * <p>
 * Created by Eric Ramírez Santis on 31-08-2018.
 * Github Account: https://github.com/EricRamirezS
 */
public class CONFIG {

	static final List<Character> abc = new ArrayList<>();
	static KeyCode PAUSA = KeyCode.SPACE;
	static KeyCode ERROR = KeyCode.Z;
	static KeyCode CORRECTA = KeyCode.X;
	static KeyCode PASAPALABRA = KeyCode.C;
	static boolean CAMERA = false;
	static boolean TIC_TAC = true;
	static int CAMERA_INDEX = 0;
	static int TIEMPO_MAXIMO = 180;
	static String BACKGROUND_COLOR = Color.web(Color.DARKSLATEGRAY.toString()).toString();


	@FXML
	private CheckBox chkCamara;

	@FXML
	private HBox cameraBox;

	@FXML
	private ComboBox<Webcam> cbCamaras;

	@FXML
	private HBox colorPickerBox;

	@FXML
	private ColorPicker colorPicker;

	@FXML
	private Spinner<Integer> spTiempo;

	@FXML
	private CheckBox chkTicTac;

	@FXML
	private TilePane caracteres;

	@FXML
	private Button btnCorrecta;

	@FXML
	private Button btnIncorrecta;

	@FXML
	private Button btnPasapalabra;

	@FXML
	private Label labelDev;

	@FXML
	void setTecla(ActionEvent event) {

		CustomDialog.showDialog();
		if (CustomDialog.code != null) {
			((Button) event.getSource()).setText(CustomDialog.code.getName());
		}

		if (event.getSource() == btnCorrecta) {
			CORRECTA = CustomDialog.code;
		} else if (event.getSource() == btnIncorrecta) {
			ERROR = CustomDialog.code;
		} else if (event.getSource() == btnPasapalabra) {
			PASAPALABRA = CustomDialog.code;
		} else {
			PAUSA = CustomDialog.code;
		}

	}

	@FXML
	void startGame(ActionEvent ignored) throws IOException {
		abc.clear();
		try {
			Integer.parseInt(spTiempo.getValueFactory().getValue().toString());
			TIEMPO_MAXIMO = spTiempo.getValueFactory().getValue();
		} catch (Exception e) {
			spTiempo.getValueFactory().setValue(180);
			return;
		}
		TIEMPO_MAXIMO = spTiempo.getValue();
		for (Node child : caracteres.getChildren()) {
			CheckBox checkBox = (CheckBox) child;
			if (checkBox.isSelected()) abc.add(checkBox.getText().charAt(0));
		}
		if (abc.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Debe seleccionar al menos una letra.");
			alert.setTitle("Error");
			alert.setGraphic(null);
			alert.setHeaderText(null);
			alert.showAndWait();
			return;
		}
		TIC_TAC = chkTicTac.isSelected();
		BACKGROUND_COLOR = "#" + Integer.toHexString(colorPicker.getValue().hashCode()).substring(0, 6).toUpperCase();
		CAMERA = chkCamara.isSelected();
		CAMERA_INDEX = cbCamaras.getSelectionModel().getSelectedIndex();
		Main.stage.setScene(
				new Scene(FXMLLoader.load(getClass().getResource("mainscreen.fxml")),
						SCREEN.getWidth(),
						SCREEN.getHeight())
		);
		Main.stage.setFullScreen(true);
		Main.stage.show();
	}

	@FXML
	void initialize() {
		cbCamaras.setItems(FXCollections.observableArrayList(WebCamManager.getWebCams()));
		cbCamaras.getSelectionModel().select(0);
		colorPicker.setValue(Color.DARKSLATEGRAY);
		colorPickerBox.visibleProperty().bind(chkCamara.selectedProperty().not());
		cameraBox.visibleProperty().bind(chkCamara.selectedProperty());

		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 180);
		spTiempo.setValueFactory(valueFactory);

		labelDev.setStyle("-fx-text-fill: darkgray");
		labelDev.setText("Desarrollado por Eric Ramírez - https://github.com/EricRamirezS");
	}

}
