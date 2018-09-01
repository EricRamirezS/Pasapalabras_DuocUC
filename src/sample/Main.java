package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    static final Rectangle2D SCREEN = Screen.getPrimary().getBounds();
    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Stage aux = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Setting.fxml"));
        aux.setTitle("Pasapalabras - configuraciones");
        aux.setScene(new Scene(root));
        aux.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
